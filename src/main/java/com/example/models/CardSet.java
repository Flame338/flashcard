package com.example.models;

import java.util.List;
import java.util.ArrayList;

public class CardSet {
    private int id;
    private String name;
    private boolean isTrueFalse;
    private List<Cards> flashcards;

    public CardSet(int id, String name) {
        this.id = id;
        this.name = name;
        this.flashcards = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isTrueFalse() {
        return isTrueFalse;
    }

    public void setTrueFalse(boolean isTrueFalse) {
        this.isTrueFalse = isTrueFalse;
    }

    public List<Cards> getFlashcards() {
        return flashcards;
    }

    public void setFlashcards(List<Cards> flashcards) {
        this.flashcards = flashcards;
    }
}