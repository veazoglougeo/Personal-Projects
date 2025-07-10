package org.example.taskmanager.app;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    private MainController mainController;

    @Override
    public void start(Stage primaryStage) {
        mainController = new MainController(primaryStage);
        mainController.initialize();
    }

    public static void main(String[] args) {
        launch(args);
    }
}