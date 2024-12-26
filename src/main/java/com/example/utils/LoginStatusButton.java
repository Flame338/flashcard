package com.example.utils;

import javafx.scene.control.Button;

public class LoginStatusButton extends Button {
    public enum LoginStatus {
        LOGGED_IN,
        SKIPPED,
        FUTURE
    }

    public LoginStatusButton(String text) {
        super(text);
        setMinWidth(30);
        setMinHeight(30);
    }

    public void setLoginStatus(LoginStatus status) {
        switch (status) {
            case LOGGED_IN:
                setStyle("-fx-background-color: #4CAF50;"); // Green
                break;
            case SKIPPED:
                setStyle("-fx-background-color: #F44336;"); // Red
                break;
            case FUTURE:
                setStyle("-fx-background-color: #BDBDBD;"); // Gray
                break;
        }
    }
}