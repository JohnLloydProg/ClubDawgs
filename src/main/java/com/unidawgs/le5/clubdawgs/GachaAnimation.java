package com.unidawgs.le5.clubdawgs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import com.unidawgs.le5.clubdawgs.rooms.Room;
import com.unidawgs.le5.clubdawgs.objects.User;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class GachaAnimation extends StackPane {
    private boolean isFinished = false;
    private int wonCosmetic;
    private ImageView gachaBall;
    private String category;
    private Image testPlaceholder;
    private ImageView test;

    public GachaAnimation(Room room) throws IOException {
        super();
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
        

        category = "5-star";

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
        gachaBall.setVisible(false);

        //ray
        Image rayLayer = new Image(getClass().getResource("gacha/ray.png").toExternalForm());
        ImageView ray = new ImageView(rayLayer);
        ray.setVisible(false);

        //sprite placeholder
        testPlaceholder = new Image(getClass().getResource("gacha/test.png").toExternalForm());
        test = new ImageView(testPlaceholder);
        test.setFitWidth(200);
        test.setPreserveRatio(true);
        test.setSmooth(true);
        test.setCache(true);
        test.setVisible(false);

        //Blackscreen
        Rectangle blackScreen = new Rectangle(0, 0, 875, 625);
        blackScreen.setFill(Color.BLACK);
        blackScreen.setOpacity(0); 

        //Label Container
        VBox labelContainer = new VBox(425);
        labelContainer.setAlignment(Pos.CENTER);
        
        //Font Label
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
        categoryLabel.setVisible(false);


        labelContainer.getChildren().addAll(congratulationsLabel, categoryLabel);
        labelContainer.setVisible(false);

        //Button Container
        Pane buttonContainer = new Pane();
        HBox extBtnContainer = new HBox();
        Image exitButton = new Image(getClass().getResource("exitButton.png").toExternalForm());
        ImageView extImage = new ImageView(exitButton);
        Button extBtn = new Button();
        extBtn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        extBtn.setGraphic(extImage);
        extImage.setFitWidth(50);
        extImage.setPreserveRatio(true);
        extBtnContainer.getChildren().addAll(extBtn);

        HBox pullBtnContainer = new HBox();
        Image pullButton = new Image(getClass().getResource("pullButton.png").toExternalForm());
        ImageView pullImage = new ImageView(pullButton);
        Button pullBtn = new Button();
        pullBtn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        pullBtn.setGraphic(pullImage);
        pullBtnContainer.getChildren().addAll(pullBtn);

        extBtnContainer.setLayoutX(800);
        extBtnContainer.setLayoutY(10);

        pullBtnContainer.setLayoutX(300);
        pullBtnContainer.setLayoutY(520);
        buttonContainer.getChildren().addAll(extBtnContainer, pullBtnContainer);

        //Animations
        //GachaBall
        TranslateTransition moveUpBall= new TranslateTransition(Duration.seconds(1.5), gachaBall);
        moveUpBall.setFromX(300); 
        moveUpBall.setFromY(175); 
        moveUpBall.setToY(0); 

        TranslateTransition moveLeftBall= new TranslateTransition(Duration.seconds(1.5), gachaBall);
        moveLeftBall.setFromX(300); 
        moveLeftBall.setFromY(0); 
        moveLeftBall.setToX(0);

        TranslateTransition moveDownBall= new TranslateTransition(Duration.seconds(1), gachaBall);
        moveDownBall.setFromX(0); 
        moveDownBall.setFromY(0);
        moveDownBall.setToY(300);
  
        //Claw animation
        TranslateTransition moveRightClaw = new TranslateTransition(Duration.seconds(1.5), claws[0]);
        moveRightClaw.setFromX(0);
        moveRightClaw.setFromY(0);
        moveRightClaw.setToX(300);
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
        changeClaw.setOnFinished(event -> {
            claws[0].setImage(new Image(getClass().getResource(clawImages[1]).toExternalForm()));
            gachaBall.setVisible(true);

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
            pullBtn.setVisible(false);
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
            extBtn.setVisible(false);
            labelContainer.setVisible(true);

            zoomIn.setOnFinished(e -> {
                this.isFinished = true;
            });
        });
        
        //Full Sequeunce of Animation
        SequentialTransition sequentialTransitionClaw = new SequentialTransition(moveRightClaw, moveDownClaw, changeClaw, parallelTransitionUp, parallelTransitionLeft, moveDownBall, fadeIn);


         //Buttons
        pullBtn.setOnAction(e -> {
            System.out.println("Button pressed");
            User user = Main.getUser();
            ArrayList<Integer> ownedCosmetics = Firebase.getCosmetics(user.getLocalId(), user.getIdToken());

            if (user.getCurrency() >= 160 && ownedCosmetics.size() < 35) {
                user.setCurrency(Firebase.changeCurrency(user.getLocalId(), user.getIdToken(), -160));
                do {
                    this.wonCosmetic = new Random().nextInt(1, 35);
                } while (ownedCosmetics.contains(this.wonCosmetic));
                Firebase.addCosmetic(user.getLocalId(), user.getIdToken(), this.wonCosmetic);
            }else {
                return;
            }

            this.getChildren().remove(gachaBall);

            if (Settings.fiveStars.contains(this.wonCosmetic)) {
                category = "5-star";
            }else if (Settings.fourStars.contains(this.wonCosmetic)) {
                category = "4-star";
            }else if (Settings.threeStars.contains(this.wonCosmetic)) {
                category = "3-star";
            }else {
                category = "default";
            }

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

            this.getChildren().remove(test);

            testPlaceholder = new Image(getClass().getResource("gacha/sprite_" + this.wonCosmetic +".png").toExternalForm());
            test = new ImageView(testPlaceholder);
            test.setFitWidth(200);
            test.setPreserveRatio(true);
            test.setSmooth(true);
            test.setCache(true);
            test.setVisible(false);

            this.getChildren().add(6, this.test);

            gachaBall.setFitWidth(875);
            gachaBall.setPreserveRatio(true);
            gachaBall.setSmooth(true);
            gachaBall.setCache(true);
            gachaBall.setVisible(false);

            moveUpBall.setNode(gachaBall);
            moveLeftBall.setNode(gachaBall);
            moveDownBall.setNode(gachaBall);

            this.getChildren().add(2, gachaBall);

            sequentialTransitionClaw.setCycleCount(1);
            sequentialTransitionClaw.play();
            this.isFinished = false;
        });

        extBtn.setOnAction(e -> {
            room.fireEvent(new Event(Game.HIDE_GACHA));
        });

        this.setOnMouseClicked(mouse -> {
            if (mouse.getButton() == MouseButton.PRIMARY && this.isFinished) {
                sequentialTransitionClaw.stop();
                test.setScaleX(1.0);
                test.setScaleY(1.0);
                test.setVisible(false);
                gachaBall.setVisible(false);
                labelContainer.setVisible(false);
                ray.setVisible(false);
                extBtn.setVisible(true);
                pullBtn.setVisible(true);
                blackScreen.setOpacity(0);
            }
        });



        this.getChildren().addAll(layer1, claws[0], gachaBall, layer2, blackScreen, ray, test, labelContainer, buttonContainer);
    }

}