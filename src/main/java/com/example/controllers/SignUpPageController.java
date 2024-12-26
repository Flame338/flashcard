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

public class SignUpPageController {
    @FXML private TextField createUsernameField;
    @FXML private PasswordField createPasswordField;
    @FXML private Label signUpError;

    private UserService userService;
    private SceneManager sceneManager;

    public SignUpPageController() {}

    @FXML
    public void initialize() {
        if (createUsernameField == null) {
            System.err.println("Error: createUserNameField is not initialized");
        }
        if (createPasswordField == null) {
            System.err.println("Error: createPasswordField is not initialized");
        }
        if (signUpError == null) {
            System.err.println("Error: signUpError label is not initialized");
        }
    }

    public void initializeServices(UserService userService, SceneManager sceneManager) {
        this.userService = userService;
        this.sceneManager = sceneManager;
    }

    @FXML
    public void handleSignUp(ActionEvent event) {
        if (saveUserInfo()) {
            try {
                sceneManager.switchScene("/main/FXML/LoginPage.fxml", "Flashcards Made Easy - Login");
            } catch (IOException e) {
                showError("Error loading login page: " + e.getMessage());
            }
        }
    }

    private boolean saveUserInfo() {
        String newUsername = createUsernameField.getText();
        String newPassword = createPasswordField.getText();

        if (newUsername.isEmpty() || newPassword.isEmpty()) {
            showError("Username and password cannot be empty");
            return false;
        }

        try {
            userService.registerUser(newUsername, newPassword);
            return true;
        } catch (SQLException e) {
            showError("Error registering user. Please try again.");
            return false;
        }
    }

    private void showError(String message) {
        if (signUpError != null) {
            signUpError.setText(message);
            signUpError.setVisible(true);
        } else {
            System.err.println("Error: " + message);
        }
    }
}