package com.unidawgs.le5.clubdawgs;

import javafx.application.Application;
import javafx.stage.Stage;

public class Minigame2_App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        int boardWidth = 360;
        int boardHeight = 640;

        Minigame2_Main minigame2Main = new Minigame2_Main(boardWidth, boardHeight);
        minigame2Main.start(primaryStage);

        primaryStage.setTitle("Flappy Dawg");
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
