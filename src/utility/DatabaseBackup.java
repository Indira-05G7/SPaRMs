package utility;

import database.DatabaseConnection;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseBackup {
    
    public static boolean executeBackup(String destinationPath) {
        System.out.println("Initiating database backup to: " + destinationPath);
        
        // Try mysqldump CLI first
        boolean cliSuccess = tryCliBackup(destinationPath);
        if (cliSuccess) {
            System.out.println("CLI backup succeeded.");
            return true;
        }

        System.out.println("CLI backup failed or unavailable. Falling back to JDBC-based programmatic backup...");
        // Fallback: Programmatic backup via JDBC queries
        return executeProgrammaticBackup(destinationPath);
    }

    private static boolean tryCliBackup(String destPath) {
        String mysqlBinPath = DatabaseConnection.getMysqlBinPath();
        File dumpExe = new File(mysqlBinPath);
        String exeToRun = dumpExe.exists() ? mysqlBinPath : "mysqldump";

        try {
            ProcessBuilder pb = new ProcessBuilder(
                exeToRun,
                "-u", DatabaseConnection.getUser(),
                "-p" + DatabaseConnection.getPassword(),
                DatabaseConnection.getDbName(),
                "--result-file=" + destPath
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            System.err.println("CLI Backup Exception (attempted with '" + exeToRun + "'): " + e.getMessage());
            return false;
        }
    }

    private static boolean executeProgrammaticBackup(String destPath) {
        String[] tables = {
            "departments",
            "admins",
            "companies",
            "recruiters",
            "students",
            "skills",
            "student_skills",
            "placement_drives",
            "applications",
            "interview_rounds",
            "selections",
            "notifications"
        };

        try (Connection conn = DatabaseConnection.getConnection();
             BufferedWriter writer = new BufferedWriter(new FileWriter(destPath))) {
            
            writer.write("-- SPaRMS Database Backup SQL Dump\n");
            writer.write("-- Generated programmatically via JDBC\n");
            writer.write("CREATE DATABASE IF NOT EXISTS " + DatabaseConnection.getDbName() + ";\n");
            writer.write("USE " + DatabaseConnection.getDbName() + ";\n\n");
            writer.write("SET FOREIGN_KEY_CHECKS = 0;\n\n");

            for (String table : tables) {
                writer.write("-- Dumping data for table `" + table + "`\n");
                
                String selectSql = "SELECT * FROM " + table;
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(selectSql)) {
                    
                    ResultSetMetaData meta = rs.getMetaData();
                    int columnCount = meta.getColumnCount();

                    // Generate insert statements
                    while (rs.next()) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("INSERT INTO `").append(table).append("` (");
                        for (int i = 1; i <= columnCount; i++) {
                            sb.append("`").append(meta.getColumnName(i)).append("` ");
                            if (i < columnCount) sb.append(", ");
                        }
                        sb.append(") VALUES (");

                        for (int i = 1; i <= columnCount; i++) {
                            Object val = rs.getObject(i);
                            if (val == null) {
                                sb.append("NULL");
                            } else if (val instanceof Number) {
                                sb.append(val.toString());
                            } else if (val instanceof Boolean) {
                                sb.append(val.toString().toUpperCase());
                            } else {
                                // String, Dates, etc. Escape single quotes.
                                String escaped = val.toString().replace("'", "''");
                                sb.append("'").append(escaped).append("'");
                            }
                            if (i < columnCount) sb.append(", ");
                        }
                        sb.append(");\n");
                        writer.write(sb.toString());
                    }
                }
                writer.write("\n");
            }

            writer.write("SET FOREIGN_KEY_CHECKS = 1;\n");
            writer.flush();
            return true;
        } catch (Exception e) {
            System.err.println("Programmatic Backup Exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
