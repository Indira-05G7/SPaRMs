package dao;

import database.DatabaseConnection;
import model.InterviewRound;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InterviewRoundDAO {
    
    public List<InterviewRound> getByApplication(int applicationId) throws Exception {
        List<InterviewRound> list = new ArrayList<>();
        String sql = "SELECT ir.*, s.name as student_name, pd.title as drive_title, c.name as company_name " +
                     "FROM interview_rounds ir " +
                     "JOIN applications a ON ir.application_id = a.id " +
                     "JOIN students s ON a.student_id = s.id " +
                     "JOIN placement_drives pd ON a.drive_id = pd.id " +
                     "JOIN companies c ON pd.company_id = c.id " +
                     "WHERE ir.application_id = ? " +
                     "ORDER BY ir.round_number ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, applicationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public List<InterviewRound> getForStudent(int studentId) throws Exception {
        List<InterviewRound> list = new ArrayList<>();
        String sql = "SELECT ir.*, s.name as student_name, pd.title as drive_title, c.name as company_name " +
                     "FROM interview_rounds ir " +
                     "JOIN applications a ON ir.application_id = a.id " +
                     "JOIN students s ON a.student_id = s.id " +
                     "JOIN placement_drives pd ON a.drive_id = pd.id " +
                     "JOIN companies c ON pd.company_id = c.id " +
                     "WHERE a.student_id = ? " +
                     "ORDER BY ir.round_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public List<InterviewRound> getForCompany(int companyId) throws Exception {
        List<InterviewRound> list = new ArrayList<>();
        String sql = "SELECT ir.*, s.name as student_name, pd.title as drive_title, c.name as company_name " +
                     "FROM interview_rounds ir " +
                     "JOIN applications a ON ir.application_id = a.id " +
                     "JOIN students s ON a.student_id = s.id " +
                     "JOIN placement_drives pd ON a.drive_id = pd.id " +
                     "JOIN companies c ON pd.company_id = c.id " +
                     "WHERE pd.company_id = ? " +
                     "ORDER BY ir.round_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, companyId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public boolean save(InterviewRound round) throws Exception {
        String sql = "INSERT INTO interview_rounds (application_id, round_number, round_name, round_date, status, feedback) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, round.getApplicationId());
            pstmt.setInt(2, round.getRoundNumber());
            pstmt.setString(3, round.getRoundName());
            pstmt.setTimestamp(4, round.getRoundDate());
            pstmt.setString(5, round.getStatus());
            pstmt.setString(6, round.getFeedback());
            
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        round.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean update(InterviewRound round) throws Exception {
        String sql = "UPDATE interview_rounds SET round_name = ?, round_date = ?, status = ?, feedback = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, round.getRoundName());
            pstmt.setTimestamp(2, round.getRoundDate());
            pstmt.setString(3, round.getStatus());
            pstmt.setString(4, round.getFeedback());
            pstmt.setInt(5, round.getId());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    private InterviewRound mapRow(ResultSet rs) throws SQLException {
        InterviewRound ir = new InterviewRound(
            rs.getInt("id"),
            rs.getInt("application_id"),
            rs.getInt("round_number"),
            rs.getString("round_name"),
            rs.getTimestamp("round_date"),
            rs.getString("status"),
            rs.getString("feedback")
        );
        ir.setStudentName(rs.getString("student_name"));
        ir.setDriveTitle(rs.getString("drive_title"));
        ir.setCompanyName(rs.getString("company_name"));
        return ir;
    }
}
