package com.example.controllers;

import java.io.IOException;

import com.example.models.CardSet;
import com.example.models.Cards;
import com.example.services.CardService;
import com.example.utils.SceneManager;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class CardPageController {

    @FXML private Text cardSetTitle;
    @FXML private Button addFlashCardButton;
    @FXML private Button returnFrontPage;
    @FXML private VBox flashcardVBox;

    private CardSet currentCardSet;
    private CardService cardService;
    private SceneManager sceneManager;

    public CardPageController() {}

    @FXML
    public void initialize() {
        if (cardSetTitle == null) System.err.println("Error: cardSetTitle is not initialized");
        if (addFlashCardButton == null) System.err.println("Error: addFlashcardButton is not initialized");
        if (returnFrontPage == null) System.err.println("Error: returnFrontPage is not initialized");
        if (flashcardVBox == null) System.err.println("Error: flashcardVBox is not initialized");
    }

    public void initializeServices(CardService cardService, SceneManager sceneManager) {
        this.cardService = cardService;
        this.sceneManager = sceneManager;
        
        if (addFlashCardButton != null) {
            addFlashCardButton.setOnAction(e -> openCardEditorPopup());
        }
        
        if (returnFrontPage != null) {
            returnFrontPage.setOnAction(e -> returnFrontPage());
        }
    }

    @FXML
    public void handleAddFlashcard() {
        try {
            sceneManager.openPopup("/main/FXML/CardEditorPage.fxml", "Card Editor Page", controller -> {
                CardEditorPageController cardEditorController = (CardEditorPageController) controller;
                String front = cardEditorController.getFront();
                String back = cardEditorController.getBack();

                if (front != null && back != null && !front.isEmpty() && !back.isEmpty()) {
                    Cards newFlashcard = new Cards(front, back);
                    cardService.addFlashcard(currentCardSet.getId(), newFlashcard);
                    updateFlashcardDisplay();
                }
            });
        } catch (Exception e) {
            System.err.println("Error opening CardEditorPage: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void setCardSet(CardSet cardSet) {
        this.currentCardSet = cardSet;
        if (cardSetTitle != null) {
            cardSetTitle.setText(cardSet.getName());
        }
        updateFlashcardDisplay();
    }

    private void openCardEditorPopup() {
        try {
            sceneManager.openPopup("/main/FXML/CardEditorPage.fxml", "Card Editor Page", controller -> {
                CardEditorPageController cardEditorController = (CardEditorPageController) controller;
                String front = cardEditorController.getFront();
                String back = cardEditorController.getBack();

                if (front != null && back != null && !front.isEmpty() && !back.isEmpty()) {
                    Cards newFlashcard = new Cards(front, back);
                    cardService.addFlashcard(currentCardSet.getId(), newFlashcard);
                    updateFlashcardDisplay();
                }
            });
        } catch (Exception e) {
            System.err.println("Error opening CardEditorPage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateFlashcardDisplay() {
        flashcardVBox.getChildren().clear();
        ObservableList<Cards> visibleFlashcards = cardService.getFlashcards(currentCardSet.getId());
        for (Cards flashcard : visibleFlashcards) {
            Button flashcardButton = createFlashcardButton(flashcard);
            flashcardVBox.getChildren().add(flashcardButton);
        }
    }

    private Button createFlashcardButton(Cards flashcard) {
        Button flashcardButton = new Button(flashcard.getFrontCard());
        flashcardButton.setPrefWidth(200);
        flashcardButton.setPrefHeight(50);
        flashcardButton.setStyle("-fx-font-size: 14px;");
        flashcardButton.setOnAction(e -> showFlashcardDetails(flashcard));
        return flashcardButton;
    }

    private void showFlashcardDetails(Cards flashcard) {
        // Implement this method to show flashcard details, perhaps in a popup
        System.out.println("Front: " + flashcard.getFrontCard() + ", Back: " + flashcard.getBackCard());
    }

    @FXML
    private void returnFrontPage() {
        try {
            sceneManager.switchScene("/main/FXML/FrontPage.fxml", "Flashcards Made Easy");
        } catch (IOException e) {
            System.err.println("Error returning to FrontPage: " + e.getMessage());
            e.printStackTrace();
        }
    }
}