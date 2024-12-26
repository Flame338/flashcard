package com.example.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import java.io.IOException;
import java.util.function.Consumer;

import com.example.controllers.*;
import com.example.models.CardSet;
import com.example.services.CardService;
import com.example.services.CardSetService;
import com.example.services.UserService;

public class SceneManager {
    private Stage primaryStage;
    private UserService userService;
    private CardService cardService;
    private CardSetService cardSetService;
    private String currentUsername;

    public SceneManager(Stage primaryStage, UserService userService, CardService cardService, CardSetService cardSetService) {
        this.primaryStage = primaryStage;
        this.userService = userService;
        this.cardService = cardService;
        this.cardSetService = cardSetService;
    }

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }

    public void switchScene(String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(this::createController);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);

        if (loader.getController() instanceof FrontPageController) {
            FrontPageController controller = loader.getController();
            controller.setCurrentUsername(currentUsername);
        }

        primaryStage.show();
    }

    public FXMLLoader switchSceneWithLoader(String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(this::createController);
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle(title);
        primaryStage.show();
        return loader;
    }

    public void switchToCardPage(CardSet cardSet) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/FXML/CardPage.fxml"));
        loader.setControllerFactory(this::createController);
        Parent root = loader.load();
        CardPageController controller = loader.getController();
        controller.setCardSet(cardSet);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Flashcards - " + cardSet.getName());
        primaryStage.show();
    }

    public void switchToLoginScene() throws IOException {
        switchScene("/main/FXML/LoginPage.fxml", "Flashcards Made Easy - Login");
    }

    public void switchToTestPage(String username) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/FXML/TestPage.fxml"));
        loader.setControllerFactory(this::createController);
        Parent root = loader.load();
        TestPageController controller = loader.getController();
        controller.startTest(username);
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Flashcards Made Easy - Test");
        primaryStage.show();
    }

    public void openPopup(String fxmlPath, String title, Consumer<Object> controllerConsumer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(this::createController);
            Parent root = loader.load();
            
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle(title);
            popupStage.setScene(new Scene(root));
            
            Object controller = loader.getController();
            popupStage.showAndWait();
            
            controllerConsumer.accept(controller);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Object createController(Class<?> type) {
        if (type == LoginPageController.class) {
            LoginPageController controller = new LoginPageController();
            controller.initializeServices(userService, this);
            return controller;
        } else if (type == SignUpPageController.class) {
            SignUpPageController controller = new SignUpPageController();
            controller.initializeServices(userService, this);
            return controller;
        } else if (type == FrontPageController.class) {
            FrontPageController controller = new FrontPageController();
            controller.initializeServices(cardSetService, userService, this);
            return controller;
        } else if (type == CardPageController.class) {
            CardPageController controller = new CardPageController();
            controller.initializeServices(cardService, this);
            return controller;
        } else if (type == CardEditorPageController.class) {
            return new CardEditorPageController();
        } else if (type == CardSetPageController.class) {
            CardSetPageController controller = new CardSetPageController();
            controller.initializeServices(cardSetService);
            return controller;
        } else if (type == TestPageController.class) {
            TestPageController controller = new TestPageController();
            controller.initializeServices(cardSetService, this);
            return controller;
        }
        throw new IllegalArgumentException("Unknown controller class: " + type.getName());
    }
}