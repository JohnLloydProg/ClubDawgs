package com.unidawgs.le5.clubdawgs;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;

import com.google.gson.JsonObject;

public class Game {
    private Player player;
    private String roomId;
    private AnimationTimer mainLoop;
    private Scene scene;
    private Image bgImg = new Image(Main.class.getResource("map.png").toString());
    private VBox chatHistoryContainer;
    private TextArea chatHistory;
    private TextField inputField;
    private ArrayList<JsonObject> currChats = new ArrayList<>();
    private ArrayList<JsonObject> updateChats = new ArrayList<>();

    public Game(Firebase firebase, User user) {

        Font vcrFont = Font.loadFont(Main.class.getResource("VCR_OSD_MONO_1.001.ttf").toString(), 18);

        HBox root = new HBox();

        VBox gameCol = new VBox();

        Canvas c = new Canvas(Settings.gameWidth, Settings.gameHeight);
        GraphicsContext gc = c.getGraphicsContext2D();
        gameCol.getChildren().add(c);

        HBox toolRow = new HBox();
        toolRow.setPrefWidth(Settings.gameWidth);
        toolRow.setPrefHeight(Settings.winHeight - Settings.gameHeight);
        toolRow.setStyle("-fx-background-color: #555555");
        toolRow.setAlignment(Pos.CENTER);

        HBox subToolRow = new HBox();
        subToolRow.setPrefWidth(toolRow.getPrefWidth());
        subToolRow.setSpacing(20);
        subToolRow.setAlignment(Pos.CENTER_LEFT);

        TextField roomField = new TextField();
        roomField.setPromptText("Room ID");
        roomField.setPrefHeight(50);
        roomField.setPrefWidth(200);
        roomField.setFocusTraversable(false);
        roomField.setOnKeyPressed(event -> {
            if (roomField.isFocused()) {
                if (event.getCode() == KeyCode.ENTER && !roomField.getText().isEmpty()) {
                    firebase.quitPlayer(user.getLocalId(), user.getIdToken(), this.roomId);
                    this.roomId = roomField.getText();
                    this.player.setPos(0, 0);
                    firebase.updateLocation(this.player, user.getLocalId(), user.getIdToken(), this.roomId);
                    chatHistory.clear();
                }
            }
        });

        Image backImg = new Image(Main.class.getResource("quitLobby.png").toString());
        ImageView backImgView = new ImageView(backImg);
        backImgView.setFitWidth(50);
        backImgView.setPreserveRatio(true);
        Button backBtn = new Button();
        backBtn.setGraphic(backImgView);
        backBtn.setStyle("-fx-background-color: transparent;");
        backBtn.setFocusTraversable(false);

        subToolRow.getChildren().addAll(backBtn, roomField);

        Image uploadImg = new Image(Main.class.getResource("uploadButton.png").toString());
        ImageView uploadImgView = new ImageView(uploadImg);
        uploadImgView.setFitWidth(50);
        uploadImgView.setPreserveRatio(true);
        Button uploadBtn = new Button();
        uploadBtn.setGraphic(uploadImgView);
        uploadBtn.setStyle("-fx-background-color: transparent;");
        uploadBtn.setFocusTraversable(false);

        toolRow.getChildren().addAll(subToolRow, uploadBtn);

        gameCol.getChildren().add(toolRow);

        // Chat history container
        chatHistoryContainer = new VBox();
        chatHistoryContainer.setPadding(new Insets(20));
        chatHistoryContainer.setSpacing(10);
        chatHistoryContainer.setPrefWidth(Settings.winWidth - Settings.gameWidth);
        chatHistoryContainer.setPrefHeight(Settings.gameHeight);
        chatHistoryContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9);");

        // Logo placeholder (loading PNG)
        Image logoImage = new Image(Main.class.getResource("logo.png").toString());
        ImageView logoImageView = new ImageView(logoImage);
        logoImageView.setFitWidth(100);
        logoImageView.setPreserveRatio(true);

        // Centering the logo using a StackPane, removing shadow effects
        StackPane logoContainer = new StackPane(logoImageView);
        logoContainer.setAlignment(Pos.CENTER);
        logoContainer.setStyle("-fx-effect: null;");

        // Chat history display (non-editable text area)
        chatHistory = new TextArea();
        chatHistory.setEditable(false);
        chatHistory.setFocusTraversable(false);
        chatHistory.setFont(vcrFont);
        chatHistory.setStyle("-fx-background-color: transparent; -fx-text-fill: #0e2d6c; -fx-border-color: transparent; -fx-padding: 10;"); // Transparent background
        chatHistory.setWrapText(true);
        chatHistory.setOpacity(0.85);
        VBox.setVgrow(chatHistory, Priority.ALWAYS);

        // Input field for typing messages
        inputField = new TextField();
        inputField.setFocusTraversable(false);
        inputField.setPromptText("Type here...");
        inputField.setFont(vcrFont);
        inputField.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8); -fx-text-fill: #0e2d6c; -fx-prompt-text-fill: gray; -fx-border-radius: 15; -fx-background-radius: 15;");

        // Create a rounded rectangle as background for input field
        Region inputBackground = new Region();
        inputBackground.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8); -fx-background-radius: 15;");
        inputBackground.setMinHeight(40);

        // Adding a listener to resize the background with the input field
        inputField.widthProperty().addListener((obs, oldVal, newVal) -> {
            inputBackground.setPrefWidth(newVal.doubleValue() + 20);
        });

        // Adding messages to chat history when Enter is pressed
        inputField.setOnKeyPressed(event -> {
            if (inputField.isFocused()) {
                if (event.getCode() == KeyCode.ENTER && !inputField.getText().isEmpty()) {
                    String playerName = "Player 1"; // Static for now
                    String message = inputField.getText();
                    //update the chat in the database
                    updateChats = firebase.getChats(user.getIdToken(), this.roomId); // Gets a list of chats
                    JsonObject chat = new JsonObject();
                    chat.addProperty("Username", user.getUsername()); // We create JsonObject for a single chat
                    chat.addProperty("Message", message); // We create JsonObject for a single chat
                    updateChats.add(chat);
                    currChats = updateChats;
                    firebase.updateChats(user.getIdToken(), this.roomId, updateChats);
                    addMessageToChat(user.getUsername(), message);
                    inputField.clear();
                }
            }
        });

        // Adding components to chat history container
        chatHistoryContainer.getChildren().addAll(logoContainer, chatHistory, inputField);

        root.getChildren().addAll(gameCol, chatHistoryContainer);

        this.roomId = user.getUsername()+ "-r";
        roomField.setText(this.roomId);
        player = new Player(0, 0, user.getUsername());
        firebase.updateLocation(player, user.getLocalId(), user.getIdToken(), this.roomId);
        ArrayList<Player> players = new ArrayList<>();

        this.mainLoop = new AnimationTimer() {
            long lastTick = 0;

            public void handle(long l) {

                if (lastTick == 0) {
                    lastTick = l;
                    return;
                }

                if (l - lastTick > 1000000000) {
                    updateChats = firebase.getChats(user.getIdToken(), roomId);
                    if(!updateChats.equals(currChats)){
                        ArrayList<JsonObject> newChats = new ArrayList<>(updateChats);
                        newChats.removeAll(currChats);
                        currChats = new ArrayList<>(updateChats);
                        for(JsonObject chats : newChats){
                            addMessageToChat(chats.get("Username").getAsString(),chats.get("Message").getAsString());
                            }
                        }
                    lastTick = l;
                }

                if (l - lastTick > 1000000000/30) {
                    if (player.isMoving()) {
                        firebase.updateLocation(player, user.getLocalId(), user.getIdToken(), roomId);
                    }
                    firebase.getPlayers(players, user.getLocalId(), user.getIdToken(), roomId);
                    tick(gc, players);
                }
            }
        };
        this.mainLoop.start();

        this.scene = new Scene(root, Settings.winWidth, Settings.winHeight);
        c.setOnMouseClicked(e -> c.requestFocus());
        c.setOnKeyPressed(e -> KeyEventHandler.keyPressed(e, player));
        c.setOnKeyReleased(e -> KeyEventHandler.keyReleased(e, player));
    }

    private void addMessageToChat(String playerName, String message) {
        chatHistory.appendText(playerName + ": " + message + "\n");
    }

    public AnimationTimer getMainLoop() {
        return this.mainLoop;
    }

    public Scene getScene() {
        return this.scene;
    }

    public String getRoomId() {
        return this.roomId;
    }

    public void tick(GraphicsContext gc, ArrayList<Player> players) {
        player.move();

        gc.setFill(Color.rgb(255, 255, 255));
        gc.drawImage(this.bgImg, 0, 0, Settings.gameWidth, Settings.gameHeight);
        player.draw(gc);
        for (Player player1 : players) {
            player1.draw(gc);
        }
    }

}
