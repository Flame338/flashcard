package com.example.utils;

import com.example.models.CardSet;
import com.example.models.Cards;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatabaseManager {
    private static DatabaseManager instance;
    
    private DatabaseManager() {}
    
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public boolean authenticateUser(String username, String password) throws SQLException {
        String query = "SELECT password FROM Users WHERE username = ?";
        try (Connection conn = MySQLConnect.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    return verifyPassword(password, storedPassword);
                }
            }
        }
        return false;
    }

    public void registerUser(String username, String password) throws SQLException {
        String query = "INSERT INTO Users (username, password) VALUES (?, ?)";
        try (Connection conn = MySQLConnect.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            String hashedPassword = hashPassword(password);
            if (hashedPassword.length() > 64) {
                throw new SQLException("Hashed password exceeds maximum length of 64 characters");
            }
            pstmt.setString(2, hashedPassword);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
        } catch (SQLException e) {
            System.err.println("Error in registerUser: " + e.getMessage());
            if (e.getMessage().contains("Duplicate entry")) {
                throw new SQLException("Username already exists", e);
            }
            throw e; // Re-throw the exception to be handled by the caller
        }
    }

    public List<CardSet> getUserCardSets(String username) throws SQLException {
        List<CardSet> cardSets = new ArrayList<>();
        String query = "SELECT cs.CardSet_id, cs.name FROM CardSets cs " +
                       "JOIN Users u ON cs.user_id = u.user_id " +
                       "WHERE u.username = ?";
        try (Connection conn = MySQLConnect.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int cardSetId = rs.getInt("CardSet_id");
                    String cardSetName = rs.getString("name");
                    cardSets.add(new CardSet(cardSetId, cardSetName));
                }
            }
        }
        return cardSets;
    }

    public void setUserCardSets(String username, List<CardSet> cardSets) throws SQLException {
        String getUserIdQuery = "SELECT user_id FROM Users WHERE username = ?";
        String deleteExistingCardSetsQuery = "DELETE FROM CardSets WHERE user_id = ?";
        String insertCardSetQuery = "INSERT INTO CardSets (user_id, name, is_boolean) VALUES (?, ?, ?)";

        try (Connection conn = MySQLConnect.connect()) {
            conn.setAutoCommit(false);
            try {
                // Get user_id
                int userId;
                try (PreparedStatement pstmt = conn.prepareStatement(getUserIdQuery)) {
                    pstmt.setString(1, username);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("User not found: " + username);
                        }
                        userId = rs.getInt("user_id");
                    }
                }

                // Delete existing card sets for this user
                try (PreparedStatement pstmt = conn.prepareStatement(deleteExistingCardSetsQuery)) {
                    pstmt.setInt(1, userId);
                    pstmt.executeUpdate();
                }

                // Insert new card sets
                try (PreparedStatement pstmt = conn.prepareStatement(insertCardSetQuery)) {
                    for (CardSet cardSet : cardSets) {
                        pstmt.setInt(1, userId);
                        pstmt.setString(2, cardSet.getName());
                        pstmt.setBoolean(3, cardSet.isTrueFalse());
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void addFlashcard(int cardSetId, Cards flashcard) {
        String query = "INSERT INTO Flashcards (cardSet_id, frontSide, backSide) VALUES (?, ?, ?)";
        try (Connection conn = MySQLConnect.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, cardSetId);
            pstmt.setString(2, flashcard.getFrontCard());
            pstmt.setString(3, flashcard.getBackCard());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Cards> getFlashcards(int cardSetId) {
        ObservableList<Cards> flashcards = FXCollections.observableArrayList();
        String query = "SELECT * FROM Flashcards WHERE CardSet_id = ?";
        try (Connection conn = MySQLConnect.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, cardSetId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String front = rs.getString("frontSide");
                    String back = rs.getString("backSide");
                    flashcards.add(new Cards(front, back));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flashcards;
    }

    public void createCardSet(String userName, String cardSetName, boolean isTrueFalse) throws SQLException {
        String query = "INSERT INTO CardSets (user_id, name, is_boolean) VALUES ((SELECT user_id FROM Users WHERE username = ?), ?, ?)";
        try (Connection conn = MySQLConnect.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, userName);
                pstmt.setString(2, cardSetName);
                pstmt.setBoolean(3, isTrueFalse);
                pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error in createCardSet: " + e.getMessage());
            throw e;
        }
    }

    public boolean usernameExists(String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM Users WHERE username = ?";
        try (Connection conn = MySQLConnect.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in usernameExists: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return false;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    private boolean verifyPassword(String inputPassword, String storedPassword) {
        String hashedInput = hashPassword(inputPassword);
        return hashedInput.equals(storedPassword);
    }

        public void recordLogin(String username, LocalDate date) throws SQLException {
        String query = "INSERT INTO UserLogins (user_id, login_date) VALUES ((SELECT user_id FROM Users WHERE username = ?), ?)";
        try (Connection conn = MySQLConnect.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setDate(2, java.sql.Date.valueOf(date));
            pstmt.executeUpdate();
        }
    }

    public List<LocalDate> getLoginDates(String username, LocalDate startDate, LocalDate endDate) throws SQLException {
        List<LocalDate> loginDates = new ArrayList<>();
        String query = "SELECT login_date FROM UserLogins WHERE user_id = (SELECT user_id FROM Users WHERE username = ?) AND login_date BETWEEN ? AND ?";
        try (Connection conn = MySQLConnect.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setDate(2, java.sql.Date.valueOf(startDate));
            pstmt.setDate(3, java.sql.Date.valueOf(endDate));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    loginDates.add(rs.getDate("login_date").toLocalDate());
                }
            }
        }
        return loginDates;
    }
}

