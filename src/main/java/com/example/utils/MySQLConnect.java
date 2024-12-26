package com.example.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnect {
    private static final String URL = "jdbc:mysql://localhost:3306/flashcardsmadeeasy?user=root";
    private static final String SQL_User = "root";
    private static final String SQL_Password = "@BUdH@B1m1shk@";
    public static Connection connect() throws SQLException{
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, SQL_User, SQL_Password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
        catch(SQLException e){
            System.err.println("Connection Error" + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            throw new SQLException("Failed to connect to FlashcardsMadeEasy database", e);
        }
    }
}