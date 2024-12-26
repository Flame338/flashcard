package com.example.controllers;

import com.example.services.UserService;
import com.example.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;

public class LoginPageController {
    @FXML private Label errorMessage;
    @FXML private TextField usernameInput;
    @FXML private PasswordField passwordInput;

    private UserService userService;
    private SceneManager sceneManager;

    public LoginPageController() {}

    @FXML
    public void initialize() {}
    
    public void initializeServices(UserService userService, SceneManager sceneManager) {
        this.userService = userService;
        this.sceneManager = sceneManager;
    }

    @FXML
    public void handleNewUser(ActionEvent event) {
        try {
            sceneManager.switchScene("/main/FXML/SignUpPage.fxml", "Flashcards Made Easy - Sign Up");
        } catch (IOException e) {
            showError("Error loading sign up page: " + e.getMessage());
        }
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameInput.getText();
        String password = passwordInput.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password cannot be empty");
            return;
        }

        try {
            if (userService.authenticateUser(username, password)) {
                sceneManager.setCurrentUsername(username);
                sceneManager.switchScene("/main/FXML/FrontPage.fxml", "Flashcards Made Easy");
            } else {
                showError("Invalid username or password");
            }
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        } catch (IOException e) {
            showError("Error loading front page: " + e.getMessage());
        }
    }

    private void showError(String message) {
        if (errorMessage != null) {
            errorMessage.setText(message);
            errorMessage.setVisible(true);
        } else {
            System.err.println("Error: " + message);
        }
    }
}

