package dao;

import database.DatabaseConnection;
import model.Recruiter;
import java.sql.*;

public class RecruiterDAO {
    
    public Recruiter getByUsername(String username) throws Exception {
        String sql = "SELECT * FROM recruiters WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Recruiter(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("name"),
                        rs.getInt("company_id")
                    );
                }
            }
        }
        return null;
    }

    public Recruiter getById(int id) throws Exception {
        String sql = "SELECT * FROM recruiters WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Recruiter(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("name"),
                        rs.getInt("company_id")
                    );
                }
            }
        }
        return null;
    }

    public boolean save(Recruiter rec) throws Exception {
        String sql = "INSERT INTO recruiters (username, password, email, name, company_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, rec.getUsername());
            pstmt.setString(2, rec.getPassword());
            pstmt.setString(3, rec.getEmail());
            pstmt.setString(4, rec.getName());
            pstmt.setInt(5, rec.getCompanyId());
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        rec.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }
}
