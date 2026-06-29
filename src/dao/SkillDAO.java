package dao;

import database.DatabaseConnection;
import model.Skill;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SkillDAO {
    
    public List<Skill> getAll() throws Exception {
        List<Skill> list = new ArrayList<>();
        String sql = "SELECT * FROM skills ORDER BY name";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Skill(rs.getInt("id"), rs.getString("name")));
            }
        }
        return list;
    }

    public Skill getById(int id) throws Exception {
        String sql = "SELECT * FROM skills WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Skill(rs.getInt("id"), rs.getString("name"));
                }
            }
        }
        return null;
    }

    public List<Skill> getForStudent(int studentId) throws Exception {
        List<Skill> list = new ArrayList<>();
        String sql = "SELECT s.* FROM skills s " +
                     "JOIN student_skills ss ON s.id = ss.skill_id " +
                     "WHERE ss.student_id = ? ORDER BY s.name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Skill(rs.getInt("id"), rs.getString("name")));
                }
            }
        }
        return list;
    }

    public void saveStudentSkills(int studentId, List<Skill> skills) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // First delete existing student skills
                String deleteSql = "DELETE FROM student_skills WHERE student_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                    pstmt.setInt(1, studentId);
                    pstmt.executeUpdate();
                }

                // Now insert selected student skills
                if (skills != null && !skills.isEmpty()) {
                    String insertSql = "INSERT INTO student_skills (student_id, skill_id) VALUES (?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                        for (Skill s : skills) {
                            pstmt.setInt(1, studentId);
                            pstmt.setInt(2, s.getId());
                            pstmt.addBatch();
                        }
                        pstmt.executeBatch();
                    }
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
