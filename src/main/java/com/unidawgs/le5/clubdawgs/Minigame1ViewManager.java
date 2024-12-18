package com.unidawgs.le5.clubdawgs;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Minigame1ViewManager {
    //LEADERBOARD
    private String idToken;
    private String roomId;
    private ArrayList<JsonObject> highScores = new ArrayList<>();

    private static final int HEIGHT = 768;
    private static final int WIDTH = 1024;
    private static final double MENU_BUTTON_START_X = 100;
    private static final double MENU_BUTTON_START_Y = 150;
    private static final String BUTTON_SFX = "file:src/model/resources/robotSFX.wav";

    AnchorPane mainPane;
    private Scene mainScene;
    private Stage mainStage;

    private Minigame1SubScene creditsSubScene;
    private Minigame1SubScene helpSubScene;
    private Minigame1SubScene scoreSubScene;
    private Minigame1SubScene shipChooserSubScene;

    private Minigame1SubScene sceneToHide;
    // Add background images for the game
	 private final Image[] BACKGROUNDS = new Image[]{
        new Image(getClass().getResource("bg1.jpg").toExternalForm()), // Background 1
        new Image(getClass().getResource("bg2.jpg").toExternalForm()), // Background 2
        new Image(getClass().getResource("bg3.jpg").toExternalForm())  // Background 3
    };
    private int currentBackgroundIndex = 0;
    private long lastBackgroundChangeTime = System.currentTimeMillis(); // Time when background was last changed


	 //Sound FX
	private MediaPlayer mainMenuPlayer;
    private MediaPlayer bgSoundPlayer;
    private MediaPlayer gameOverPlayer;
    private MediaPlayer scoreSoundPlayer;
    private MediaPlayer btnSoundPlayer;

	 private void loadSounds() {
		mainMenuPlayer = createMediaPlayer("/com/unidawgs/le5/clubdawgs/mg1_mainmenusound.mp3");
		bgSoundPlayer = createMediaPlayer("/com/unidawgs/le5/clubdawgs/mg1_gamebgsound.mp3");
		gameOverPlayer = createMediaPlayer("/com/unidawgs/le5/clubdawgs/mg1_gameoversound.mp3");
		scoreSoundPlayer = createMediaPlayer("/com/unidawgs/le5/clubdawgs/mg1_scoresound.mp3");
        btnSoundPlayer = createMediaPlayer("/com/unidawgs/le5/clubdawgs/mg1_btnsound.mp3");
  	}

	private MediaPlayer createMediaPlayer(String filePath) {
        Media sound = new Media(getClass().getResource(filePath).toExternalForm());
        return new MediaPlayer(sound);
    }


    List<Minigame1Button> menuButtons;

    public Minigame1ViewManager (String idToken, String roomId) {    
        menuButtons = new ArrayList<>();
        mainPane = new AnchorPane();   
        mainScene = new Scene(mainPane, WIDTH, HEIGHT);
        mainStage = new Stage();    
        mainStage.setScene(mainScene);
        createSubScenes();    
        createButtons();
        createBackground();    
        createLogo();
        loadSounds();   
        playMainMenuSound();
        this.roomId = roomId;    
        this.idToken = idToken;
    }

    private void showSubScene(Minigame1SubScene subScene) {
        playBtnSoundPlayer();
        if (sceneToHide != null) {
            sceneToHide.moveSubScene();
        }
        subScene.moveSubScene();
        sceneToHide = subScene;
    }

    private void createSubScenes() {
        creditsSubScene = new Minigame1SubScene();
        helpSubScene = new Minigame1SubScene();
        scoreSubScene = new Minigame1SubScene();
        shipChooserSubScene = new Minigame1SubScene();

        //mainPane.getChildren().addAll(creditsSubScene, helpSubScene, scoreSubScene, shipChooserSubScene);

        createScoreSubScene();
        createHelpSubScene();
        createCreditsSubScene();

    }

    private void createScoreSubScene() {
        scoreSubScene = new Minigame1SubScene();
        mainPane.getChildren().add(scoreSubScene);
        InfoLabel score = new InfoLabel("< Scores >");
        score.setLayoutX(115);
        score.setLayoutY(20);
/*        VBox scoreContainer = new VBox();
        scoreContainer.setLayoutX(150);
        scoreContainer.setLayoutY(150);*/
        // Create a GridPane to display scores in a table-like layout
        GridPane scoreGrid = new GridPane();
        scoreGrid.setLayoutX(150);
        scoreGrid.setLayoutY(50);
        scoreGrid.setVgap(10);  // Vertical gap between rows
        scoreGrid.setHgap(150);  // Horizontal gap between columns

        scoreGrid.setPadding(new Insets(0));
        // Add the column headers (Name, Score)
        Label nameHeader = new Label("Name");
        nameHeader.setFont(Font.font("Verdana", 20));
        nameHeader.setTextFill(Color.WHITE);
        Label scoreHeader = new Label("Score");
        scoreHeader.setFont(Font.font("Verdana", 20));
        scoreHeader.setTextFill(Color.WHITE);

        // Add headers to GridPane
        scoreGrid.add(nameHeader, 0, 0);  // Column 0, Row 0
        scoreGrid.add(scoreHeader, 1, 0);  // Column 1, Row 0
        //Added
        ArrayList<Label> scoreList = new ArrayList<>();
        highScores = Firebase.getLeaderboard(idToken,roomId,"minigame1");
        int rowIndex = 1;  // Start from row 1 (row 0 is used for headers)
        for(JsonObject scoreObject : highScores){
            String username = Firebase.getUsername(scoreObject.get("localId").getAsString(),idToken);
            String scoreValue = scoreObject.get("score").getAsString();
            // Create labels for each player's username and score
            Label playerName = new Label(username);
            playerName.setFont(Font.font("Verdana", 18));
            Label playerScore = new Label(scoreValue);
            playerScore.setFont(Font.font("Verdana", 18));
            // Add labels to the GridPane at the appropriate row
            scoreGrid.add(playerName, 0, rowIndex);  // Column 0, Row `rowIndex`
            scoreGrid.add(playerScore, 1, rowIndex); // Column 1, Row `rowIndex`

            // Increment rowIndex for the next entry
            rowIndex++;
            if(rowIndex > 5) {
                break;
            }
            //.add(new Label(username+"		  "+scoreObject.get("score").getAsString()));
        }
        //
        Label scoreHeading = new Label("     Name			Score   ");
        scoreHeading.setUnderline(true);
        scoreHeading.setFont(Font.font("Verdana",20));
        for (Label scoreLabel : scoreList) {
            scoreLabel.setFont(Font.font("Verdana", 20));
        }

        VBox scoreContainer = new VBox();
        scoreContainer.setLayoutX(150);
        scoreContainer.setLayoutY(150);
        scoreContainer.setSpacing(1); // Space between title and grid
        scoreContainer.setPadding(new Insets(0, 0, 0, 0));
        scoreContainer.setBackground(new Background(new BackgroundFill(Color.web("#5BC0BE"), null, new Insets(-20, -20, -20, -20))));
        scoreContainer.getChildren().addAll(scoreGrid);
        scoreSubScene.getPane().getChildren().addAll(score, scoreContainer);//, score1, score2, score3;

    }
    private void createHelpSubScene() {
        helpSubScene = new Minigame1SubScene();
        mainPane.getChildren().add(helpSubScene);
        InfoLabel help = new InfoLabel("< TUTORIAL >");
        help.setLayoutX(120); //Changed 150 -> 120
        help.setLayoutY(20);
        GridPane helpGrid = new GridPane();
        helpGrid.setLayoutX(100);
        helpGrid.setLayoutY(100);
        helpGrid.setHgap(20);
        helpGrid.setVgap(20);

        ImageView ship = new ImageView(new Image(getClass().getResource("mg1-player.png").toExternalForm(), 80, 80, true, false));
        ImageView meteor1 = new ImageView(), meteor2 = new ImageView();
        meteor1.setImage(new Image(getClass().getResource("mg1-alien2.png").toExternalForm(), 80, 80, true, false));
        meteor2.setImage(new Image(getClass().getResource("mg1-alien1.png").toExternalForm(), 80, 80, true, false));

        Label shipHelp 	 = new Label("This is your character. Control it with WASD and \nClick for shooting your bullets. \nThere are also powerups to help \nyou as you progress.");
        Label meteorHelp = new Label("These are enemies.\nAvoid them or shoot them.\nAs you score higher, they will get faster.");
        shipHelp.setTextFill(Color.WHITE);
        meteorHelp.setTextFill(Color.WHITE);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                meteor1.setRotate(90+now/10000000l);
                meteor2.setRotate(180+now/10000000l);
                ship.setRotate(-now/10000000l);
            }
        };
        timer.start();
        /* gridpane:
         * ___0_|__1_|__2_|_3_
         * 0|___|____|____|__
         * 1|___|____|____|__
         * 2|___|____|____|__
         * 3|___|____|____|___
         */

        helpGrid.add(ship, 0, 0);
        helpGrid.add(shipHelp, 1, 0);
        helpGrid.add(meteor1, 0, 1);
        helpGrid.add(meteor2, 2, 1);
        helpGrid.add(meteorHelp, 1, 1);
        helpSubScene.getPane().getChildren().addAll(help, helpGrid);

    }
    private void createCreditsSubScene() {
        creditsSubScene = new Minigame1SubScene();
        mainPane.getChildren().add(creditsSubScene);

        InfoLabel credits = new InfoLabel("< Credits >");
        credits.setLayoutX(120);
        credits.setLayoutY(20);
        Label credit0 = new Label("Concept Art by: Audrize Cruz");
        Label credit1 = new Label("Coded by: Audrize Cruz & Jared Santos");
        credit0.setTextFill(Color.WHITE);
        credit1.setTextFill(Color.WHITE);

        String[]link    = new String[4];
        link[0] = "https://github.com/JohnLloydProg/ClubDawgs";
        link[1] = "https://freesound.org/";
        link[2] = "http://soundbible.com/";
        link[3] = "https://www.freesoundslibrary.com/";


        Hyperlink link0, link1, link2, link3;
        link0 = new Hyperlink(link[0]);
        link1 = new Hyperlink(link[1]);
        link2 = new Hyperlink(link[2]);
        link3 = new Hyperlink(link[3]);

        VBox creditsBox = new VBox(10, credit0, credit1, link0, link1, link2, link3);

        creditsBox.setLayoutX(50);
        creditsBox.setLayoutY(80);
        creditsSubScene.getPane().getChildren().addAll(credits, creditsBox);

        Application app = new Application() {@Override public void start(Stage primaryStage) throws Exception{}};
        HostServices services = app.getHostServices();

        link0.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                services.showDocument(link[0]);
            }
        });
        link1.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                services.showDocument(link[1]);
            }
        });
        link2.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                services.showDocument(link[2]);
            }
        });
        link3.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                services.showDocument(link[3]);
            }
        });
    }



    private void createButtons() {
        createStartButton();
        createScoresButton();
        createHelpButton();
        createCreditsButton();
        createExitButton();
    }

    public Stage getMainStage() {
        return mainStage;
    }

    private void addMenuButtons(Minigame1Button button) {
        button.setLayoutX(MENU_BUTTON_START_X);
        button.setLayoutY(MENU_BUTTON_START_Y + menuButtons.size() * 100);
        menuButtons.add(button);
        mainPane.getChildren().add(button);

    }

    private void createStartButton() {
        Minigame1Button startButton = new Minigame1Button("",getClass().getResource("mg-start.png").toExternalForm());
        addMenuButtons(startButton);
        startButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override //LEADERBOARD CHANGE
            public void handle(ActionEvent arg0) {
                 //put button sound here always sa start button
                 if(sceneToHide != null){
                    sceneToHide.moveSubScene();
                    sceneToHide = null;
                }

                gameOverPlayer.play();
                gameOverPlayer.stop();
                mainStage.hide();
                mainMenuPlayer.stop();
                Minigame1 gameViewManagger = new Minigame1();
                gameViewManagger.startGame(mainStage, roomId);
            }
        });
    }

    private void createScoresButton() {
        Minigame1Button scoresButton = new Minigame1Button("",getClass().getResource("mg-scores.png").toExternalForm());
        addMenuButtons(scoresButton);
        scoresButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                createScoreSubScene(); //LEADERBOARD
                showSubScene(scoreSubScene); //LEADERBOARD
            }
        });
    }

    private void createHelpButton() {
        Minigame1Button helpButton = new Minigame1Button("",getClass().getResource("mg-tutorial.png").toExternalForm());
        addMenuButtons(helpButton);
        helpButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {

                showSubScene(helpSubScene);
            }
        });
    }

    private void createCreditsButton() {
        Minigame1Button creditsButton = new Minigame1Button("",getClass().getResource("mg-credits.png").toExternalForm());
        addMenuButtons(creditsButton);

        creditsButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {

                showSubScene(creditsSubScene);
            }
        });
    }

    private void createExitButton() {
        Minigame1Button exitButton = new Minigame1Button("",getClass().getResource("mg-exit.png").toExternalForm());
        addMenuButtons(exitButton);
        exitButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {

                mainStage.close();
                // mainStage.close();
            }
        });
    }

    private void createBackground() {
        Image bgImage = new Image(getClass().getResource("mg-bg.png").toExternalForm(), 256, 256, false, true);
        BackgroundImage bg = new BackgroundImage(bgImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT, new BackgroundSize(1024, 800, false, false, false, false));
        mainPane.setBackground(new Background(bg));
    }

    private void createLogo() {
        Image logoImage = new Image(getClass().getResource("mg-logo.png").toExternalForm(), 500, 100, false, false);
        ImageView logo = new ImageView(logoImage);
        logo.setLayoutX(400);
        logo.setLayoutY(50);
        logo.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                logo.setEffect(new DropShadow(100, Color.CYAN));
            }
        });
        logo.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                logo.setEffect(null);
            }
        });
        mainPane.getChildren().add(logo);
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

      private void playBtnSoundPlayer() {
        btnSoundPlayer.stop();
        btnSoundPlayer.play();
      }
 
}