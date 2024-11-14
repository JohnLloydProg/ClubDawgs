package com.unidawgs.le5.clubdawgs;

import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
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
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


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

    private List<ScoreEntry> highScores;

    public Minigame2_SceneFactory(int width, int height) {
        this.boardWidth = width;
        this.boardHeight = height;

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

        highScores = new ArrayList<>();
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

        ImageView startButton = new ImageView(startButtonImg);
        startButton.setFitWidth(75);
        startButton.setPreserveRatio(true);
        startButton.setLayoutX((boardWidth - startButton.getFitWidth()) / 2);
        startButton.setLayoutY(305);
        startButton.setOnMouseClicked(e -> startGameAction.run());

        // Score Button
        ImageView scoreButton = new ImageView(scoreButtonImg);
        scoreButton.setFitWidth(100);
        scoreButton.setPreserveRatio(true);
        scoreButton.setLayoutX((boardWidth / 2) - scoreButton.getFitWidth() - 40);
        scoreButton.setLayoutY(390);
        scoreButton.setOnMouseClicked(e -> showScoresAction.run());

        // Exit Button
        ImageView exitButton = new ImageView(exitButtonImg);
        exitButton.setFitWidth(100);
        exitButton.setPreserveRatio(true);
        exitButton.setLayoutX((boardWidth / 2) + 40);
        exitButton.setLayoutY(390);
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

        double scaleFactor = 0.1;
        tutorialImageView.setFitWidth(tutorialImage.getWidth() * scaleFactor);
        tutorialImageView.setFitHeight(tutorialImage.getHeight() * scaleFactor);
        tutorialImageView.setX((boardWidth - tutorialImageView.getFitWidth()) / 2);
        tutorialImageView.setY((boardHeight - tutorialImageView.getFitHeight()) / 2);

        preGamePane.getChildren().add(tutorialImageView);

        Font customFont = Minigame2_FontUtils.getRetroGamingFont(20);
        Text startText = new Text("PRESS SPACE TO START");
        startText.setFont(customFont);
        startText.setFill(Color.BLACK);
        startText.setX((boardWidth - startText.getBoundsInLocal().getWidth()) / 2);
        startText.setY(boardHeight - 120);

        preGamePane.getChildren().add(startText);

        Timeline blinkTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, event -> startText.setVisible(true)),
                new KeyFrame(Duration.seconds(0.8), event -> startText.setVisible(false)),
                new KeyFrame(Duration.seconds(1.6), event -> startText.setVisible(true))
        );
        blinkTimeline.setCycleCount(Timeline.INDEFINITE);
        blinkTimeline.play();

        Scene preGameScene = new Scene(preGamePane, boardWidth, boardHeight);
        preGameScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                blinkTimeline.stop();
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
        gameOverPane.getChildren().add(backgroundImageView);

        ImageView gameOverImage = new ImageView(gameoverImg);
        gameOverImage.setFitWidth(200);
        gameOverImage.setPreserveRatio(true);
        gameOverImage.setLayoutX((boardWidth - gameOverImage.getFitWidth()) / 2);
        gameOverImage.setLayoutY(75);

        Font customFont = Minigame2_FontUtils.getRetroGamingFont(20);

        Label scoreLabel = new Label("SCORE:\n" + finalScore);
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setFont(customFont);
        scoreLabel.setLayoutX((boardWidth - 200) / 2 + 90);
        scoreLabel.setLayoutY(gameOverImage.getLayoutY() + gameOverImage.getFitHeight() + 190);

        int highScore = highScores.isEmpty() ? 0 : highScores.get(0).getScore();
        Label highScoreLabel = new Label("HIGHSCORE:\n" + highScore);
        highScoreLabel.setTextFill(Color.WHITE);
        highScoreLabel.setFont(customFont);
        highScoreLabel.setLayoutX((boardWidth - 200) / 2 + 90);
        highScoreLabel.setLayoutY(gameOverImage.getLayoutY() + gameOverImage.getFitHeight() + 250);

        ImageView restartButton = new ImageView(new Image(getClass().getResource("mg2-startbutton.png").toExternalForm()));
        restartButton.setFitWidth(100);
        restartButton.setPreserveRatio(true);
        restartButton.setLayoutX((boardWidth - restartButton.getFitWidth()) / 2 - 80);
        restartButton.setLayoutY(270);
        restartButton.setOnMouseClicked(event -> restartAction.run());

        ImageView exitButton = new ImageView(new Image(getClass().getResource("mg2-exitbutton.png").toExternalForm()));
        exitButton.setFitWidth(100);
        exitButton.setPreserveRatio(true);
        exitButton.setLayoutX((boardWidth - exitButton.getFitWidth()) / 2 + 80);
        exitButton.setLayoutY(390);
        exitButton.setOnMouseClicked(event -> exitAction.run());

        ImageView scoreButton = new ImageView(new Image(getClass().getResource("mg2-scorebutton.png").toExternalForm()));
        scoreButton.setFitWidth(100);
        scoreButton.setPreserveRatio(true);
        scoreButton.setLayoutX((boardWidth - scoreButton.getFitWidth()) / 2 - 80);
        scoreButton.setLayoutY(390);
        scoreButton.setOnMouseClicked(event -> showScoresAction.run());

        gameOverPane.getChildren().addAll(gameOverImage, restartButton, scoreLabel, highScoreLabel, exitButton, scoreButton);

        return new Scene(gameOverPane, boardWidth, boardHeight);
    }


    // Method to add a score
    public void addScore(String playerName, int score) {
        highScores.add(new ScoreEntry(playerName, score));
        Collections.sort(highScores, Comparator.comparingInt(ScoreEntry::getScore).reversed());
    }

    // Method to get the top scores as a formatted string
    public String getTopScores(int count) {
        StringBuilder topScores = new StringBuilder();
        for (int i = 0; i < Math.min(count, highScores.size()); i++) {
            ScoreEntry entry = highScores.get(i);
            topScores.append(entry.getPlayerName()).append(" - ").append(entry.getScore()).append("\n");
        }
        return topScores.toString();
    }

    public void showScoresWindow(Stage ownerStage) {
        Stage scoresStage = new Stage();
        scoresStage.initModality(Modality.APPLICATION_MODAL);
        scoresStage.initOwner(ownerStage);
        scoresStage.setTitle("High Scores");

        Pane scoresPane = new Pane();
        scoresPane.setStyle("-fx-background-color: #f0e98e;");

        Label scoresLabel = new Label("High Scores:");
        scoresLabel.setFont(Minigame2_FontUtils.getRetroGamingFont(24));
        scoresLabel.setTextFill(Color.web("#44abf1"));
        scoresLabel.setLayoutX(20);
        scoresLabel.setLayoutY(20);

        Label topScoresLabel = new Label(getTopScores(5));
        topScoresLabel.setFont(Minigame2_FontUtils.getRetroGamingFont(18));
        topScoresLabel.setTextFill(Color.web("#44abf1"));
        topScoresLabel.setLayoutX(20);
        topScoresLabel.setLayoutY(scoresLabel.getLayoutY() + scoresLabel.getHeight() + 30);

        Button backButton = new Button("Back");
        backButton.setFont(Minigame2_FontUtils.getRetroGamingFont(18));
        backButton.setTextFill(Color.web("#44abf1"));
        backButton.setPrefWidth(80);
        backButton.setPrefHeight(30);
        backButton.setLayoutX(20);
        backButton.setLayoutY(245);
        backButton.setOnAction(e -> scoresStage.close());

        scoresPane.getChildren().addAll(scoresLabel,topScoresLabel, backButton);

        Scene scoresScene = new Scene(scoresPane, 300, 300);
        scoresStage.setScene(scoresScene);
        scoresStage.show();
    }

    public static class ScoreEntry {
        private final String playerName;
        private final int score;

        public ScoreEntry(String playerName, int score) {
            this.playerName = playerName;
            this.score = score;
        }

        public String getPlayerName() {
            return playerName;
        }

        public int getScore() {
            return score;
        }
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