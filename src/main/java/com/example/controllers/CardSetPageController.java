package com.example.controllers;

import java.sql.SQLException;

import com.example.services.CardSetService;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CardSetPageController {
    @FXML private Button confirmCardChanges;
    @FXML private TextField cardSetNameField;
    @FXML private RadioButton tofField;

    private CardSetService cardSetService;
    private String cardSetName;
    private boolean isBoolean;
    private String currentUserName;

    public CardSetPageController() {}

    public void initializeServices(CardSetService cardSetService) {
        this.cardSetService = cardSetService;
    }

    public void setCurrentUsername(String userName) {
        this.currentUserName = userName;
    }

    @FXML
    public String confirmCardSet() throws SQLException {
        cardSetName = cardSetNameField.getText();
        isBoolean = tofField.isSelected();
        
        cardSetService.createCardSet(currentUserName, cardSetName, isBoolean);

        Stage stage = (Stage) confirmCardChanges.getScene().getWindow();
        stage.close();

        return cardSetName;
    }

    public String getCardSetName() {
        return cardSetName;
    }

    public boolean isTrueFalse() {
        return isBoolean;
    }
}