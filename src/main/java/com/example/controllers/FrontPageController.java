package com.example.controllers;

import com.example.models.CardSet;
import com.example.services.CardSetService;
import com.example.services.UserService;
import com.example.utils.LoginStatusButton;
import com.example.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import java.util.List;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;

public class FrontPageController {
    @FXML private VBox cardSetVBox;
    @FXML private Button addCardSet;
    @FXML private Label welcomeLabel;
    @FXML private GridPane calendarGrid;

    private UserService userService;
    private CardSetService cardSetService;
    private SceneManager sceneManager;
    private String currentUsername;

    public FrontPageController() {
    }

    @FXML
    public void initialize() {
        if (cardSetVBox == null) System.err.println("Error: cardSetVBox is not initialized");
        if (addCardSet == null) System.err.println("Error: addCardSet button is not initialized");
    }

    public void initializeServices(CardSetService cardSetService, UserService userService, SceneManager sceneManager) {
        this.userService = userService;
        this.sceneManager = sceneManager;
        this.cardSetService = cardSetService;
    }
    public void setCurrentUsername(String username) {
        this.currentUsername = username;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + username + "!");
        }
        loadCardSets();
        updateCalendar();
    }

    private void updateCalendar() {
        LocalDate today = LocalDate.now();
        YearMonth yearMonth = YearMonth.from(today);
        LocalDate firstOfMonth = yearMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        try {
            List<LocalDate> loginDates = userService.getLoginDates(currentUsername, firstOfMonth, today);

            for (int i = 1; i <= yearMonth.lengthOfMonth(); i++) {
                LoginStatusButton button = new LoginStatusButton(String.valueOf(i));
                LocalDate date = LocalDate.of(today.getYear(), today.getMonth(), i);

                if (loginDates.contains(date)) {
                    button.setLoginStatus(LoginStatusButton.LoginStatus.LOGGED_IN);
                } else if (date.isBefore(today)) {
                    button.setLoginStatus(LoginStatusButton.LoginStatus.SKIPPED);
                } else {
                    button.setLoginStatus(LoginStatusButton.LoginStatus.FUTURE);
                }

                calendarGrid.add(button, (dayOfWeek + i - 2) % 7, (dayOfWeek + i - 2) / 7);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving login dates: " + e.getMessage());
        }
    }

    @FXML
    public void handleAddCardSet() {
        try {
            sceneManager.openPopup("/main/FXML/CardSetPage.fxml", "Create New Card Set", controller -> {
                CardSetPageController cardSetController = (CardSetPageController) controller;
                String cardSetName = cardSetController.getCardSetName();
                boolean isTrueFalse = cardSetController.isTrueFalse();
                if (cardSetName != null && !cardSetName.isEmpty()) {
                    try {
                        cardSetService.createCardSet(currentUsername, cardSetName, isTrueFalse);
                        loadCardSets();
                    } catch (SQLException e) {
                        System.err.println("Error creating card set: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("Error opening CardSetPage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadCardSets() {
        try {
            List<CardSet> cardSets = userService.getUserCardSets(currentUsername);
            cardSetVBox.getChildren().clear();
            for (CardSet cardSet : cardSets) {
                addCardSetButton(cardSet);
            }
        } catch (SQLException e) {
            System.err.println("Error loading card sets: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addCardSetButton(CardSet cardSet) {
        Button cardSetButton = new Button(cardSet.getName());
        cardSetButton.setPrefWidth(200);
        cardSetButton.setPrefHeight(50);
        cardSetButton.setStyle("-fx-font-size: 14px;");

        cardSetButton.setOnAction(e -> openCardPage(cardSet));

        cardSetVBox.getChildren().add(cardSetButton);
    }

    private void openCardPage(CardSet cardSet) {
        try {
            sceneManager.switchToCardPage(cardSet);
        } catch (IOException e) {
            System.err.println("Error opening CardPage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogout() {
        try {
            sceneManager.switchScene("/main/FXML/LoginPage.fxml", "Flashcards Made Easy - Login");
        } catch (IOException e) {
            System.err.println("Error switching to LoginPage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleStartRandomQuiz() {
        try {
            sceneManager.switchToTestPage(currentUsername);
        } catch (IOException e) {
            System.err.println("Error switching to test page: " + e.getMessage());
        }
    }
}
