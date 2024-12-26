package com.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class CardEditorPageController {
    @FXML
    private TextArea FlashcardFront;
    @FXML
    private TextArea FlashcardBack;
    @FXML
    private Button ConfirmCard;

    public CardEditorPageController() {}

    public String getFront(){
        return FlashcardFront.getText();
    }

    public String getBack(){
        return FlashcardBack.getText();
    }

    public void Card_Confirm(){
        Stage stage = (Stage) ConfirmCard.getScene().getWindow();
        stage.close();
    }
}
