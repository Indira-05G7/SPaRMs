package dao;

import database.DatabaseConnection;
import model.Application;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDAO {
    
    public List<Application> getAll() throws Exception {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT a.*, s.name as student_name, s.roll_number as student_roll, s.cgpa as student_cgpa, " +
                     "pd.title as drive_title, pd.job_role, pd.package_lpa, c.name as company_name " +
                     "FROM applications a " +
                     "JOIN students s ON a.student_id = s.id " +
                     "JOIN placement_drives pd ON a.drive_id = pd.id " +
                     "JOIN companies c ON pd.company_id = c.id " +
                     "ORDER BY a.applied_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Application> getByStudent(int studentId) throws Exception {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT a.*, s.name as student_name, s.roll_number as student_roll, s.cgpa as student_cgpa, " +
                     "pd.title as drive_title, pd.job_role, pd.package_lpa, c.name as company_name " +
                     "FROM applications a " +
                     "JOIN students s ON a.student_id = s.id " +
                     "JOIN placement_drives pd ON a.drive_id = pd.id " +
                     "JOIN companies c ON pd.company_id = c.id " +
                     "WHERE a.student_id = ? " +
                     "ORDER BY a.applied_date DESC";
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

    public List<Application> getByDrive(int driveId) throws Exception {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT a.*, s.name as student_name, s.roll_number as student_roll, s.cgpa as student_cgpa, " +
                     "pd.title as drive_title, pd.job_role, pd.package_lpa, c.name as company_name " +
                     "FROM applications a " +
                     "JOIN students s ON a.student_id = s.id " +
                     "JOIN placement_drives pd ON a.drive_id = pd.id " +
                     "JOIN companies c ON pd.company_id = c.id " +
                     "WHERE a.drive_id = ? " +
                     "ORDER BY a.applied_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, driveId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public List<Application> getByCompany(int companyId) throws Exception {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT a.*, s.name as student_name, s.roll_number as student_roll, s.cgpa as student_cgpa, " +
                     "pd.title as drive_title, pd.job_role, pd.package_lpa, c.name as company_name " +
                     "FROM applications a " +
                     "JOIN students s ON a.student_id = s.id " +
                     "JOIN placement_drives pd ON a.drive_id = pd.id " +
                     "JOIN companies c ON pd.company_id = c.id " +
                     "WHERE pd.company_id = ? " +
                     "ORDER BY a.applied_date DESC";
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

    public Application getById(int id) throws Exception {
        String sql = "SELECT a.*, s.name as student_name, s.roll_number as student_roll, s.cgpa as student_cgpa, " +
                     "pd.title as drive_title, pd.job_role, pd.package_lpa, c.name as company_name " +
                     "FROM applications a " +
                     "JOIN students s ON a.student_id = s.id " +
                     "JOIN placement_drives pd ON a.drive_id = pd.id " +
                     "JOIN companies c ON pd.company_id = c.id " +
                     "WHERE a.id = ?";
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

    public boolean save(Application app) throws Exception {
        String sql = "INSERT INTO applications (student_id, drive_id, status) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, app.getStudentId());
            pstmt.setInt(2, app.getDriveId());
            pstmt.setString(3, app.getStatus());
            
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        app.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean updateStatus(int id, String status) throws Exception {
        String sql = "UPDATE applications SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean exists(int studentId, int driveId) throws Exception {
        String sql = "SELECT 1 FROM applications WHERE student_id = ? AND drive_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, driveId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Application mapRow(ResultSet rs) throws SQLException {
        Application app = new Application(
            rs.getInt("id"),
            rs.getInt("student_id"),
            rs.getInt("drive_id"),
            rs.getTimestamp("applied_date"),
            rs.getString("status")
        );
        app.setStudentName(rs.getString("student_name"));
        app.setStudentRoll(rs.getString("student_roll"));
        app.setStudentCgpa(rs.getDouble("student_cgpa"));
        app.setDriveTitle(rs.getString("drive_title"));
        app.setCompanyName(rs.getString("company_name"));
        app.setJobRole(rs.getString("job_role"));
        app.setPackageLpa(rs.getDouble("package_lpa"));
        return app;
    }
}
