package com.unidawgs.le5.clubdawgs;

import com.unidawgs.le5.clubdawgs.objects.User;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class signup {
    public static Scene createSignUpScene(Main app) {

        // Create a BorderPane for the layout
        BorderPane borderPane = new BorderPane();

        // Background image
        Image bgImage = new Image(Main.class.getResource("bg.png").toExternalForm());
        Image cloudImage = new Image(Main.class.getResource("cloud.png").toExternalForm());

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

        // StackPane to hold logo, login, and back button
        StackPane stackPane = new StackPane();

        // Default font styling for label
        Font labelStyle = Font.font("Arial", FontWeight.BOLD, 14);

        // Logo image
        Image logoImage = new Image(Main.class.getResource("logo.png").toExternalForm());
        ImageView logoImageView = new ImageView(logoImage);
        logoImageView.setPreserveRatio(true);
        logoImageView.setFitWidth(350);

        // Signup Container
        VBox signupContainer = new VBox(15);
        signupContainer.setAlignment(Pos.CENTER);
        signupContainer.setStyle("-fx-background-color: white; -fx-padding: 30px; -fx-background-radius: 10;");
        StackPane.setMargin(signupContainer, new Insets(200, 225, 255, 200));
        signupContainer.setPadding(new Insets(0, 50, 0, 50));

        // Username Container
        VBox usernameContainer = new VBox(5);
        Label usernameLabel = new Label("USERNAME");
        usernameLabel.setFont(labelStyle);
        TextField usernameField = new TextField();
        usernameField.setStyle("-fx-background-color: #D4F1F4; -fx-border-radius: 20;-fx-padding: 10;");
        usernameContainer.getChildren().addAll(usernameLabel, usernameField);

        // Email Container
        VBox emailContainer = new VBox(5);
        Label emailLabel = new Label("EMAIL");
        emailLabel.setFont(labelStyle);
        TextField emailField = new TextField();
        emailField.setStyle("-fx-background-color: #D4F1F4; -fx-border-radius: 20;-fx-padding: 10;");
        emailContainer.getChildren().addAll(emailLabel, emailField);

        // Password Container
        VBox passwordContainer = new VBox(5);
        Label passwordLabel = new Label("PASSWORD");
        passwordLabel.setFont(labelStyle);
        PasswordField passwordField = new PasswordField();
        passwordField.setStyle("-fx-background-color: #D4F1F4; -fx-border-radius: 20;-fx-padding: 10;");
        passwordContainer.getChildren().addAll(passwordLabel, passwordField);

        // Confirm Password Container
        VBox confirmPasswordContainer = new VBox(5);
        Label confirmPasswordLabel = new Label("CONFIRM PASSWORD");
        confirmPasswordLabel.setFont(labelStyle);
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setStyle("-fx-background-color: #D4F1F4; -fx-border-radius: 20;-fx-padding: 10;");
        confirmPasswordContainer.getChildren().addAll(confirmPasswordLabel, confirmPasswordField);

        // Signup button
        Button signupButton = new Button("Sign up");
        signupButton.setStyle("-fx-background-color: #2c67f2; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;-fx-padding: 5;");
        signupButton.setPrefSize(100, 30);

        // Back button setup (as is in the provided code)
        VBox backButtonContainer = new VBox();
        backButtonContainer.setAlignment(Pos.TOP_LEFT);
        Image backImage = new Image(Main.class.getResource("backBtn.png").toExternalForm());
        ImageView backImageView = new ImageView(backImage);
        Button backButton = new Button();
        backButton.setGraphic(backImageView);
        backButton.setStyle("-fx-background-color: transparent;");
        backImageView.setFitWidth(100);
        backImageView.setPreserveRatio(true);
        backButton.setOnAction(e -> app.getStage().setScene(app.getLogin()));
        backButtonContainer.getChildren().add(backButton);

        signupButton.setOnAction(e -> {
            String email = emailField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            // Email, username, and password validation
            if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showAlert("Error", "All fields must be filled out!");
                return;
            }

            if(username.contains(" ")){
                showAlert("Error", "Username cannot include spaces. Please try again");
                return;
            }

            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {  // Basic email pattern check
                showAlert("Error", "Invalid email format!");
                return;
            }

            if (!password.equals(confirmPassword)) {
                showAlert("Error", "Passwords do not match!");
                return;
            }

            new Thread(() -> {
                try {
                    User newUser = Firebase.signUp(email, password, username);
                    if (newUser != null) {
                        // Signup successful
                        javafx.application.Platform.runLater(() -> {
                            showAlert("Success", "Signup successful!");
                            app.getStage().setScene(app.getLogin()); // Redirect to login page
                        });
                    } else {
                        // Signup failed (could be email or username already taken)
                        javafx.application.Platform.runLater(() -> {
                            showAlert("Error", "Signup failed. Please try again.");
                        });
                    }
                } catch (FirebaseSignUpException ex) {

                    javafx.application.Platform.runLater(() -> {
                        String errorCode = ex.getErrorCode();
                        switch (errorCode) {
                            case "EMAIL_EXISTS":
                                showAlert("Error", "The email address is already in use.");
                                break;
                            case "INVALID_EMAIL":
                                showAlert("Error", "The email address is not valid.");
                                break;
                            case "USERNAME_EXISTS":
                                showAlert("Error", "The username is already taken.");
                                break;
                            case "WEAK_PASSWORD : Password should be at least 6 characters":
                                showAlert("Error", "The password is too weak.");
                                break;
                            default:
                                showAlert("Error", "Signup failed. Please try again.");
                                break;
                        }
                    });
                }
            }).start();
        });


        EventHandler<KeyEvent> enterKeyHandler = event -> {
            if (event.getCode() == KeyCode.ENTER) {
                signupButton.fire(); // Simulates button click
            }
        };

        usernameField.setOnKeyPressed(enterKeyHandler);
        emailField.setOnKeyPressed(enterKeyHandler);
        passwordField.setOnKeyPressed(enterKeyHandler);
        confirmPasswordField.setOnKeyPressed(enterKeyHandler);

        signupContainer.getChildren().addAll(usernameContainer, emailContainer, passwordContainer, confirmPasswordContainer, signupButton);
        stackPane.getChildren().addAll(logoImageView, backButtonContainer, signupContainer);

        // Alignments in StackPane
        StackPane.setAlignment(logoImageView, Pos.TOP_CENTER);
        StackPane.setAlignment(signupContainer, Pos.CENTER);
        StackPane.setAlignment(backButtonContainer, Pos.TOP_LEFT);

        borderPane.setCenter(stackPane);

        return new Scene(borderPane, 875, 625);
    }

    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
