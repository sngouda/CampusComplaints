package com.campus.complaint;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // ✅ Fixed URL with allowPublicKeyRetrieval=true
    private static final String URL = "jdbc:mysql://localhost:3306/campus_complaint_db"
        + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "@Suhas123105";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Connection Failed! Check output console. " + e.getMessage());
        }
        return conn;
    }
}