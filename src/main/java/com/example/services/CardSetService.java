package com.example.services;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import javafx.collections.ObservableList;

import com.example.models.CardSet;
import com.example.models.Cards;
import com.example.utils.DatabaseManager;

public class CardSetService {
    private DatabaseManager dbManager;

    public CardSetService(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void createCardSet(String userName, String cardSetName, boolean isTrueFalse) throws SQLException {
        dbManager.createCardSet(userName, cardSetName, isTrueFalse);
    }

    public CardSet getRandomCardSet(String username) throws SQLException {
        List<CardSet> cardSets = dbManager.getUserCardSets(username);
        if (cardSets.isEmpty()) {
            throw new IllegalStateException("No card sets available for the user.");
        }
        
        Random random = new Random();
        CardSet selectedSet;
        
        if (cardSets.size() == 1) {
            selectedSet = cardSets.get(0);
        } else {
            selectedSet = cardSets.get(random.nextInt(cardSets.size()));
        }
        
        // Load flashcards for the selected set
        ObservableList<Cards> flashcards = dbManager.getFlashcards(selectedSet.getId());
        selectedSet.setFlashcards(flashcards);
        
        if (flashcards.isEmpty()) {
            throw new IllegalStateException("Selected card set has no flashcards.");
        }
        
        return selectedSet;
    }
}