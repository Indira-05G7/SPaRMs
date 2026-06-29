package dao;

import database.DatabaseConnection;
import model.Selection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SelectionDAO {
    
    public List<Selection> getAll() throws Exception {
        List<Selection> list = new ArrayList<>();
        String sql = "SELECT sel.*, s.name as student_name, s.roll_number as student_roll, d.name as department_name, " +
                     "pd.title as drive_title, pd.job_role, c.name as company_name " +
                     "FROM selections sel " +
                     "JOIN students s ON sel.student_id = s.id " +
                     "LEFT JOIN departments d ON s.department_id = d.id " +
                     "JOIN placement_drives pd ON sel.drive_id = pd.id " +
                     "JOIN companies c ON pd.company_id = c.id " +
                     "ORDER BY sel.selection_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Selection> getByStudent(int studentId) throws Exception {
        List<Selection> list = new ArrayList<>();
        String sql = "SELECT sel.*, s.name as student_name, s.roll_number as student_roll, d.name as department_name, " +
                     "pd.title as drive_title, pd.job_role, c.name as company_name " +
                     "FROM selections sel " +
                     "JOIN students s ON sel.student_id = s.id " +
                     "LEFT JOIN departments d ON s.department_id = d.id " +
                     "JOIN placement_drives pd ON sel.drive_id = pd.id " +
                     "JOIN companies c ON pd.company_id = c.id " +
                     "WHERE sel.student_id = ? " +
                     "ORDER BY sel.selection_date DESC";
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

    public List<Selection> getByCompany(int companyId) throws Exception {
        List<Selection> list = new ArrayList<>();
        String sql = "SELECT sel.*, s.name as student_name, s.roll_number as student_roll, d.name as department_name, " +
                     "pd.title as drive_title, pd.job_role, c.name as company_name " +
                     "FROM selections sel " +
                     "JOIN students s ON sel.student_id = s.id " +
                     "LEFT JOIN departments d ON s.department_id = d.id " +
                     "JOIN placement_drives pd ON sel.drive_id = pd.id " +
                     "JOIN companies c ON pd.company_id = c.id " +
                     "WHERE pd.company_id = ? " +
                     "ORDER BY sel.selection_date DESC";
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

    public boolean save(Selection sel) throws Exception {
        String sql = "INSERT INTO selections (student_id, drive_id, application_id, offered_package_lpa, selection_date, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, sel.getStudentId());
            pstmt.setInt(2, sel.getDriveId());
            pstmt.setInt(3, sel.getApplicationId());
            pstmt.setDouble(4, sel.getOfferedPackageLpa());
            pstmt.setDate(5, sel.getSelectionDate());
            pstmt.setString(6, sel.getStatus());
            
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        sel.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean updateStatus(int id, String status) throws Exception {
        String sql = "UPDATE selections SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Selection mapRow(ResultSet rs) throws SQLException {
        Selection sel = new Selection(
            rs.getInt("id"),
            rs.getInt("student_id"),
            rs.getInt("drive_id"),
            rs.getInt("application_id"),
            rs.getDouble("offered_package_lpa"),
            rs.getDate("selection_date"),
            rs.getString("status")
        );
        sel.setStudentName(rs.getString("student_name"));
        sel.setStudentRoll(rs.getString("student_roll"));
        sel.setDepartmentName(rs.getString("department_name"));
        sel.setDriveTitle(rs.getString("drive_title"));
        sel.setCompanyName(rs.getString("company_name"));
        sel.setJobRole(rs.getString("job_role"));
        return sel;
    }
}
