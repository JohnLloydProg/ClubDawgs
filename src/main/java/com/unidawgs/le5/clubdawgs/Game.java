package com.unidawgs.le5.clubdawgs;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

import com.google.gson.JsonObject;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

public class Game {
    private Player player;
    private Main application;
    private AnimationTimer mainLoop;
    private User user;
    private Scene scene;
    private Room room;
    private VBox chatHistoryContainer;
    private TextArea chatHistory;
    private TextField inputField;
    private ArrayList<JsonObject> currChats = new ArrayList<>();
    private ArrayList<JsonObject> updateChats = new ArrayList<>();
    private Thread playersUpdateThread;
    private File selectedFile = null;
    private int selectedFileXPos;
    private int selectedFileYPos;
    private Image dropBoxPlaceHold = new Image(Main.class.getResource("item.png").toString());
    private Thread createDropBoxThread;
    private boolean uploadingFile = false;
    private Request request;
    private String pending = null;
    private Thread checkRequestThread;
    private Thread getRequestsThread;
    private int pendingTimer;
    private ArrayList<String> requests = new ArrayList<>();
    private TextField roomField;

    public Game(Main application, String roomId) {
        this.application = application;
        this.user = application.getUser();

        this.initiateUI(roomId);

        Runnable playersUpdateTask = (Runnable) () -> {
            Firebase.updateLocation(player, user.getLocalId(), user.getIdToken(), this.room.getRoomId());
            this.room.updatePlayers(this.user.getLocalId(), this.user.getIdToken());
        };

        Runnable chatUpdateTask = (Runnable) () -> {
            updateChats = Firebase.getChats(user.getIdToken(), this.room.getRoomId());
            if(!updateChats.equals(currChats)){
                chatHistory.clear();
                ArrayList<JsonObject> newChats = new ArrayList<>(updateChats);
                // newChats.removeAll(currChats);
                currChats = new ArrayList<>(updateChats);
                for(JsonObject chats : newChats){
                    addMessageToChat(chats.get("Username").getAsString(),chats.get("Message").getAsString());
                }
            }
        };

        Runnable addDropBox = (Runnable) () -> {
            String downloadToken = Firebase.sendFile(user.getIdToken(), this.room.getRoomId(), selectedFile.getName(), selectedFile.toPath());
            DropBox dropBox = new DropBox("DropBox", selectedFile.getName(), downloadToken, selectedFileXPos-25, selectedFileYPos-25);
            Firebase.postDropBox(user.getIdToken(), this.room.getRoomId(), dropBox);
            selectedFile = null;
            uploadingFile = false;
        };

        Runnable checkPending = (Runnable) () -> {
            pending = Firebase.checkRequest(user.getLocalId(), user.getIdToken(), roomField.getText());
        };

        Runnable getRequests = (Runnable) () -> {
            requests = Firebase.getRequests(user.getIdToken(), user.getUsername()+ "-r");
        };

        this.mainLoop = new AnimationTimer() {
            long lastTickChat = System.nanoTime();
            long lastTickMove = System.nanoTime();
            Thread chatUpdateThread;
            String collisionCommand = "";

            public void handle(long l) {

                if (System.nanoTime() - lastTickChat >= 1000000000) {
                    if (!requests.isEmpty()) {
                        request = new Request(requests.get(0), user.getIdToken(), user.getUsername()+ "-r");
                    }else {
                        request = null;
                    }
                    chatUpdateThread = new Thread(chatUpdateTask);
                    chatUpdateThread.setDaemon(true);
                    chatUpdateThread.start();
                    room.updateDropBoxes(user.getIdToken());
                    getRequestsThread = new Thread(getRequests);
                    getRequestsThread.setDaemon(true);
                    getRequestsThread.start();
                    if (pending != null) {
                        checkRequestThread = new Thread(checkPending);
                        checkRequestThread.setDaemon(true);
                        checkRequestThread.start();
                        pendingTimer--;
                        if (pending.contentEquals( "Accepted")) {
                            mainLoop.stop();
                            Firebase.quitPlayer(user.getLocalId(), user.getIdToken(), room.getRoomId());
                            application.newGame(roomField.getText());
                        }
                        if (pendingTimer == 0 || pending.contentEquals("Rejected")) {
                            pending = null;
                            Firebase.rejectRequest(user.getIdToken(), roomField.getText(), user.getLocalId());
                            roomField.setDisable(false);
                        }
                    }
                    lastTickChat = System.nanoTime();
                }

                if (System.nanoTime() - lastTickMove > 1000000000/50) {
                    playersUpdateThread = new Thread(playersUpdateTask);
                    playersUpdateThread.setDaemon(true);
                    playersUpdateThread.start();
                    player.getMove();
                    collisionCommand = room.collisionHandler(player);
                    executeCollisionCommand(collisionCommand);
                    player.move();
                    tick();
                    lastTickMove = System.nanoTime();
                }
            }
        };
        this.mainLoop.start();


        this.room.setOnMouseMoved(e -> {
            if (selectedFile != null && !this.uploadingFile) {
                selectedFileXPos = (int) e.getX();
                selectedFileYPos = (int) e.getY();
            }
        });
        this.room.setOnMouseClicked(e -> {
            this.room.requestFocus();

            //Coordinating purpose
            if (e.getButton() == MouseButton.SECONDARY) {
                System.out.println(e.getX() + ", " + e.getY());
            }
            //

            for (DropBox box : this.room.getDropBoxes()) {
                if (box.clicked(e)) {
                    box.interact(this.user, this.room.getRoomId());
                }
            }

            if (request != null) {
                request.eventHandler(e);
            }

            if (e.getButton() == MouseButton.PRIMARY) {
                if (selectedFile != null && !this.uploadingFile) {
                    createDropBoxThread = new Thread(addDropBox);
                    createDropBoxThread.setDaemon(true);
                    createDropBoxThread.start();
                    this.uploadingFile = true;
                }
            }

        });
        this.room.setOnKeyPressed(e -> KeyEventHandler.keyPressed(e, player));
        this.room.setOnKeyReleased(e -> KeyEventHandler.keyReleased(e, player));
    }

    private void initiateUI(String roomId) {
        Font vcrFont = Font.loadFont(Main.class.getResource("VCR_OSD_MONO_1.001.ttf").toString(), 18);

        HBox root = new HBox();

        VBox gameCol = new VBox();

        int cosmetic = Firebase.getCosmetic(this.user.getLocalId(), this.user.getIdToken());
        if (roomId.contains("-r")) {
            this.room = new Backyard(Settings.gameWidth, Settings.gameHeight, roomId);
            this.player = new Player(80, 540, user.getUsername(), cosmetic);
        }else if (roomId.contains("-pr")) {
            this.room = new PersonalRoom(Settings.gameWidth, Settings.gameHeight, roomId);
            this.player = new Player(380, 510, user.getUsername(), cosmetic);
        }else {
            this.room = new Lobby(Settings.gameWidth, Settings.gameHeight, roomId);
            this.player = new Player(80, 540, user.getUsername(), cosmetic);
        }
        gameCol.getChildren().add(this.room);


        Firebase.updateLocation(player, user.getLocalId(), user.getIdToken(), this.room.getRoomId());

        HBox toolRow = new HBox();
        toolRow.setPrefWidth(Settings.gameWidth);
        toolRow.setPrefHeight(Settings.winHeight - Settings.gameHeight);
        toolRow.setStyle("-fx-background-color: #555555");
        toolRow.setAlignment(Pos.CENTER);

        HBox subToolRow = new HBox();
        subToolRow.setPrefWidth(toolRow.getPrefWidth());
        subToolRow.setSpacing(20);
        subToolRow.setAlignment(Pos.CENTER_LEFT);

        this.roomField = new TextField();
        this.roomField.setPromptText("Room ID");
        this.roomField.setPrefHeight(50);
        this.roomField.setPrefWidth(200);
        this.roomField.setFocusTraversable(false);
        this.roomField.setOnKeyPressed(event -> {
            if (roomField.isFocused()) {
                if (event.getCode() == KeyCode.ENTER && !roomField.getText().isEmpty()) {
                    if (roomField.getText().contains("-r")) {
                        pending = "Pending";
                        pendingTimer = 10;
                        Firebase.sendRequest(user.getLocalId(), user.getIdToken(), roomField.getText());
                        roomField.setDisable(true);
                    }else if (!roomField.getText().contains("-pr")){
                        mainLoop.stop();
                        Firebase.quitPlayer(user.getLocalId(), user.getIdToken(), this.room.getRoomId());
                        this.application.newGame(roomField.getText());
                    }
                }
            }
        });
        roomField.setText(this.room.getRoomId());

        Image backImg = new Image(Main.class.getResource("quitLobby.png").toString());
        ImageView backImgView = new ImageView(backImg);
        backImgView.setFitWidth(50);
        backImgView.setPreserveRatio(true);
        Button backBtn = new Button();
        backBtn.setGraphic(backImgView);
        backBtn.setStyle("-fx-background-color: transparent;");
        backBtn.setFocusTraversable(false);
        backBtn.setOnMouseClicked((event) -> {
            if (!this.room.getRoomId().contentEquals(this.user.getUsername() + "-r")) {
                mainLoop.stop();
                Firebase.quitPlayer(user.getLocalId(), user.getIdToken(), this.room.getRoomId());
                this.application.newGame(this.user.getUsername() + "-r");
            }
        });

        subToolRow.getChildren().addAll(backBtn, roomField);

        Image uploadImg = new Image(Main.class.getResource("uploadButton.png").toString());
        ImageView uploadImgView = new ImageView(uploadImg);
        uploadImgView.setFitWidth(50);
        uploadImgView.setPreserveRatio(true);
        Button uploadBtn = new Button();
        uploadBtn.setGraphic(uploadImgView);
        uploadBtn.setStyle("-fx-background-color: transparent;");
        uploadBtn.setFocusTraversable(false);
        uploadBtn.setOnAction(e -> {
            System.err.println(this.uploadingFile);
            if (selectedFile == null && !this.uploadingFile) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select a File to Upload");
                selectedFile = fileChooser.showOpenDialog(null);
            }
        });

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

        Runnable sendChatTask = (Runnable) () -> {
            String message = inputField.getText();
            //update the chat in the database
            updateChats = Firebase.getChats(user.getIdToken(), this.room.getRoomId()); // Gets a list of chats
            JsonObject chat = new JsonObject();
            chat.addProperty("Username", user.getUsername()); // We create JsonObject for a single chat
            chat.addProperty("Message", message); // We create JsonObject for a single chat
            updateChats.add(chat);
            currChats = updateChats;
            Firebase.updateChats(user.getIdToken(), this.room.getRoomId(), updateChats);
            addMessageToChat(user.getUsername(), message);
            inputField.clear();
        };

        // Adding messages to chat history when Enter is pressed
        inputField.setOnKeyPressed(event -> {
            if (inputField.isFocused()) {
                if (event.getCode() == KeyCode.ENTER && !inputField.getText().isEmpty()) {
                    Thread sendChatThread = new Thread(sendChatTask);
                    sendChatThread.setDaemon(true);
                    sendChatThread.start();
                }
            }
        });

        // Adding components to chat history container
        chatHistoryContainer.getChildren().addAll(logoContainer, chatHistory, inputField);

        root.getChildren().addAll(gameCol, chatHistoryContainer);
        this.scene = new Scene(root, Settings.winWidth, Settings.winHeight);
    }

    private void executeCollisionCommand(String command) {
        switch (command) {
            case "Entering House":
                this.mainLoop.stop();
                Firebase.quitPlayer(this.user.getLocalId(), this.user.getIdToken(), this.room.getRoomId());
                this.application.newGame(this.user.getUsername() + "-pr");
            break;
        }
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
        return this.room.getRoomId();
    }

    public void tick() {
        room.drawRoom(this.player);

        if (selectedFile != null && !this.uploadingFile) {
            room.getGraphicsContext2D().drawImage(this.dropBoxPlaceHold, selectedFileXPos - 25, selectedFileYPos - 25, 50, 50);
        }

        if (request != null) {
            request.draw(room.getGraphicsContext2D());
        }
    }

}
