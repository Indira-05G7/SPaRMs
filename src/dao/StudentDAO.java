package dao;

import database.DatabaseConnection;
import model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public List<Student> getAll() throws Exception {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY name";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }
    
    public Student getByUsername(String username) throws Exception {
        String sql = "SELECT * FROM students WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public Student getById(int id) throws Exception {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public boolean save(Student student) throws Exception {
        String sql = "INSERT INTO students (roll_number, username, password, email, name, phone, department_id, cgpa, tenth_percentage, twelfth_percentage, active_backlogs, resume_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, student.getRollNumber());
            pstmt.setString(2, student.getUsername());
            pstmt.setString(3, student.getPassword());
            pstmt.setString(4, student.getEmail());
            pstmt.setString(5, student.getName());
            pstmt.setString(6, student.getPhone());
            if (student.getDepartmentId() > 0) {
                pstmt.setInt(7, student.getDepartmentId());
            } else {
                pstmt.setNull(7, java.sql.Types.INTEGER);
            }
            pstmt.setDouble(8, student.getCgpa());
            pstmt.setDouble(9, student.getTenthPercentage());
            pstmt.setDouble(10, student.getTwelfthPercentage());
            pstmt.setInt(11, student.getActiveBacklogs());
            pstmt.setString(12, student.getResumeUrl());
            
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        student.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean update(Student student) throws Exception {
        String sql = "UPDATE students SET roll_number = ?, email = ?, name = ?, phone = ?, department_id = ?, cgpa = ?, tenth_percentage = ?, twelfth_percentage = ?, active_backlogs = ?, resume_url = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getRollNumber());
            pstmt.setString(2, student.getEmail());
            pstmt.setString(3, student.getName());
            pstmt.setString(4, student.getPhone());
            if (student.getDepartmentId() > 0) {
                pstmt.setInt(5, student.getDepartmentId());
            } else {
                pstmt.setNull(5, java.sql.Types.INTEGER);
            }
            pstmt.setDouble(6, student.getCgpa());
            pstmt.setDouble(7, student.getTenthPercentage());
            pstmt.setDouble(8, student.getTwelfthPercentage());
            pstmt.setInt(9, student.getActiveBacklogs());
            pstmt.setString(10, student.getResumeUrl());
            pstmt.setInt(11, student.getId());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        return new Student(
            rs.getInt("id"),
            rs.getString("roll_number"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("email"),
            rs.getString("name"),
            rs.getString("phone"),
            rs.getInt("department_id"),
            rs.getDouble("cgpa"),
            rs.getDouble("tenth_percentage"),
            rs.getDouble("twelfth_percentage"),
            rs.getInt("active_backlogs"),
            rs.getString("resume_url")
        );
    }
}
