package com.unidawgs.le5.clubdawgs;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class GachaAnimation extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        StackPane root = new StackPane();
        String[] clawImages = {
            "gacha/claw 1.png", "gacha/claw 2.png", "gacha/claw 3.png"
        };

        ImageView[] claws = new ImageView[3];
        for (int i = 0; i < clawImages.length; i++) {
            claws[i] = new ImageView(new Image(getClass().getResource(clawImages[i]).toExternalForm()));
            claws[i].setFitWidth(875);
            claws[i].setPreserveRatio(true);
            claws[i].setSmooth(true);
            claws[i].setCache(true);
        }
       
        Image baseLayer = new Image(getClass().getResource("gacha/gacha layer 1.png").toExternalForm());
        ImageView layer1 = new ImageView(baseLayer); 
        
        layer1.setFitWidth(875);
        layer1.setPreserveRatio(true);
        layer1.setSmooth(true);
        layer1.setCache(true);

        Image secondLayer = new Image(getClass().getResource("gacha/gacha layer 2.png").toExternalForm());
        ImageView layer2 = new ImageView(secondLayer);

        layer2.setFitWidth(875);
        layer2.setPreserveRatio(true);
        layer2.setSmooth(true);
        layer2.setCache(true);

        Image blueGacha = new Image(getClass().getResource("gacha/blue-3-star.png").toExternalForm());
        Image redGacha = new Image(getClass().getResource("gacha/red-4-star.png").toExternalForm());
        Image goldGacha = new Image(getClass().getResource("gacha/gold-5-star.png").toExternalForm());
        ImageView gachaBall;
        

        String category = "5-star"; 

        // Set gachaBall based on category
        switch (category) {
            case "3-star":
                gachaBall = new ImageView(blueGacha);
                break;
            case "4-star":
                gachaBall = new ImageView(redGacha);
                break;
            case "5-star":
                gachaBall = new ImageView(goldGacha);
                break;
            default:
                gachaBall = new ImageView(blueGacha); // Default to blue for unknown categories
                break;
        }

        gachaBall.setFitWidth(875);
        gachaBall.setPreserveRatio(true);
        gachaBall.setSmooth(true);
        gachaBall.setCache(true);

        Image rayLayer = new Image(getClass().getResource("gacha/ray.png").toExternalForm());
        ImageView ray = new ImageView(rayLayer);
        ray.setVisible(false);

        Image testPlaceholder = new Image(getClass().getResource("gacha/test.png").toExternalForm());
        ImageView test = new ImageView(testPlaceholder);
        test.setFitWidth(200);
        test.setPreserveRatio(true);
        test.setSmooth(true);
        test.setCache(true);
        test.setVisible(false);

        Rectangle blackScreen = new Rectangle(0, 0, 875, 625); 
        blackScreen.setFill(Color.BLACK);
        blackScreen.setOpacity(0); 

        VBox labelContainer = new VBox(425);
        labelContainer.setAlignment(Pos.CENTER);
        
        String fontPath = "ARCADE_N.ttf";
        InputStream fontStream = getClass().getResourceAsStream(fontPath);
        if (fontStream == null) {
            throw new FileNotFoundException("Font file not found");
        }
        Font labelStyle = Font.loadFont(fontStream, 50);
    
        Label congratulationsLabel = new Label("CONGRATULATIONS!");
        congratulationsLabel.setFont(labelStyle);
        congratulationsLabel.setStyle("-fx-text-fill: white;");
    
        Label categoryLabel = new Label(category);
        categoryLabel.setFont(labelStyle);
        categoryLabel.setStyle("-fx-text-fill: white;");


        labelContainer.getChildren().addAll(congratulationsLabel, categoryLabel);
        labelContainer.setVisible(false);

        //Animations
        //GachaBall
        TranslateTransition moveUpBall= new TranslateTransition(Duration.seconds(1.5), gachaBall);
        moveUpBall.setFromX(300); 
        moveUpBall.setFromY(175); 
        moveUpBall.setToY(0); 
        moveUpBall.play();
        
        TranslateTransition moveLeftBall= new TranslateTransition(Duration.seconds(1.5), gachaBall);
        moveLeftBall.setFromX(300); 
        moveLeftBall.setFromY(0); 
        moveLeftBall.setToX(0); 
        moveLeftBall.play();

        TranslateTransition moveDownBall= new TranslateTransition(Duration.seconds(1), gachaBall);
        moveDownBall.setFromX(0); 
        moveDownBall.setFromY(0);
        moveDownBall.setToY(300);
  
        //Claw animation
        TranslateTransition moveRightClaw = new TranslateTransition(Duration.seconds(1.5), claws[0]);
        moveRightClaw.setByX(300);
        moveRightClaw.setOnFinished(event -> {
            claws[0].setImage(new Image(getClass().getResource(clawImages[1]).toExternalForm()));  
        });

        TranslateTransition moveDownClaw = new TranslateTransition(Duration.seconds(1.5), claws[0]);
        moveDownClaw.setByY(175);
        moveRightClaw.setOnFinished(event -> {
            claws[0].setImage(new Image(getClass().getResource(clawImages[1]).toExternalForm()));  
            claws[0].setImage(new Image(getClass().getResource(clawImages[2]).toExternalForm()));  
        });
        
        TranslateTransition changeClaw = new TranslateTransition(Duration.seconds(1.5), claws[0]);
        moveDownClaw.setByY(175);
        moveRightClaw.setOnFinished(event -> {
            claws[0].setImage(new Image(getClass().getResource(clawImages[1]).toExternalForm()));  
        });
    
        TranslateTransition moveUpClaw = new TranslateTransition(Duration.seconds(1.5), claws[0]);
        moveUpClaw.setByY(-175);
        moveUpClaw.setOnFinished(event -> {
            claws[0].setImage(new Image(getClass().getResource(clawImages[1]).toExternalForm()));  
        });

        TranslateTransition moveLeftClaw = new TranslateTransition(Duration.seconds(1.5), claws[0]);
        moveLeftClaw.setByX(-300);
        moveLeftClaw.setOnFinished(event -> {
            claws[0].setImage(new Image(getClass().getResource(clawImages[2]).toExternalForm()));  
        });

        ParallelTransition parallelTransitionUp = new ParallelTransition( moveUpClaw, moveUpBall);
        ParallelTransition parallelTransitionLeft = new ParallelTransition( moveLeftClaw, moveLeftBall);
        
        //Reveal
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.7), blackScreen);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(0.7); 

        fadeIn.setOnFinished(event -> {
            ray.setVisible(true);
            RotateTransition rotateRay = new RotateTransition(Duration.seconds(5), ray);  
            rotateRay.setByAngle(360);
            rotateRay.setCycleCount(RotateTransition.INDEFINITE);  
            rotateRay.setInterpolator(Interpolator.LINEAR);  
            rotateRay.play();
            ScaleTransition zoomIn = new ScaleTransition(Duration.seconds(1), test);  
            test.setVisible(true);
            zoomIn.setByX(1);  
            zoomIn.setByY(1);  
            zoomIn.setCycleCount(1); 
            zoomIn.setInterpolator(Interpolator.EASE_IN); 
            zoomIn.play();  
            labelContainer.setVisible(true);
        });
        
        //Full Sequeunce of Animation
        SequentialTransition sequentialTransitionClaw = new SequentialTransition(moveRightClaw, moveDownClaw, changeClaw, parallelTransitionUp, parallelTransitionLeft, moveDownBall, fadeIn);
        sequentialTransitionClaw.setCycleCount(1); 
        sequentialTransitionClaw.play(); 

        root.getChildren().addAll(layer1, claws[0], gachaBall, layer2, blackScreen, ray, test, labelContainer);
    
        Scene scene = new Scene(root, 875, 625);
        stage.setScene(scene);
        stage.show();
    }

    
    public static void main(String[] args) {
        launch();
    }

}