package dao;

import database.DatabaseConnection;
import model.Notification;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    
    public List<Notification> getForUser(String role, Integer userId) throws Exception {
        List<Notification> list = new ArrayList<>();
        // Query broadcasts (userId is NULL) or target role and target user
        String sql = "SELECT * FROM notifications " +
                     "WHERE (user_role = ? AND (user_id IS NULL OR user_id = ?)) " +
                     "ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, role);
            if (userId != null) {
                pstmt.setInt(2, userId);
            } else {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Notification(
                        rs.getInt("id"),
                        rs.getString("user_role"),
                        rs.getObject("user_id") != null ? rs.getInt("user_id") : null,
                        rs.getString("title"),
                        rs.getString("message"),
                        rs.getTimestamp("created_at"),
                        rs.getBoolean("is_read")
                    ));
                }
            }
        }
        return list;
    }

    public boolean save(Notification notification) throws Exception {
        String sql = "INSERT INTO notifications (user_role, user_id, title, message, is_read) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, notification.getUserRole());
            if (notification.getUserId() != null) {
                pstmt.setInt(2, notification.getUserId());
            } else {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            }
            pstmt.setString(3, notification.getTitle());
            pstmt.setString(4, notification.getMessage());
            pstmt.setBoolean(5, notification.isRead());
            
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        notification.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean markAsRead(int id) throws Exception {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }
}
