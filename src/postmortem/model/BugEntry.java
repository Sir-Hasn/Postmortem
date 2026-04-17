package postmortem.model;

import java.time.LocalDateTime;
import java.util.List;

public class BugEntry {
    private int id;
    private int userId;
    private String title;
    private String errorMessage;
    private String errorContext;
    private String solution;
    private String tags; // comma-separated
    private String status; // OPEN, RESOLVED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BugEntry() {
        this.status = "OPEN";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public BugEntry(int userId, String title, String errorMessage, String errorContext, String tags) {
        this();
        this.userId = userId;
        this.title = title;
        this.errorMessage = errorMessage;
        this.errorContext = errorContext;
        this.tags = tags;
        this.solution = "";
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getErrorContext() { return errorContext; }
    public void setErrorContext(String errorContext) { this.errorContext = errorContext; }

    public String getSolution() { return solution; }
    public void setSolution(String solution) { this.solution = solution; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<String> getTagList() {
        return tags == null ? List.of() : List.of(tags.split(",\\s*"));
    }

    @Override
    public String toString() {
        return "BugEntry{" + "id=" + id + ", title='" + title + '\'' + ", status='" + status + '\'' + '}';
    }
}
