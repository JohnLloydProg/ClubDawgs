package com.unidawgs.le5.clubdawgs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import com.google.gson.JsonObject;
import com.unidawgs.le5.clubdawgs.events.CosmeticEvent;
import com.unidawgs.le5.clubdawgs.events.OverlayEvent;
import com.unidawgs.le5.clubdawgs.events.RoomEvent;
import com.unidawgs.le5.clubdawgs.objects.DropBox;
import com.unidawgs.le5.clubdawgs.objects.Player;
import com.unidawgs.le5.clubdawgs.objects.Request;
import com.unidawgs.le5.clubdawgs.overlays.Overlay;
import com.unidawgs.le5.clubdawgs.rooms.Backyard;
import com.unidawgs.le5.clubdawgs.rooms.Lobby;
import com.unidawgs.le5.clubdawgs.rooms.PersonalRoom;
import com.unidawgs.le5.clubdawgs.rooms.Room;

import javafx.animation.AnimationTimer;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Game {
    public static EventType<OverlayEvent> DISPLAYING_OVERLAY = new EventType<>("DISPLAYING_OVERLAY");
    public static EventType<Event> HIDE_OVERLAY = new EventType<>("HIDE_OVERLAY");
    public static EventType<Event> MOUSE_ENTER = new EventType<>("MOUSE_ENTER");
    public static EventType<Event> MOUSE_EXIT = new EventType<>("MOUSE_EXIT");
    public static EventType<CosmeticEvent> CHANGE_COSMETIC = new EventType<>("CHANGE_COSMETIC");
    public static EventType<RoomEvent> ROOM_TRANSITION = new EventType<>("ROOM_TRANSITION");
    public static EventType<Event> SHOW_GACHA = new EventType<>("SHOW_GACHA");
    public static EventType<Event> HIDE_GACHA = new EventType<>("HIDE_GACHA");
    private Player player;
    private Main application;
    private AnimationTimer mainLoop;
    private Scene scene;
    private Room room;
    private VBox chatHistoryContainer;
    private TextArea chatHistory;
    private TextField inputField;
    private ArrayList<JsonObject> currChats = new ArrayList<>();
    private ArrayList<JsonObject> updateChats = new ArrayList<>();
    private File selectedFile = null;
    private int selectedFileXPos;
    private int selectedFileYPos;
    private Image dropBoxPlaceHold = new Image(Main.class.getResource("item.png").toString());
    private Thread createDropBoxThread;
    private boolean uploadingFile = false;
    private Request request;
    private String pending = null;
    private int pendingTimer;
    private ArrayList<String> requests = new ArrayList<>();
    private TextField roomField;
    private Overlay overlay = null;
    private Font currencyFont = new Font(20);
    private ColorAdjust darken = new ColorAdjust();
    private double brightness = -1;
    private boolean hiden = true;
    private String roomTransition = null;
    private AudioClip[] barkSfx = {
            new AudioClip(Main.class.getResource("sfx/bark1.mp3").toString()),
            new AudioClip(Main.class.getResource("sfx/bark2.mp3").toString()),
            new AudioClip(Main.class.getResource("sfx/bark3.mp3").toString())
    };
    private StackPane gamePane = new StackPane();
    private GachaAnimation gachaAnimation;
    private boolean update = true;

    public Game(Main application, String roomId) {
        System.out.println("Creating new game");
        this.application = application;

        this.initiateUI(roomId);
        new Minigame1(this.room.getRoomId()).start(new Stage()); // For testing purposes LEADERBOARD

        Runnable playersUpdateTask = (Runnable) () -> {
            Firebase.updateLocation(player, Main.getUser().getLocalId(), Main.getUser().getIdToken(), this.room.getRoomId());
            this.room.updatePlayers(Main.getUser().getLocalId(), Main.getUser().getIdToken());
        };

        Runnable chatUpdateTask = (Runnable) () -> {
            updateChats = Firebase.getChats(Main.getUser().getIdToken(), this.room.getRoomId());
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
            String downloadToken = Firebase.sendFile(Main.getUser().getIdToken(), this.room.getRoomId(), selectedFile.getName(), selectedFile.toPath());
            DropBox dropBox = new DropBox("DropBox", selectedFile.getName(), downloadToken, selectedFileXPos-25, selectedFileYPos-25);
            Firebase.postDropBox(Main.getUser().getIdToken(), this.room.getRoomId(), dropBox);
            selectedFile = null;
            uploadingFile = false;
        };

        Runnable checkPending = (Runnable) () -> {
            pending = Firebase.checkRequest(Main.getUser().getLocalId(), Main.getUser().getIdToken(), roomField.getText());
        };

        Runnable getRequests = (Runnable) () -> {
            requests = Firebase.getRequests(Main.getUser().getIdToken(), Main.getUser().getUsername()+ "-r");
        };

        this.mainLoop = new AnimationTimer() {
            long lastTickChat = System.nanoTime();
            long lastTickMove = System.nanoTime();
            Thread chatUpdateThread;
            Thread checkRequestThread;
            Thread getRequestsThread;
            Thread playersUpdateThread;

            public void handle(long l) {

                if (System.nanoTime() - lastTickChat >= 1000000000) {
                    bark();
                    if (!requests.isEmpty()) {
                        request = new Request(requests.get(0), Main.getUser().getIdToken(), Main.getUser().getUsername()+ "-r");
                    }else {
                        request = null;
                    }
                    chatUpdateThread = new Thread(chatUpdateTask);
                    chatUpdateThread.setDaemon(true);
                    chatUpdateThread.start();
                    room.updateDropBoxes(Main.getUser().getIdToken());
                    getRequestsThread = new Thread(getRequests);
                    getRequestsThread.setDaemon(true);
                    getRequestsThread.start();
                    if (pending != null) {
                        checkRequestThread = new Thread(checkPending);
                        checkRequestThread.setDaemon(true);
                        checkRequestThread.start();
                        pendingTimer--;
                        if (pending.contentEquals( "Accepted")) {
                            room.fireEvent(new RoomEvent(ROOM_TRANSITION, roomField.getText()));
                        }
                        if (pendingTimer == 0 || pending.contentEquals("Rejected")) {
                            pending = null;
                            Firebase.rejectRequest(Main.getUser().getIdToken(), roomField.getText(), Main.getUser().getLocalId());
                            roomField.setDisable(false);
                        }
                    }
                    lastTickChat = System.nanoTime();
                }

                if (System.nanoTime() - lastTickMove > 1000000000/50) {
                    if (roomTransition != null) {
                        transition();
                    }
                    if (update) {
                        playersUpdateThread = new Thread(playersUpdateTask);
                        playersUpdateThread.setDaemon(true);
                        playersUpdateThread.start();
                    }
                    player.getMove();
                    room.collisionHandler(player);
                    player.move();
                    tick();
                    lastTickMove = System.nanoTime();
                }
            }
        };
        this.mainLoop.start();


        this.room.setOnMouseMoved(e -> {
            if (this.overlay != null) {
                this.overlay.mouseMoveHandler(e);
                return;
            }

            this.room.mouseMoveHandler(e);

            if (selectedFile != null && !this.uploadingFile) {
                selectedFileXPos = (int) e.getX();
                selectedFileYPos = (int) e.getY();
            }
        });
        this.room.setOnMouseClicked(e -> {
            if (this.overlay != null) {
                this.overlay.mouseClickHandler(e);
                return;
            }

            this.room.requestFocus();
            this.room.mouseClickHandler(e);

            //Coordinating purpose
            if (e.getButton() == MouseButton.SECONDARY) {
                System.out.println(e.getX() + ", " + e.getY());
            }
            //

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
        this.room.setOnScroll(e -> {
            if (this.overlay != null) {
                this.overlay.scrollHandler(e);
            }
        });
        this.room.setOnKeyPressed(e -> KeyEventHandler.keyPressed(e, player));
        this.room.setOnKeyReleased(e -> KeyEventHandler.keyReleased(e, player));
        this.room.addEventFilter(DISPLAYING_OVERLAY, overlayEvent -> {
            if (this.overlay == null) {
                this.overlay = overlayEvent.getOverlay();
                this.scene.setCursor(Cursor.DEFAULT);
            }
        });
        this.room.addEventFilter(HIDE_OVERLAY, overlayEvent -> {
            this.overlay = null;
            this.scene.setCursor(Cursor.DEFAULT);
        });
        this.room.addEventFilter(MOUSE_ENTER, moveEvent -> this.scene.setCursor(Cursor.HAND));
        this.room.addEventFilter(MOUSE_EXIT, moveEvent -> this.scene.setCursor(Cursor.DEFAULT));
        this.room.addEventFilter(CHANGE_COSMETIC, cosmeticEvent -> this.player.changeCosmetic(cosmeticEvent.getCosmetic()));
        this.room.addEventFilter(ROOM_TRANSITION, roomEvent -> {
            this.roomTransition = roomEvent.getRoomId();
        });
        this.room.addEventFilter(SHOW_GACHA, e -> {
            this.room.fireEvent(new Event(MOUSE_EXIT));
            try {
                this.gachaAnimation = new GachaAnimation(this.room);
                this.gamePane.getChildren().add(this.gachaAnimation);
            }catch (IOException err) {
                err.printStackTrace();
            }
        });
        this.room.addEventFilter(HIDE_GACHA, e -> {
            this.gamePane.getChildren().remove(this.gachaAnimation);
            this.gachaAnimation = null;
        });
    }

    private void initiateUI(String roomId) {
        Font vcrFont = Font.loadFont(Main.class.getResource("VCR_OSD_MONO_1.001.ttf").toString(), 18);

        HBox root = new HBox();

        VBox gameCol = new VBox();

        int cosmetic = Firebase.getCosmetic(Main.getUser().getLocalId(), Main.getUser().getIdToken());
        if (roomId.contains("-r")) {
            this.room = new Backyard(Settings.gameWidth, Settings.gameHeight, roomId);
            this.player = new Player(80, 540, Main.getUser().getUsername(), cosmetic);
        }else if (roomId.contains("-pr")) {
            this.room = new PersonalRoom(Settings.gameWidth, Settings.gameHeight, roomId, Firebase.getRoomLevel(Main.getUser().getLocalId(), Main.getUser().getIdToken()));
            this.player = new Player(380, 510, Main.getUser().getUsername(), cosmetic);
        }else {
            this.room = new Lobby(Settings.gameWidth, Settings.gameHeight, roomId);
            this.player = new Player(411, 302, Main.getUser().getUsername(), cosmetic);
        }
        this.tick();
        this.gamePane.getChildren().add(this.room);
        gameCol.getChildren().add(gamePane);


        Firebase.updateLocation(player, Main.getUser().getLocalId(), Main.getUser().getIdToken(), this.room.getRoomId());

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
                        Firebase.sendRequest(Main.getUser().getLocalId(), Main.getUser().getIdToken(), roomField.getText());
                        roomField.setDisable(true);
                    }else if (!roomField.getText().contains("-pr")){
                        this.room.fireEvent(new RoomEvent(ROOM_TRANSITION, roomField.getText()));
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
            if (!this.room.getRoomId().contentEquals(Main.getUser().getUsername() + "-r")) {
                this.room.fireEvent(new RoomEvent(ROOM_TRANSITION, Main.getUser().getUsername() + "-r"));
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
            updateChats = Firebase.getChats(Main.getUser().getIdToken(), this.room.getRoomId()); // Gets a list of chats
            JsonObject chat = new JsonObject();
            chat.addProperty("Username", Main.getUser().getUsername()); // We create JsonObject for a single chat
            chat.addProperty("Message", message); // We create JsonObject for a single chat
            updateChats.add(chat);
            currChats = updateChats;
            Firebase.updateChats(Main.getUser().getIdToken(), this.room.getRoomId(), updateChats);
            addMessageToChat(Main.getUser().getUsername(), message);
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

    private void bark() {
        Random random = new Random();
        int chance = random.nextInt(1, 101);
        int barkIndex = random.nextInt(3);
        if (chance >= 95) {
            this.barkSfx[barkIndex].play();
        }
    }

    private void addMessageToChat(String playerName, String message) {
        chatHistory.appendText(playerName + ": " + message + "\n");
    }

    private void transition() {
        this.update = false;
        if (this.brightness > -1) {
            this.brightness -= 0.05;
        }else {
            this.mainLoop.stop();
            System.out.println("Stopped main loop");
            Firebase.quitPlayer(Main.getUser().getLocalId(), Main.getUser().getIdToken(), this.room.getRoomId());
            this.application.newGame(this.roomTransition);
        }
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
        GraphicsContext gc = room.getGraphicsContext2D();
        if (this.hiden) {
            this.brightness += 0.05;
        }
        if (this.brightness >= 0) {
            this.hiden = false;
            this.brightness = 0;
        }
        darken.setBrightness(this.brightness);
        gc.setEffect(darken);
        room.drawRoom(this.player);

        if (selectedFile != null && !this.uploadingFile) {
            gc.drawImage(this.dropBoxPlaceHold, selectedFileXPos - 25, selectedFileYPos - 25, 50, 50);
        }

        if (overlay != null) {
            overlay.draw(gc);
        }

        if (request != null && overlay == null) {
            request.draw(gc);
        }

        gc.setTextAlign(TextAlignment.RIGHT);
        gc.setTextBaseline(VPos.TOP);
        gc.setFill(Color.BLACK);
        gc.setFont(this.currencyFont);
        gc.fillText(Main.getUser().getCurrency() + "", Settings.gameWidth - 10, 10);
    }

}
