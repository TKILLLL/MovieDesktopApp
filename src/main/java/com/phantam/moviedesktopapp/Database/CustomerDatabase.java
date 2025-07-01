package com.phantam.moviedesktopapp.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CustomerDatabase {
    private static final String URL = "jdbc:mysql://localhost:3306/Customers";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static boolean isConnected = false;

    private Connection conn;

    public Connection getConn() {
        return conn;
    }

    public static boolean isIsConnected() {
        return isConnected;
    }

    /**
     * Tạo kết nối đến cơ sở dữ liệu.
     */
    public void CustomerDatabaseConnection() {
        try {
            // Kết nối database
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            isConnected = true;
        } catch (SQLException e) {
            isConnected = false;
        }
    }

    /**
     * Đóng kết nối với cơ sở dữ liệu.
     */
    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
                isConnected = false;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
