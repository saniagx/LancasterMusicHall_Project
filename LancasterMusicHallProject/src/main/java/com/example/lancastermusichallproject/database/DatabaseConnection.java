package com.example.lancastermusichallproject.database;

import java.sql.*;
import java.sql.DriverManager;
import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseConnection {
    public static void main(String[] args) {
        // Database Details
        String host = "sst-stuproj.city.ac.uk";
        String port = "3306";  //MySQL port
        String dbName = "in2033t02";

        Dotenv dotenv = Dotenv.load();
        String user = dotenv.get("DB_USERNAME");
        String password = dotenv.get("DB_PASSWORD");

        // Connection URL
        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?user=" + user + "&password=" + password;
        System.out.println("Connecting to database...");
        try {
            //connection to the database
            Connection conn = DriverManager.getConnection(url);
            System.out.println("Connected to the database!");

            // Our database operations can go here

            // Closing connection when done
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
