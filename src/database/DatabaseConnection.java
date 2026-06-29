package database;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    private static String host = "localhost";
    private static String port = "3306";
    private static String dbName = "sparms_db";
    private static String user = "root";
    private static String password = "Indira@0201";
    private static String mysqlBinPath = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe";

    static {
        try {
            // Register MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Load configuration from config.properties if available
            loadConfig();
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        }
    }

    private static void loadConfig() {
        java.util.Properties props = new java.util.Properties();
        java.io.File configFile = new java.io.File("config.properties");
        if (configFile.exists()) {
            try (java.io.FileInputStream fis = new java.io.FileInputStream(configFile)) {
                props.load(fis);
                System.out.println("Loaded database configuration from: " + configFile.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("Could not load config.properties from file system, trying classpath: " + e.getMessage());
            }
        }

        if (props.isEmpty()) {
            try (java.io.InputStream is = DatabaseConnection.class.getResourceAsStream("/config.properties")) {
                if (is != null) {
                    props.load(is);
                    System.out.println("Loaded database configuration from classpath resources.");
                }
            } catch (Exception e) {
                // Ignore and use defaults
            }
        }

        if (!props.isEmpty()) {
            host = props.getProperty("db.host", host);
            port = props.getProperty("db.port", port);
            dbName = props.getProperty("db.name", dbName);
            user = props.getProperty("db.user", user);
            password = props.getProperty("db.password", password);
            mysqlBinPath = props.getProperty("mysql.dump.path", mysqlBinPath);
        }
    }

    private static String getUrlNoDb() {
        return "jdbc:mysql://" + host + ":" + port + "/?allowMultiQueries=true";
    }

    private static String getUrlWithDb() {
        return "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?allowMultiQueries=true";
    }

    public static String getDbName() {
        return dbName;
    }

    public static String getUser() {
        return user;
    }

    public static String getPassword() {
        return password;
    }

    public static String getMysqlBinPath() {
        return mysqlBinPath;
    }

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(getUrlWithDb(), user, password);
    }

    public static synchronized void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(getUrlNoDb(), user, password);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("Connecting to MySQL to check database...");
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
            System.out.println("Database " + dbName + " verified/created.");

            // Now connect to the database and verify table existence
            try (Connection dbConn = DriverManager.getConnection(getUrlWithDb(), user, password)) {
                if (!tablesExist(dbConn)) {
                    System.out.println("Tables not found. Initializing database schema from schema.sql...");
                    executeSchemaScript(dbConn);
                    System.out.println("Schema initialization complete.");
                } else {
                    System.out.println("Tables already exist. Database verification passed.");
                }
            }
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean tablesExist(Connection conn) {
        try {
            ResultSet rs = conn.getMetaData().getTables(null, null, "admins", null);
            return rs.next();
        } catch (Exception e) {
            return false;
        }
    }

    private static void executeSchemaScript(Connection conn) throws Exception {
        // Read schema.sql content
        String schemaPath = "src/resources/schema.sql";
        List<String> lines = null;
        
        if (Files.exists(Paths.get(schemaPath))) {
            lines = Files.readAllLines(Paths.get(schemaPath));
        } else {
            // Try loading from classpath
            try (InputStream is = DatabaseConnection.class.getResourceAsStream("/resources/schema.sql")) {
                if (is != null) {
                    lines = new ArrayList<>();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            lines.add(line);
                        }
                    }
                }
            }
        }

        if (lines == null || lines.isEmpty()) {
            throw new Exception("Could not find schema.sql at " + schemaPath + " or on classpath.");
        }

        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            // Ignore SQL comments and empty lines
            if (line.trim().startsWith("--") || line.trim().startsWith("/*") || line.trim().isEmpty()) {
                continue;
            }
            sb.append(line).append("\n");
        }

        // Split by semicolon, making sure we don't split inside triggers or procedures if they exist
        String[] statements = sb.toString().split(";");
        try (Statement stmt = conn.createStatement()) {
            conn.setAutoCommit(false);
            for (String sql : statements) {
                String trimmedSql = sql.trim();
                if (!trimmedSql.isEmpty()) {
                    stmt.execute(trimmedSql);
                }
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (Exception e) {
            conn.rollback();
            throw e;
        }
    }
}
