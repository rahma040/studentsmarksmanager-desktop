package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SingletonConnection {
    private static volatile Connection connection;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/studentmanager";
    private static final String USER = "root";
    private static final String PASS = "admin";

    static {
        try {
            // Use the newer driver class if you're using MySQL Connector/J 8.0+
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            try {
                // Fall back to older driver if new one isn't available
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                System.err.println(" MySQL JDBC Driver not found!");
                System.err.println("Please add the MySQL Connector/J JAR to your classpath.");
                ex.printStackTrace();
                throw new RuntimeException("MySQL JDBC Driver not found", ex);
            }
        }
    }

    private SingletonConnection() {
        // Private constructor to prevent instantiation
    }

    public static Connection getInstance() {
        if (connection == null) {
            synchronized (SingletonConnection.class) {
                if (connection == null) {
                    try {
                        connection = createConnection();
                    } catch (SQLException e) {
                        System.err.println(" Failed to create database connection");
                        e.printStackTrace();
                        // Re-throw as RuntimeException to prevent code from continuing with null connection
                        throw new RuntimeException("Failed to create database connection", e);
                    }
                }
            }
        }
        return connection;
    }

    static Connection createConnection() throws SQLException {
        try {
            Properties props = new Properties();
            props.setProperty("user", USER);
            props.setProperty("password", PASS);
            
            // Use only essential properties to minimize issues
            props.setProperty("useSSL", "false");
            props.setProperty("allowPublicKeyRetrieval", "true");
            props.setProperty("serverTimezone", "UTC");
            props.setProperty("useUnicode", "true");
            props.setProperty("characterEncoding", "UTF-8");
            
            // Create connection
            Connection conn = DriverManager.getConnection(DB_URL, props);
            
            // Test the connection with simple query
            conn.createStatement().execute("SELECT 1");
            System.out.println(" Database connection established successfully!");
            return conn;
            
        } catch (SQLException e) {
            System.err.println(" Connection failed! Error: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("\nPlease verify:");
            System.err.println("- MySQL server is running on localhost:3306");
            System.err.println("- Database 'studentmanager' exists");
            System.err.println("- User 'root' has proper privileges");
            System.err.println("- Password is correct");
            throw e;
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println(" Database connection closed successfully");
            } catch (SQLException e) {
                System.err.println(" Error closing connection: " + e.getMessage());
                e.printStackTrace();
            } finally {
                connection = null;
            }
        }
    }
}