package com.example.services;

import com.example.models.CardSet;
import com.example.utils.DatabaseManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class UserService {
    private DatabaseManager dbManager;

    public UserService(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public boolean authenticateUser(String username, String password) throws SQLException {
        return dbManager.authenticateUser(username, password);
    }

    public void registerUser(String username, String password) throws SQLException {
        dbManager.registerUser(username, password);
    }

    public List<CardSet> getUserCardSets(String username) throws SQLException {
        return dbManager.getUserCardSets(username);
    }

    public void setUserCardSets(String username, List<CardSet> cardSets) throws SQLException {
        dbManager.setUserCardSets(username, cardSets);
    }

    public void recordLogin(String username) throws SQLException {
        dbManager.recordLogin(username, LocalDate.now());
    }

    public List<LocalDate> getLoginDates(String username, LocalDate startDate, LocalDate endDate) throws SQLException {
        return dbManager.getLoginDates(username, startDate, endDate);
    }
}