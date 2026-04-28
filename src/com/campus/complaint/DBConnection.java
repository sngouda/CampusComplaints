package com.campus.complaint;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Database credentials and URL
    private static final String URL = "jdbc:mysql://localhost:3306/campus_complaint_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";       // Change this as per user's MySQL setup
    private static final String PASSWORD = "root";   // Change this as per user's MySQL setup
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Load the MySQL JDBC driver
            Class.forName(DRIVER);
            // Establish the connection
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Connection Failed! Check output console. " + e.getMessage());
        }
        return conn;
    }
}
