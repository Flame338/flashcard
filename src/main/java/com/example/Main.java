package com.example;

import javafx.application.Application;
import javafx.stage.Stage;
import com.example.services.UserService;
import com.example.utils.DatabaseManager;
import com.example.utils.MySQLConnect;
import com.example.utils.SceneManager;

import java.sql.Connection;

import com.example.services.CardService;
import com.example.services.CardSetService;

public class Main extends Application {
    private DatabaseManager dbManager;
    private SceneManager sceneManager;
    private UserService userService;
    private CardService cardService;
    private CardSetService cardSetService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            dbManager = DatabaseManager.getInstance();
            userService = new UserService(dbManager);
            cardService = new CardService(dbManager);
            cardSetService = new CardSetService(dbManager);
            sceneManager = new SceneManager(primaryStage, userService, cardService, cardSetService);

            // Test database connection
            try (Connection conn = MySQLConnect.connect()) {
                System.out.println("Database connected successfully");
            } catch (Exception e) {
                System.err.println("Database connection failed: " + e.getMessage());
            }

            sceneManager.switchToLoginScene();
        } catch (Exception e) {
            System.err.println("Application failed to start: " + e.getMessage());
            e.printStackTrace();
        }
    }
}