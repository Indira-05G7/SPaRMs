package service;

import database.DatabaseConnection;
import exception.DatabaseException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class AdminService {
    
    public Map<String, Object> getDashboardStats() throws DatabaseException {
        Map<String, Object> stats = new HashMap<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Total Students
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM students")) {
                stats.put("totalStudents", rs.next() ? rs.getInt(1) : 0);
            }
            
            // Total Companies
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM companies")) {
                stats.put("totalCompanies", rs.next() ? rs.getInt(1) : 0);
            }
            
            // Total Placement Drives
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM placement_drives")) {
                stats.put("totalDrives", rs.next() ? rs.getInt(1) : 0);
            }
            
            // Total Applications
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM applications")) {
                stats.put("totalApplications", rs.next() ? rs.getInt(1) : 0);
            }
            
            // Total Selected Students
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM selections WHERE status = 'ACCEPTED'")) {
                stats.put("totalSelected", rs.next() ? rs.getInt(1) : 0);
            }
            
            // Highest Package
            try (ResultSet rs = stmt.executeQuery("SELECT MAX(offered_package_lpa) FROM selections WHERE status = 'ACCEPTED'")) {
                stats.put("highestPackage", rs.next() ? rs.getDouble(1) : 0.0);
            }
            
            // Average Package
            try (ResultSet rs = stmt.executeQuery("SELECT AVG(offered_package_lpa) FROM selections WHERE status = 'ACCEPTED'")) {
                stats.put("averagePackage", rs.next() ? rs.getDouble(1) : 0.0);
            }
            
            // Calculate Placement Percentage
            int totalStud = (int) stats.get("totalStudents");
            int selectedStud = (int) stats.get("totalSelected");
            double placementPct = totalStud > 0 ? ((double) selectedStud / totalStud) * 100.0 : 0.0;
            stats.put("placementPercentage", placementPct);
            
        } catch (Exception e) {
            throw new DatabaseException("Failed to fetch dashboard statistics: " + e.getMessage(), e);
        }
        
        return stats;
    }
}
