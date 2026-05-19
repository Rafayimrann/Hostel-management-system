package com.hms;

import com.hms.ui.LoginScreen;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * HMS Application Entry Point.
 * Launches the JavaFX application with the Login screen.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hostel Management System");
        LoginScreen loginScreen = new LoginScreen(primaryStage);
        loginScreen.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
