package edu.psu.se411.lab04;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/** Basic JavaFX application for the Maven dependency exercise. */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Label message = new Label("SE411 Lab 04 - Maven and JavaFX");
        StackPane root = new StackPane(message);
        Scene scene = new Scene(root, 500, 250);

        primaryStage.setTitle("SE411 Lab 04");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
