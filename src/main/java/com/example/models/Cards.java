package com.example.models;

import java.io.Serializable;

public class Cards implements Serializable {
    private String frontCard;
    private String backCard;

    public Cards(String frontCard, String backCard) {
        this.frontCard = frontCard;
        this.backCard = backCard;
    }

    public String getFrontCard() {
        return frontCard;
    }

    public String getBackCard() {
        return backCard;
    }

    public void setFrontCard(String frontCard) {
        this.frontCard = frontCard;
    }

    public void setBackCard(String backCard) {
        this.backCard = backCard;
    }
}