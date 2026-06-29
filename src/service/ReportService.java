package service;

import database.DatabaseConnection;
import exception.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportService {
    
    public List<Map<String, Object>> getDepartmentWisePlacements() throws DatabaseException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT d.name as dept_name, " +
                     "COUNT(s.id) as total_students, " +
                     "SUM(CASE WHEN sel.id IS NOT NULL AND sel.status = 'ACCEPTED' THEN 1 ELSE 0 END) as placed_students " +
                     "FROM departments d " +
                     "LEFT JOIN students s ON s.department_id = d.id " +
                     "LEFT JOIN selections sel ON sel.student_id = s.id " +
                     "GROUP BY d.id, d.name " +
                     "ORDER BY dept_name";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                int total = rs.getInt("total_students");
                int placed = rs.getInt("placed_students");
                double pct = total > 0 ? ((double) placed / total) * 100.0 : 0.0;
                
                map.put("department", rs.getString("dept_name"));
                map.put("total", total);
                map.put("placed", placed);
                map.put("percentage", pct);
                list.add(map);
            }
        } catch (Exception e) {
            throw new DatabaseException("Failed to get department placements report", e);
        }
        return list;
    }

    public List<Map<String, Object>> getCompanyWiseHiring() throws DatabaseException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT c.name as company_name, " +
                     "COUNT(sel.id) as hired_count, " +
                     "MAX(sel.offered_package_lpa) as max_package, " +
                     "AVG(sel.offered_package_lpa) as avg_package " +
                     "FROM companies c " +
                     "JOIN placement_drives pd ON pd.company_id = c.id " +
                     "JOIN selections sel ON sel.drive_id = pd.id " +
                     "WHERE sel.status = 'ACCEPTED' " +
                     "GROUP BY c.id, c.name " +
                     "ORDER BY hired_count DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("company", rs.getString("company_name"));
                map.put("hired", rs.getInt("hired_count"));
                map.put("maxPackage", rs.getDouble("max_package"));
                map.put("avgPackage", rs.getDouble("avg_package"));
                list.add(map);
            }
        } catch (Exception e) {
            throw new DatabaseException("Failed to get company hiring report", e);
        }
        return list;
    }

    public List<Map<String, Object>> getDriveWiseStatistics() throws DatabaseException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT pd.title as drive_title, c.name as company_name, pd.job_role, " +
                     "COUNT(a.id) as total_applicants, " +
                     "SUM(CASE WHEN a.status = 'SHORTLISTED' THEN 1 ELSE 0 END) as shortlisted, " +
                     "SUM(CASE WHEN a.status = 'SELECTED' THEN 1 ELSE 0 END) as selected, " +
                     "SUM(CASE WHEN a.status = 'REJECTED' THEN 1 ELSE 0 END) as rejected " +
                     "FROM placement_drives pd " +
                     "JOIN companies c ON pd.company_id = c.id " +
                     "LEFT JOIN applications a ON a.drive_id = pd.id " +
                     "GROUP BY pd.id, pd.title, c.name, pd.job_role " +
                     "ORDER BY pd.drive_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("driveTitle", rs.getString("drive_title"));
                map.put("company", rs.getString("company_name"));
                map.put("role", rs.getString("job_role"));
                map.put("applicants", rs.getInt("total_applicants"));
                map.put("shortlisted", rs.getInt("shortlisted"));
                map.put("selected", rs.getInt("selected"));
                map.put("rejected", rs.getInt("rejected"));
                list.add(map);
            }
        } catch (Exception e) {
            throw new DatabaseException("Failed to get drive statistics report", e);
        }
        return list;
    }

    public List<Map<String, Object>> getEligibleStudentsForDrive(int driveId) throws DatabaseException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT s.name, s.roll_number, s.cgpa, s.active_backlogs, d.name as dept_name " +
                     "FROM students s " +
                     "LEFT JOIN departments d ON s.department_id = d.id " +
                     "CROSS JOIN placement_drives pd " +
                     "WHERE pd.id = ? AND s.cgpa >= pd.min_cgpa AND s.active_backlogs <= pd.max_backlogs " +
                     "ORDER BY s.cgpa DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, driveId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", rs.getString("name"));
                    map.put("rollNumber", rs.getString("roll_number"));
                    map.put("cgpa", rs.getDouble("cgpa"));
                    map.put("backlogs", rs.getInt("active_backlogs"));
                    map.put("department", rs.getString("dept_name"));
                    list.add(map);
                }
            }
        } catch (Exception e) {
            throw new DatabaseException("Failed to get eligible students report", e);
        }
        return list;
    }
}
