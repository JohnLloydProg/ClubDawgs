package com.unidawgs.le5.clubdawgs;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Minigame2_SceneFactory {
    private int boardWidth;
    private int boardHeight;
    private Image startBackgroundImg;
    private Image startButtonImg;
    private Image scoreButtonImg;
    private Image exitButtonImg;
    private Image logoImg;
    private Image topPipeImg;
    private Image bottomPipeImg;
    private Image gameBackgroundImg;
    private Image dogImg;
    private Image gameoverImg;

    public Minigame2_SceneFactory(int width, int height) {
        this.boardWidth = width;
        this.boardHeight = height;

        // Load images
        startBackgroundImg = new Image(getClass().getResource("mg2flappydogbg.png").toExternalForm());
        startButtonImg = new Image(getClass().getResource("mg2-startbutton.png").toExternalForm());
        scoreButtonImg = new Image(getClass().getResource("mg2-scorebutton.png").toExternalForm());
        exitButtonImg = new Image(getClass().getResource("mg2-exitbutton.png").toExternalForm());
        logoImg = new Image(getClass().getResource("mg2-logo1.png").toExternalForm());
        topPipeImg = new Image(getClass().getResource("mg2-toppipe.png").toExternalForm());
        bottomPipeImg = new Image(getClass().getResource("mg2-bottompipe.png").toExternalForm());
        gameBackgroundImg = new Image(getClass().getResource("mg2flappydogbg.png").toExternalForm());
        dogImg = new Image(getClass().getResource("mg2-logo2.png").toExternalForm());
        gameoverImg = new Image(getClass().getResource("mg2-gameover.png").toExternalForm());
    }

    public Scene createStartScene(Stage stage, Runnable startGameAction, Runnable showScoresAction, Runnable exitAction) {
        Pane startPane = new Pane();

        ImageView startBackgroundImageView = new ImageView(startBackgroundImg);
        startBackgroundImageView.setFitWidth(boardWidth);
        startBackgroundImageView.setFitHeight(boardHeight);
        startPane.getChildren().add(startBackgroundImageView);

        ImageView logo = new ImageView(logoImg);
        logo.setFitWidth(300);
        logo.setPreserveRatio(true);
        logo.setLayoutX((boardWidth - logo.getFitWidth()) / 2);
        logo.setLayoutY(55);

        // Start Button
        ImageView startButton = new ImageView(startButtonImg);
        startButton.setFitWidth(75);
        startButton.setPreserveRatio(true);
        startButton.setLayoutX((boardWidth - startButton.getFitWidth()) / 2);
        startButton.setLayoutY(290);
        startButton.setOnMouseClicked(e -> startGameAction.run());

        // Score Button
        ImageView scoreButton = new ImageView(scoreButtonImg);
        scoreButton.setFitWidth(100);
        scoreButton.setPreserveRatio(true);
        scoreButton.setLayoutX((boardWidth / 2) - scoreButton.getFitWidth() - 40);
        scoreButton.setLayoutY(400);
        scoreButton.setOnMouseClicked(e -> showScoresAction.run());

        // Exit Button
        ImageView exitButton = new ImageView(exitButtonImg);
        exitButton.setFitWidth(100);
        exitButton.setPreserveRatio(true);
        exitButton.setLayoutX((boardWidth / 2) + 40);
        exitButton.setLayoutY(400);
        exitButton.setOnMouseClicked(e -> exitAction.run());

        startPane.getChildren().addAll(startButton, scoreButton, exitButton);

        ImageView flyingDog = new ImageView(dogImg);
        flyingDog.setFitWidth(150);
        flyingDog.setPreserveRatio(true);
        flyingDog.setLayoutX(boardWidth);
        flyingDog.setLayoutY(logo.getLayoutY() + 90);
        startPane.getChildren().addAll(logo, flyingDog);

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(2), flyingDog);
        translateTransition.setFromX(boardWidth);
        translateTransition.setToX((boardWidth - flyingDog.getFitWidth()) / 2 - boardWidth);
        translateTransition.setCycleCount(1);

        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.5), flyingDog);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(0.7);
        scaleTransition.setToY(0.7);
        scaleTransition.setCycleCount(1);
        translateTransition.setOnFinished(event -> scaleTransition.play());
        translateTransition.play();

        return new Scene(startPane, boardWidth, boardHeight);
    }



    public Scene createTutorialScene(Stage stage, Runnable startGameAction) {
        Pane preGamePane = new Pane();

        ImageView backgroundImageView = new ImageView(startBackgroundImg);
        backgroundImageView.setFitWidth(boardWidth);
        backgroundImageView.setFitHeight(boardHeight);
        preGamePane.getChildren().add(backgroundImageView);

        Image tutorialImage = new Image(getClass().getResource("mg2-tutorial.png").toExternalForm());
        ImageView tutorialImageView = new ImageView(tutorialImage);

        // tutorial image (e.g., 0.5 for 50% size)
        double scaleFactor = 0.1;
        tutorialImageView.setFitWidth(tutorialImage.getWidth() * scaleFactor);
        tutorialImageView.setFitHeight(tutorialImage.getHeight() * scaleFactor);
        tutorialImageView.setX((boardWidth - tutorialImageView.getFitWidth()) / 2);
        tutorialImageView.setY((boardHeight - tutorialImageView.getFitHeight()) / 2);

        preGamePane.getChildren().add(tutorialImageView);

        Scene preGameScene = new Scene(preGamePane, boardWidth, boardHeight);
        preGameScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                startGameAction.run();
            }
        });

        return preGameScene;
    }


    public Scene createGameScene(Canvas canvas) {
        Pane gamePane = new Pane();
        gamePane.getChildren().add(canvas);
        return new Scene(gamePane, boardWidth, boardHeight);
    }

    public Scene createGameOverScene(Stage stage, int finalScore, Runnable restartAction, Runnable exitAction, Runnable showScoresAction) {
        Pane gameOverPane = new Pane();

        ImageView backgroundImageView = new ImageView(gameBackgroundImg);
        backgroundImageView.setFitWidth(boardWidth);
        backgroundImageView.setFitHeight(boardHeight);
        gameOverPane.getChildren().add(backgroundImageView); // Add background as the first child

        // Game Over Image
        ImageView gameOverImage = new ImageView(gameoverImg);
        gameOverImage.setFitWidth(200);
        gameOverImage.setPreserveRatio(true);
        gameOverImage.setLayoutX((boardWidth - gameOverImage.getFitWidth()) / 2);
        gameOverImage.setLayoutY(75);

        // Score Label
        Label scoreLabel = new Label("Score: " + finalScore);
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setFont(new Font("Arial", 32));
        scoreLabel.setLayoutX((boardWidth - 200) / 2 + 90);
        scoreLabel.setLayoutY(gameOverImage.getLayoutY() + gameOverImage.getFitHeight() + 200);

        // Score Label 2
        Label scoreLabel2 = new Label("Highscore: " + finalScore);
        scoreLabel2.setTextFill(Color.WHITE);
        scoreLabel2.setFont(new Font("Arial", 25));
        scoreLabel2.setLayoutX((boardWidth - 200) / 2 + 90);
        scoreLabel2.setLayoutY(gameOverImage.getLayoutY() + gameOverImage.getFitHeight() + 240);

        ImageView restartButton = new ImageView(new Image(getClass().getResource("mg2-startbutton.png").toExternalForm()));
        restartButton.setFitWidth(100);
        restartButton.setPreserveRatio(true);
        restartButton.setLayoutX((boardWidth - restartButton.getFitWidth()) / 2 -80);
        restartButton.setLayoutY(270);
        restartButton.setOnMouseClicked(event -> restartAction.run());

        ImageView exitButton = new ImageView(new Image(getClass().getResource("mg2-exitbutton.png").toExternalForm()));
        exitButton.setFitWidth(100);
        exitButton.setPreserveRatio(true);
        exitButton.setLayoutX((boardWidth - exitButton.getFitWidth()) / 2 + 80);
        exitButton.setLayoutY(400);
        exitButton.setOnMouseClicked(event -> exitAction.run());

        ImageView scoreButton = new ImageView(new Image(getClass().getResource("mg2-scorebutton.png").toExternalForm()));
        scoreButton.setFitWidth(100);
        scoreButton.setPreserveRatio(true);
        scoreButton.setLayoutX((boardWidth - scoreButton.getFitWidth()) / 2 - 80);
        scoreButton.setLayoutY(400);
        scoreButton.setOnMouseClicked(event -> showScoresAction.run());

        gameOverPane.getChildren().addAll(gameOverImage, restartButton, scoreLabel, scoreLabel2, exitButton, scoreButton);

        return new Scene(gameOverPane, boardWidth, boardHeight);
    }


    public void showScoresWindow(Stage ownerStage) {
        Stage scoresStage = new Stage();
        scoresStage.initModality(Modality.APPLICATION_MODAL);
        scoresStage.initOwner(ownerStage);
        scoresStage.setTitle("High Scores");

        Pane scoresPane = new Pane();
        scoresPane.setStyle("-fx-background-color: lightgray;");

        Label scoresLabel = new Label("High Scores\n1. Player 1 - 100\n2. Player 2 - 90\n...");
        scoresLabel.setFont(new Font("Arial", 24));
        scoresLabel.setLayoutX(50);
        scoresLabel.setLayoutY(50);
        Button backButton = new Button("Back");
        backButton.setLayoutX(50);
        backButton.setLayoutY(200);
        backButton.setOnAction(e -> scoresStage.close());

        scoresPane.getChildren().addAll(scoresLabel, backButton);

        Scene scoresScene = new Scene(scoresPane, 300, 300);
        scoresStage.setScene(scoresScene);
        scoresStage.show();
    }

    public Image getTopPipeImg() {
        return topPipeImg;
    }

    public Image getBottomPipeImg() {
        return bottomPipeImg;
    }

    public Image getGameBackgroundImg() {
        return gameBackgroundImg;
    }
}
