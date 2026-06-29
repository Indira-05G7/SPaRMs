package dao;

import database.DatabaseConnection;
import model.Company;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyDAO {
    
    public List<Company> getAll() throws Exception {
        List<Company> list = new ArrayList<>();
        String sql = "SELECT * FROM companies ORDER BY name";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Company(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("website"),
                    rs.getString("industry"),
                    rs.getString("description")
                ));
            }
        }
        return list;
    }

    public Company getById(int id) throws Exception {
        String sql = "SELECT * FROM companies WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Company(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("website"),
                        rs.getString("industry"),
                        rs.getString("description")
                    );
                }
            }
        }
        return null;
    }

    public boolean save(Company company) throws Exception {
        String sql = "INSERT INTO companies (name, website, industry, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, company.getName());
            pstmt.setString(2, company.getWebsite());
            pstmt.setString(3, company.getIndustry());
            pstmt.setString(4, company.getDescription());
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        company.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean update(Company company) throws Exception {
        String sql = "UPDATE companies SET name = ?, website = ?, industry = ?, description = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, company.getName());
            pstmt.setString(2, company.getWebsite());
            pstmt.setString(3, company.getIndustry());
            pstmt.setString(4, company.getDescription());
            pstmt.setInt(5, company.getId());
            return pstmt.executeUpdate() > 0;
        }
    }
}
