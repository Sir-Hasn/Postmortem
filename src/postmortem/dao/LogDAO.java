package postmortem.dao;

import postmortem.model.BugEntry;
import postmortem.util.DatabaseUtil;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class LogDAO {
    private static final String TABLE_NAME = "bug_logs";
    private static final Set<String> ALLOWED_STATUS = Set.of("OPEN", "RESOLVED");

    public LogDAO() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "title TEXT NOT NULL," +
                "error_message TEXT," +
                "error_context TEXT," +
                "solution TEXT," +
                "tags TEXT," +
                "status TEXT NOT NULL DEFAULT 'OPEN' CHECK(status IN ('OPEN','RESOLVED'))," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY(user_id) REFERENCES users(id)" +
                ")";
        try (Connection conn = DatabaseUtil.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
        }
    }

    public int insertLog(BugEntry entry) {
        String status = normalizeAndValidateStatus(entry.getStatus());
        String sql = "INSERT INTO " + TABLE_NAME +
                "(user_id, title, error_message, error_context, solution, tags, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, entry.getUserId());
            pstmt.setString(2, entry.getTitle());
            pstmt.setString(3, entry.getErrorMessage());
            pstmt.setString(4, entry.getErrorContext());
            pstmt.setString(5, entry.getSolution());
            pstmt.setString(6, entry.getTags());
            pstmt.setString(7, status);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error inserting log: " + e.getMessage());
        }
        return -1;
    }

    public List<BugEntry> getAllLogsByUser(int userId) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE user_id = ? ORDER BY created_at DESC";
        List<BugEntry> logs = new ArrayList<>();
        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                logs.add(mapResultSetToBugEntry(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching logs: " + e.getMessage());
        }
        return logs;
    }

    public BugEntry getLogById(int id) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToBugEntry(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching log: " + e.getMessage());
        }
        return null;
    }

    public boolean updateLog(BugEntry entry) {
        String status = normalizeAndValidateStatus(entry.getStatus());
        String sql = "UPDATE " + TABLE_NAME +
                " SET title = ?, error_message = ?, error_context = ?, solution = ?, tags = ?, status = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE id = ?";
        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entry.getTitle());
            pstmt.setString(2, entry.getErrorMessage());
            pstmt.setString(3, entry.getErrorContext());
            pstmt.setString(4, entry.getSolution());
            pstmt.setString(5, entry.getTags());
            pstmt.setString(6, status);
            pstmt.setInt(7, entry.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating log: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteLog(int id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting log: " + e.getMessage());
        }
        return false;
    }

    public List<BugEntry> searchLogs(int userId, String query, String statusFilter, String tagFilter) {
        StringBuilder sql = new StringBuilder("SELECT * FROM " + TABLE_NAME + " WHERE user_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (query != null && !query.isEmpty()) {
            sql.append(" AND (title LIKE ? OR error_message LIKE ? OR tags LIKE ?)");
            String searchTerm = "%" + query + "%";
            params.add(searchTerm);
            params.add(searchTerm);
            params.add(searchTerm);
        }

        if (statusFilter != null && !statusFilter.isEmpty() && !statusFilter.equals("ALL")) {
            String validatedFilter = normalizeAndValidateStatus(statusFilter);
            sql.append(" AND status = ?");
            params.add(validatedFilter);
        }

        if (tagFilter != null && !tagFilter.isEmpty() && !tagFilter.equals("ALL")) {
            sql.append(" AND tags LIKE ?");
            params.add("%" + tagFilter + "%");
        }

        sql.append(" ORDER BY created_at DESC");

        List<BugEntry> results = new ArrayList<>();
        try (Connection conn = DatabaseUtil.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                results.add(mapResultSetToBugEntry(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching logs: " + e.getMessage());
        }
        return results;
    }

    private BugEntry mapResultSetToBugEntry(ResultSet rs) throws SQLException {
        BugEntry entry = new BugEntry();
        entry.setId(rs.getInt("id"));
        entry.setUserId(rs.getInt("user_id"));
        entry.setTitle(rs.getString("title"));
        entry.setErrorMessage(rs.getString("error_message"));
        entry.setErrorContext(rs.getString("error_context"));
        entry.setSolution(rs.getString("solution"));
        entry.setTags(rs.getString("tags"));
        entry.setStatus(rs.getString("status"));
        Timestamp createdTs = rs.getTimestamp("created_at");
        Timestamp updatedTs = rs.getTimestamp("updated_at");
        entry.setCreatedAt(createdTs == null ? null : createdTs.toLocalDateTime());
        entry.setUpdatedAt(updatedTs == null ? null : updatedTs.toLocalDateTime());
        return entry;
    }

    private String normalizeAndValidateStatus(String status) {
        String normalized = status == null ? "OPEN" : status.trim().toUpperCase(Locale.ROOT);
        if (!ALLOWED_STATUS.contains(normalized)) {
            throw new IllegalArgumentException("Invalid status. Only OPEN or RESOLVED are allowed.");
        }
        return normalized;
    }
}
