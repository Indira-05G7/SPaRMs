package model;

import java.sql.Timestamp;

public class Notification {
    private int id;
    private String userRole; // ADMIN, RECRUITER, STUDENT
    private Integer userId; // Nullable
    private String title;
    private String message;
    private Timestamp createdAt;
    private boolean isRead;

    public Notification() {}

    public Notification(int id, String userRole, Integer userId, String title, String message, Timestamp createdAt, boolean isRead) {
        this.id = id;
        this.userRole = userRole;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
