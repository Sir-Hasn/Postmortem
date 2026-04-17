package postmortem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import postmortem.util.AuthUtils;
import postmortem.util.DatabaseUtil;

/**
 * Data Access Object for User operations.
 */
public class UserDAO {
    static {
        createTableIfNotExists();
    }

    /**
     * Get a database connection.
     */
    public static Connection getConnection() throws SQLException {
        return DatabaseUtil.connect();
    }

    private static void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "UserName TEXT UNIQUE NOT NULL," +
                "PasswordHash TEXT NOT NULL," +
                "Salt TEXT NOT NULL," +
                "CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating users table: " + e.getMessage());
        }
    }

    /**
     * Create a new user with username and password.
     * Returns true if successful, false if username already exists.
     */
    public static boolean createUser(String username, char[] password) throws Exception {
        String hashedPassword = AuthUtils.hashPassword(password);
        String[] parts = hashedPassword.split(":");
        String salt = parts[0];
        String hash = parts[1];

        String sql = "INSERT INTO users (UserName, PasswordHash, Salt) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, hash);
            ps.setString(3, salt);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                return false; // Username already exists
            }
            throw e;
        }
    }

    /**
     * Authenticate a user by username and password.
     * Returns true if credentials are valid, false otherwise.
     */
    public static boolean authenticate(String username, char[] password) throws Exception {
        String sql = "SELECT PasswordHash, Salt FROM users WHERE UserName = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return false; // User not found
                }

                String hash = rs.getString("PasswordHash");
                String salt = rs.getString("Salt");
                String storedHash = salt + ":" + hash;

                return AuthUtils.verifyPassword(password, storedHash);
            }
        }
    }

    /**
     * Check if a username already exists.
     */
    public static boolean userExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE UserName = ? LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Get the current logged-in user ID by username.
     */
    public static int getUserId(String username) throws SQLException {
        String sql = "SELECT Id FROM users WHERE UserName = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Id");
                }
                return -1; // User not found
            }
        }
    }

    /**
     * Update username for a user ID.
     * Returns true on success, false when the username already exists.
     */
    public static boolean updateUsername(int userId, String newUsername) throws SQLException {
        String sql = "UPDATE users SET UserName = ? WHERE Id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newUsername);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                return false;
            }
            throw e;
        }
    }

    /**
     * Update password for a user ID.
     */
    public static boolean updatePasswordByUserId(int userId, char[] newPassword) throws Exception {
        String hashedPassword = AuthUtils.hashPassword(newPassword);
        String[] parts = hashedPassword.split(":");
        String salt = parts[0];
        String hash = parts[1];

        String sql = "UPDATE users SET PasswordHash = ?, Salt = ? WHERE Id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, hash);
            ps.setString(2, salt);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        }
    }
}
