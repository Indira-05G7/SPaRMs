package dao;

import database.DatabaseConnection;
import model.PlacementDrive;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlacementDriveDAO {
    
    public List<PlacementDrive> getAll() throws Exception {
        List<PlacementDrive> list = new ArrayList<>();
        String sql = "SELECT pd.*, c.name as company_name FROM placement_drives pd " +
                     "JOIN companies c ON pd.company_id = c.id " +
                     "ORDER BY pd.drive_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<PlacementDrive> getDrivesByCompany(int companyId) throws Exception {
        List<PlacementDrive> list = new ArrayList<>();
        String sql = "SELECT pd.*, c.name as company_name FROM placement_drives pd " +
                     "JOIN companies c ON pd.company_id = c.id " +
                     "WHERE pd.company_id = ? " +
                     "ORDER BY pd.drive_date DESC";
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

    public PlacementDrive getById(int id) throws Exception {
        String sql = "SELECT pd.*, c.name as company_name FROM placement_drives pd " +
                     "JOIN companies c ON pd.company_id = c.id " +
                     "WHERE pd.id = ?";
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

    public boolean save(PlacementDrive drive) throws Exception {
        String sql = "INSERT INTO placement_drives (company_id, title, job_role, job_description, package_lpa, min_cgpa, max_backlogs, drive_date, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, drive.getCompanyId());
            pstmt.setString(2, drive.getTitle());
            pstmt.setString(3, drive.getJobRole());
            pstmt.setString(4, drive.getJobDescription());
            pstmt.setDouble(5, drive.getPackageLpa());
            pstmt.setDouble(6, drive.getMinCgpa());
            pstmt.setInt(7, drive.getMaxBacklogs());
            pstmt.setDate(8, drive.getDriveDate());
            pstmt.setString(9, drive.getStatus());
            
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        drive.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean update(PlacementDrive drive) throws Exception {
        String sql = "UPDATE placement_drives SET title = ?, job_role = ?, job_description = ?, package_lpa = ?, min_cgpa = ?, max_backlogs = ?, drive_date = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, drive.getTitle());
            pstmt.setString(2, drive.getJobRole());
            pstmt.setString(3, drive.getJobDescription());
            pstmt.setDouble(4, drive.getPackageLpa());
            pstmt.setDouble(5, drive.getMinCgpa());
            pstmt.setInt(6, drive.getMaxBacklogs());
            pstmt.setDate(7, drive.getDriveDate());
            pstmt.setString(8, drive.getStatus());
            pstmt.setInt(9, drive.getId());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    private PlacementDrive mapRow(ResultSet rs) throws SQLException {
        PlacementDrive drive = new PlacementDrive(
            rs.getInt("id"),
            rs.getInt("company_id"),
            rs.getString("title"),
            rs.getString("job_role"),
            rs.getString("job_description"),
            rs.getDouble("package_lpa"),
            rs.getDouble("min_cgpa"),
            rs.getInt("max_backlogs"),
            rs.getDate("drive_date"),
            rs.getString("status")
        );
        drive.setCompanyName(rs.getString("company_name"));
        return drive;
    }
}
