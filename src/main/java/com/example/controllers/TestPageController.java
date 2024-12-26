package com.example.controllers;

import com.example.models.CardSet;
import com.example.models.Cards;
import com.example.services.CardSetService;
import com.example.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.List;

public class TestPageController {
    @FXML private Label cardSetNameLabel;
    @FXML private Label questionLabel;
    @FXML private TextField answerField;
    @FXML private Button submitButton;
    @FXML private Label feedbackLabel;
    @FXML private Button nextButton;
    @FXML private Button finishButton;

    private CardSetService cardSetService;
    private SceneManager sceneManager;
    private CardSet currentCardSet;
    private List<Cards> cards;
    private int currentCardIndex;

    public TestPageController() {
    }

    @FXML
    public void initialize() {
        nextButton.setDisable(true);
        finishButton.setDisable(true);
    }

    public void initializeServices(CardSetService cardSetService, SceneManager sceneManager) {
        this.cardSetService = cardSetService;
        this.sceneManager = sceneManager;
    }

    public void startTest(String username) {
        try {
            currentCardSet = cardSetService.getRandomCardSet(username);
            cards = currentCardSet.getFlashcards();
            cardSetNameLabel.setText(currentCardSet.getName());
            currentCardIndex = 0;
            showNextQuestion();
        } catch (Exception e) {
            System.err.println("Error starting test: " + e.getMessage());
        }
    }

    private void showNextQuestion() {
        if (currentCardIndex < cards.size()) {
            Cards currentCard = cards.get(currentCardIndex);
            questionLabel.setText(currentCard.getFrontCard());
            answerField.clear();
            feedbackLabel.setText("");
            submitButton.setDisable(false);
            nextButton.setDisable(true);
            finishButton.setDisable(true);
        } else {
            questionLabel.setText("Test Completed!");
            answerField.setDisable(true);
            submitButton.setDisable(true);
            nextButton.setDisable(true);
            finishButton.setDisable(false);
        }
    }

    @FXML
    private void handleSubmit() {
        Cards currentCard = cards.get(currentCardIndex);
        String userAnswer = answerField.getText().trim().toLowerCase();
        String correctAnswer = currentCard.getBackCard().toLowerCase();

        if (userAnswer.equals(correctAnswer)) {
            feedbackLabel.setText("Correct!");
            feedbackLabel.setStyle("-fx-text-fill: green;");
        } else {
            feedbackLabel.setText("Incorrect. The correct answer is: " + currentCard.getBackCard());
            feedbackLabel.setStyle("-fx-text-fill: red;");
        }

        submitButton.setDisable(true);
        nextButton.setDisable(false);
    }

    @FXML
    private void handleNext() {
        currentCardIndex++;
        showNextQuestion();
    }

    @FXML
    private void handleFinish() {
        try {
            sceneManager.switchScene("/main/FXML/FrontPage.fxml", "Front Page");
        } catch (IOException e) {
            System.err.println("Error returning to front page: " + e.getMessage());
        }
    }
}