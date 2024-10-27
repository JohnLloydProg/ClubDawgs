package com.unidawgs.le5.clubdawgs;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {
    private Game game;
    private User user;
    private Stage stage;
    private Scene login;
    private Scene signUp;
    private MediaPlayer mediaPlayer;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.login = this.createLoginScene();
        this.signUp = signup.createSignUpScene(this);

        stage.setTitle("ClubDawgs");
        stage.setScene(this.login);
        stage.setResizable(false);

        stage.setOnHiding((event) -> {
            if (this.game != null) {
                if (stage.getScene() == this.game.getScene()) {
                    this.game.getMainLoop().stop();
                    Firebase.quitPlayer(user.getLocalId(), user.getIdToken(), this.game.getRoomId());
                }
            }
            this.mediaPlayer.stop();
        });

        stage.show();
    }

    public Stage getStage() {
        return this.stage;
    }

    public Scene getLogin() {
        return this.login;
    }

    public User getUser() {
        return this.user;
    }

    public void newGame(String roomId) {
        this.game = new Game(this, roomId);
        this.stage.setScene(this.game.getScene());
    }

    public Scene createLoginScene() {
        // Root pane
        BorderPane borderPane = new BorderPane();

        // Background image
        Image bgImage = new Image(getClass().getResource("bg.png").toExternalForm());
        Image cloudImage = new Image(getClass().getResource("cloud.png").toExternalForm());

        // Cloud image
        ImageView imageViewCloud = new ImageView(cloudImage);
        ImageView imageViewBackground = new ImageView(bgImage);
        borderPane.getChildren().addAll(imageViewBackground, imageViewCloud);

        // Cloud Animation
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(1), imageViewCloud);
        translateTransition.setByY(50);
        translateTransition.setAutoReverse(true);
        translateTransition.setCycleCount(TranslateTransition.INDEFINITE);
        translateTransition.play();

        // StackPane to hold logo and login
        StackPane stackPane = new StackPane();
        stackPane.setAlignment(Pos.TOP_CENTER);

        // Default font styling for label
        Font labelStyle = Font.font("Arial", FontWeight.BOLD, 14);

        // Logo image
        Image logoImage = new Image(getClass().getResource("logo.png").toExternalForm());
        ImageView logoImageView = new ImageView(logoImage);
        logoImageView.setPreserveRatio(true);
        logoImageView.setFitWidth(450);

        // Login Container
        VBox loginContainer = new VBox(15);
        loginContainer.setAlignment(Pos.CENTER);
        loginContainer.setStyle("-fx-background-color: white; -fx-padding: 30px; -fx-background-radius: 10;");
        StackPane.setMargin(loginContainer, new Insets(250, 250, 250, 225));
        loginContainer.setPadding(new Insets(0, 50, 0, 50));

        // Username Container
        VBox usernameContainer = new VBox(5);
        usernameContainer.setAlignment(Pos.BASELINE_LEFT);

        // Username Label
        Label emailLabel = new Label("EMAIL");
        emailLabel.setFont(labelStyle);

        // Username Text Field
        TextField emailField = new TextField();
        emailField.setStyle("-fx-background-color: #D4F1F4; -fx-border-radius: 20;-fx-padding: 10;");

        usernameContainer.getChildren().addAll(emailLabel, emailField);

        // Password Container
        VBox passwordContainer = new VBox(5);
        passwordContainer.setAlignment(Pos.BASELINE_LEFT);

        // Password Label
        Label passwordLabel = new Label("PASSWORD");
        passwordLabel.setFont(labelStyle);

        // Password Field
        PasswordField passwordField = new PasswordField();
        passwordField.setStyle("-fx-background-color: #D4F1F4; -fx-border-radius: 20;-fx-padding: 10;");
        passwordContainer.getChildren().addAll(passwordLabel, passwordField);

        // Login Button Container
        HBox loginButtonContainer = new HBox();
        loginButtonContainer.setAlignment(Pos.CENTER);
        loginButtonContainer.setPadding(new Insets(10, 0, 10, 0));

        // Login Button
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #2c67f2; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;-fx-padding: 5;");
        loginButton.setPrefSize(100, 30);
        loginButton.setOnMouseClicked((event) -> {
            this.user = Firebase.signIn(emailField.getText(), passwordField.getText());
            System.out.println(this.user);
            if (this.user != null) {
                this.newGame(this.user.getUsername() + "-r");
            }
        });
        loginButtonContainer.getChildren().add(loginButton);

        // Font styling for signup container
        Font registerLabelStyle = Font.font("Arial", FontWeight.SEMI_BOLD, 12);

        // Signup Container
        HBox signupContainer = new HBox(5);
        signupContainer.setAlignment(Pos.CENTER); // Align signup link in the center

        // Register Label
        Label registerLabel = new Label("Don't have an account?");
        registerLabel.setFont(registerLabelStyle);

        // Hyperlink signup (if user has no account)
        Hyperlink signUpLink = new Hyperlink("Sign up");
        signUpLink.setStyle("-fx-text-fill: #2c67f2; -fx-underline: true;");
        signUpLink.setFont(registerLabelStyle);

        signUpLink.setOnMouseClicked((MouseEvent event) -> {
            stage.setScene(this.signUp);
        });

        signupContainer.getChildren().addAll(registerLabel, signUpLink);

        loginContainer.getChildren().addAll(usernameContainer, passwordContainer, loginButtonContainer, signupContainer);

        stackPane.getChildren().addAll(loginContainer, logoImageView);
        StackPane.setAlignment(loginContainer, Pos.CENTER);
        borderPane.setCenter(stackPane);

        Media pick = new Media("https://drive.google.com/uc?export=download&id=1mR_4UzHzjH5hoLdEWMe4DiWfL-2IFC94"); 
        mediaPlayer = new MediaPlayer(pick);
        MediaView mediaView = new MediaView();
        mediaView.setMediaPlayer(mediaPlayer);
        borderPane.getChildren().add(mediaView);
        //mediaPlayer.play();

        return new Scene(borderPane, 875, 625);
    }

    public static void main(String[] args) {
        launch();
    }
}