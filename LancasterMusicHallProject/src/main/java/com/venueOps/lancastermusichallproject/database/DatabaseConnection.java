package com.venueOps.lancastermusichallproject.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseConnection {
    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Database Details
                String host = "sst-stuproj.city.ac.uk";
                String port = "3306";
                String dbName = "in2033t02";

                // Load credentials from .env
                Dotenv dotenv;
                try {
                    dotenv = Dotenv.load();
                } catch (Exception e) {
                    System.err.println("Failed to load .env file: " + e.getMessage());
                    return null;
                }
                String user = dotenv.get("DB_USERNAME");
                String password = dotenv.get("DB_PASSWORD");
                if (user == null || password == null) {
                    System.err.println("Database credentials not found in .env file");
                    return null;
                }

                // Connection
                String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName;
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Connected to the database");
            }
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            connection = null;
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
