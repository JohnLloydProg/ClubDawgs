package com.unidawgs.le5.clubdawgs;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.InputStream;
import java.io.FileNotFoundException;

public class chathistory extends Application {

    private VBox chatHistoryContainer;
    private TextArea chatHistory;
    private TextField inputField;

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        // Load the custom font
        String fontPath = "/com/example/VCR_OSD_MONO_1.001.ttf";
        InputStream fontStream = getClass().getResourceAsStream(fontPath);
        if (fontStream == null) {
            throw new FileNotFoundException("Font file not found");
        }
        Font vcrFont = Font.loadFont(fontStream, 18);

        // Main layout container
        HBox mainLayout = new HBox();
        mainLayout.setPadding(new Insets(10));
        mainLayout.setSpacing(20);
        mainLayout.setPrefSize(1200, 650);
        HBox.setHgrow(mainLayout, Priority.ALWAYS);

        // Playable area (image)
        Image mapImage = new Image(getClass().getResourceAsStream("/com/example/map.png"));
        ImageView mapImageView = new ImageView(mapImage);
        mapImageView.setPreserveRatio(true);
        mapImageView.setSmooth(true);

        // StackPane for the image to fill the space
        StackPane playableArea = new StackPane();
        playableArea.getChildren().add(mapImageView);
        playableArea.setStyle("-fx-background-color: black;");

        // Resizing image view based on the container
        playableArea.widthProperty().addListener((obs, oldVal, newVal) -> {
            mapImageView.setFitWidth(newVal.doubleValue());
        });
        playableArea.heightProperty().addListener((obs, oldVal, newVal) -> {
            mapImageView.setFitHeight(newVal.doubleValue());
        });

        // Chat history container
        chatHistoryContainer = new VBox();
        chatHistoryContainer.setPadding(new Insets(10));
        chatHistoryContainer.setSpacing(10);
        chatHistoryContainer.setPrefWidth(300);
        chatHistoryContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9);");
        VBox.setVgrow(chatHistoryContainer, Priority.ALWAYS);

        // Logo placeholder (loading PNG)
        InputStream inputStream = getClass().getResourceAsStream("/com/example/logo.png");
        if (inputStream == null) {
            throw new FileNotFoundException("Logo file not found");
        }
        Image logoImage = new Image(inputStream);
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
        chatHistory.setFont(vcrFont);
        chatHistory.setStyle("-fx-background-color: transparent; -fx-text-fill: #0e2d6c; -fx-border-color: transparent; -fx-padding: 10;"); // Transparent background
        chatHistory.setWrapText(true);
        chatHistory.setOpacity(0.85);
        VBox.setVgrow(chatHistory, Priority.ALWAYS);

        // Input field for typing messages
        inputField = new TextField();
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
            if (event.getCode() == KeyCode.ENTER && !inputField.getText().isEmpty()) {
                String playerName = "Player 1"; // Static for now
                String message = inputField.getText();
                addMessageToChat(playerName, message);
                inputField.clear();
            }
        });

        // Adding components to chat history container
        chatHistoryContainer.getChildren().addAll(logoContainer, chatHistory, inputField);

        // Adding components to main layout (playable area + chat history)
        mainLayout.getChildren().addAll(playableArea, chatHistoryContainer);

        // Setting up the scene
        Scene scene = new Scene(mainLayout);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Club Dawg");
        primaryStage.show();
    }

    // Method to append player message to chat history
    private void addMessageToChat(String playerName, String message) {
        chatHistory.appendText(playerName + ": " + message + "\n");
    }

    public static void main(String[] args) {
        launch(args);
    }
}