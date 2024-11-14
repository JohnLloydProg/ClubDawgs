package com.unidawgs.le5.clubdawgs;

import com.unidawgs.le5.clubdawgs.objects.User;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;

public class Minigame2_Main {
    private static final int PIPE_HEIGHT = 512;
    private int boardWidth;
    private int boardHeight;
    private Minigame2_Sprite minigame2Sprite;
    private ArrayList<Minigame2_Pipe> pipes;
    private double velocityX = -1;
    private double velocityY = 0;
    private double gravity = 0.15;
    private boolean gameOver = false;
    private double score = 0;
    private double finalScore = 0;
    private AnimationTimer gameLoop;
    private long lastPipeTime = 0;
    private Random random = new Random();
    private Label scoreLabel;
    private Stage stage;
    private GraphicsContext gc;
    private Minigame2_SceneFactory minigame2SceneFactory;
    private Scene startScene, gameScene, gameOverScene, tutorialScene;
    private Canvas gameCanvas;
    private boolean isPausedBeforeDrop = true;
    private long pauseStartTime;
    private static final long PAUSE_DURATION = 500_000_000L;
    private MediaPlayer mainMenuPlayer;
    private MediaPlayer bgSoundPlayer;
    private MediaPlayer gameOverPlayer;
    private MediaPlayer scoreSoundPlayer;
    private MediaPlayer tapSoundPlayer;
    private String roomId;

    public Minigame2_Main(int width, int height, String roomId) {
        this.boardWidth = width;
        this.boardHeight = height;
        this.minigame2SceneFactory = new Minigame2_SceneFactory(width, height);
        this.pipes = new ArrayList<>();
        this.roomId = roomId;
        loadSounds();
    }

    private void loadSounds() {
        mainMenuPlayer = createMediaPlayer("/com/unidawgs/le5/clubdawgs/mg2_mainmenusound.mp3");
        bgSoundPlayer = createMediaPlayer("/com/unidawgs/le5/clubdawgs/mg2_gamebgsound.mp3");
        gameOverPlayer = createMediaPlayer("/com/unidawgs/le5/clubdawgs/mg2_gameoversound.mp3");
        scoreSoundPlayer = createMediaPlayer("/com/unidawgs/le5/clubdawgs/mg2_scoresound.mp3");
        tapSoundPlayer = createMediaPlayer("/com/unidawgs/le5/clubdawgs/mg2_tapsound.wav");
    }

    private MediaPlayer createMediaPlayer(String filePath) {
        Media sound = new Media(getClass().getResource(filePath).toExternalForm());
        return new MediaPlayer(sound);
    }

    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        startScene = minigame2SceneFactory.createStartScene(stage,this::showTutorialScene,this::showScores,stage::close);
        gameScene = minigame2SceneFactory.createGameScene(createGameCanvas());
        gameOverScene = minigame2SceneFactory.createGameOverScene(stage,(int) finalScore, this::restartGame, stage::close,this::showScores);

        tutorialScene = minigame2SceneFactory.createTutorialScene(stage, this::startGame);

        this.stage.setOnCloseRequest(e -> {
            bgSoundPlayer.stop();
        });

        primaryStage.setScene(startScene);
        primaryStage.show();

        playMainMenuSound();

        createGameLoop();
    }

    private Canvas createGameCanvas() {
        gameCanvas = new Canvas(boardWidth, boardHeight);
        gc = gameCanvas.getGraphicsContext2D();
        return gameCanvas;
    }

    private void showTutorialScene() {
        stage.setScene(tutorialScene);
        mainMenuPlayer.stop();
        playBGSound();
    }

    private void startGame() {
        resetGame();
        stage.setScene(gameScene);
        gameCanvas.requestFocus();
        gameLoop.start();
    }

    private void restartGame() {
        resetGame();
        stage.setScene(gameScene);
        gameCanvas.requestFocus();
        gameLoop.start();
        mainMenuPlayer.stop();
        bgSoundPlayer.play();
    }

    private void resetGame() {
        if (minigame2Sprite == null) {
            Image dogImage = new Image(getClass().getResource("mg2-dog-sprite-big-right.png").toExternalForm());
            Image airplaneImage = new Image(getClass().getResource("mg2-airplane.png").toExternalForm());
            minigame2Sprite = new Minigame2_Sprite(dogImage, airplaneImage, boardWidth / 8, boardHeight / 4, 31, 24);
        } else {
            minigame2Sprite.reset(boardWidth / 8, boardHeight / 4);
        }
        velocityY = 0;
        pipes.clear();
        gameOver = false;
        score = 0;
        finalScore = 0;
        lastPipeTime = 0;

        isPausedBeforeDrop = true;
        pauseStartTime = System.nanoTime();
    }

    private void createGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastPipeTime >= 3000000000L && !isPausedBeforeDrop) {
                    placePipes();
                    lastPipeTime = now;
                }
                move();
                draw();
                if (gameOver) {
                    stop();
                    finalScore = (int) score;
                    User user = Main.getUser();
                    user.setCurrency(Firebase.changeCurrency(user.getLocalId(), user.getIdToken(), (int) finalScore/5));
                    Firebase.updateLeaderboard(user.getLocalId(), user.getIdToken(), roomId, "FlappyDawg", (int) finalScore);
                    minigame2SceneFactory.addScore("Player", (int) finalScore);
                    stage.setScene(minigame2SceneFactory.createGameOverScene(stage,(int) finalScore, Minigame2_Main.this::restartGame,stage::close, Minigame2_Main.this::showScores)
                    );
                    bgSoundPlayer.stop();
                    playGameOverSound();
                    mainMenuPlayer.stop();
                    PauseTransition delay = new PauseTransition(Duration.seconds(1));
                    delay.setOnFinished(event -> {
                        playMainMenuSound();
                    });
                    delay.play();
                }
            }
        };

        gameScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE && !isPausedBeforeDrop) {
                velocityY = -3.8;
                playTapSound();
                if (gameOver) {
                    restartGame();
                }
            }
        });
    }
    private void move() {
        if (isPausedBeforeDrop) {
            if (System.nanoTime() - pauseStartTime >= PAUSE_DURATION) {
                isPausedBeforeDrop = false;
            }
            return;
        }

        velocityY += gravity;
        minigame2Sprite.move(velocityY, boardHeight);
        for (Minigame2_Pipe pipe : pipes) {
            pipe.move(velocityX);
            if (!pipe.isPassed() && minigame2Sprite.getX() > pipe.getX() + pipe.getWidth()) {
                score += 0.5;
                velocityX -= 0.1;
                pipe.setPassed(true);
                playScoreSound();
            }
            if (minigame2Sprite.collidesWith(pipe)) {
                gameOver = true;
                finalScore = (int) score;
            }
        }
        if (minigame2Sprite.getY() + minigame2Sprite.getHeight() > boardHeight || minigame2Sprite.getY() < 0) {
            gameOver = true;
            finalScore = (int) score;
        }
        pipes.removeIf(pipe -> pipe.getX() + pipe.getWidth() < 0);
    }

    private void draw() {
        gc.drawImage(minigame2SceneFactory.getGameBackgroundImg(), 0, 0, boardWidth, boardHeight);

        double airplaneScaleFactor = 0.1;
        double airplaneWidth = minigame2Sprite.getAirplaneImage().getWidth() * airplaneScaleFactor;
        double airplaneHeight = minigame2Sprite.getAirplaneImage().getHeight() * airplaneScaleFactor;

        double airplaneXOffset = -6;
        double airplaneYOffset = 7;

        gc.drawImage(
                minigame2Sprite.getDogImage(),
                minigame2Sprite.getX(),
                minigame2Sprite.getY(),
                minigame2Sprite.getWidth(),
                minigame2Sprite.getHeight()
        );

        gc.drawImage(
                minigame2Sprite.getAirplaneImage(),
                minigame2Sprite.getX() + airplaneXOffset,
                minigame2Sprite.getY() + airplaneYOffset,
                airplaneWidth,
                airplaneHeight
        );

        for (Minigame2_Pipe pipe : pipes) {
            gc.drawImage(pipe.getImage(), pipe.getX(), pipe.getY(), pipe.getWidth(), pipe.getHeight());
        }

        String scoreText = String.valueOf((int) score);
        gc.setFill(Color.WHITE);
        gc.setFont(Minigame2_FontUtils.getRetroGamingFont(35));

        javafx.scene.text.Text text = new javafx.scene.text.Text(scoreText);
        text.setFont(gc.getFont());
        double textWidth = text.getLayoutBounds().getWidth();

        double xPosition = (boardWidth - textWidth) / 2;
        double yPosition = 150;

        gc.fillText(scoreText, xPosition, yPosition);
    }

    private void placePipes() {
        int randomPipeY = -PIPE_HEIGHT / 4 - random.nextInt(PIPE_HEIGHT / 2);
        int openingSpace = boardHeight / 4;
        Image topPipeImg = minigame2SceneFactory.getTopPipeImg();
        Image bottomPipeImg = minigame2SceneFactory.getBottomPipeImg();
        Minigame2_Pipe topPipe = new Minigame2_Pipe(topPipeImg, boardWidth, randomPipeY, 64, PIPE_HEIGHT);
        pipes.add(topPipe);
        Minigame2_Pipe bottomPipe = new Minigame2_Pipe(bottomPipeImg, boardWidth, topPipe.getY() + topPipe.getHeight() + openingSpace, 64, PIPE_HEIGHT);
        pipes.add(bottomPipe);
    }

    private void showScores() {
        minigame2SceneFactory.showScoresWindow(stage);
    }

    // Methods to play sounds
    private void playMainMenuSound() {
        mainMenuPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mainMenuPlayer.play();
    }

    private void playBGSound() {
        bgSoundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        bgSoundPlayer.play();
    }

    private void playGameOverSound() {
        gameOverPlayer.stop();
        gameOverPlayer.play();
    }

    private void playScoreSound() {
        scoreSoundPlayer.stop();
        scoreSoundPlayer.play();
    }

    private void playTapSound() {
        tapSoundPlayer.stop();
        tapSoundPlayer.play();
    }
}
