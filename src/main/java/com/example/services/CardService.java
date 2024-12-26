package com.example.services;

import com.example.models.Cards;
import com.example.utils.DatabaseManager;
import javafx.collections.ObservableList;

public class CardService {
    private DatabaseManager dbManager;

    public CardService(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void addFlashcard(int cardSetId, Cards flashcard) {
        dbManager.addFlashcard(cardSetId, flashcard);
    }

    public ObservableList<Cards> getFlashcards(int cardSetId) {
        return dbManager.getFlashcards(cardSetId);
    }
}