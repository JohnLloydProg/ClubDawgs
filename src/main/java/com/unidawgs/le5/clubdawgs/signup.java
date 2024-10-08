package com.unidawgs.le5.clubdawgs;


import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
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
    
        // Login Container
        VBox signupContainer = new VBox(15);
        signupContainer.setAlignment(Pos.CENTER);
        signupContainer.setStyle("-fx-background-color: white; -fx-padding: 30px; -fx-background-radius: 10;");
        StackPane.setMargin(signupContainer, new Insets(200,225,255,200));
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
    
        signupContainer.getChildren().addAll(usernameContainer, emailContainer, passwordContainer, confirmPasswordContainer, signupButton);
    
        //Back button Container
        VBox backButtonContainer = new VBox();
        backButtonContainer.setAlignment(Pos.TOP_LEFT);

        // Back Button Image
        Image backImage = new Image(Main.class.getResource("backBtn.png").toExternalForm());
        ImageView backImageView = new ImageView(backImage);
        
       //Back Button
        Button backButton = new Button();
        backButton.setGraphic(backImageView);
        backButton.setStyle("-fx-background-color: transparent;");
        backImageView.setFitWidth(100);
        backImageView.setPreserveRatio(true);
        backButton.setOnAction(e -> {
            app.getStage().setScene(app.getLogin());
        });

       
     
        backButtonContainer.getChildren().add(backButton); 
        
        stackPane.getChildren().addAll(logoImageView, signupContainer, backButtonContainer);
    
        //Alignment in stack pane
        StackPane.setAlignment(logoImageView, Pos.TOP_CENTER);
        StackPane.setAlignment(signupContainer, Pos.CENTER);
        StackPane.setAlignment(backButtonContainer, Pos.TOP_LEFT);
    

        borderPane.setCenter(stackPane);
    
        return new Scene(borderPane, 875, 625);
    }
    

}
