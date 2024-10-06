package com.unidawgs.le5.clubdawgs;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
    CharacterMovement movement = new CharacterMovement();
    int x = 0;
    int y = 0;

    @Override
    public void start(Stage stage) {
        VBox root = new VBox();
        Canvas c = new Canvas(500, 500);
        GraphicsContext gc = c.getGraphicsContext2D();
        root.getChildren().add(c);

        new AnimationTimer() {
            long lastTick = 0;

            public void handle(long l) {

                if (lastTick == 0) {
                    lastTick = l;
                    return;
                }

                if (l - lastTick > 1000000000/60) {
                    tick(gc);
                    lastTick = l;
                }
            }
        }.start();

        Scene scene = new Scene(root, 500, 500);
        stage.setTitle("Hello!");
        stage.setScene(scene);

        scene.addEventFilter(KeyEvent.KEY_PRESSED, (key) -> keyPressedEventHandler(key));
        scene.addEventFilter(KeyEvent.KEY_RELEASED, (key) -> keyReleasedEventHandler(key));

        stage.show();
    }

    public void keyPressedEventHandler(KeyEvent key) {
        if (key.getCode() == KeyCode.UP) {
            movement.north = true;
        }
        if (key.getCode() == KeyCode.DOWN) {
            movement.south = true;
        }
        if (key.getCode() == KeyCode.LEFT) {
            movement.west = true;
        }
        if (key.getCode() == KeyCode.RIGHT) {
            movement.east = true;
        }
    }

    public void keyReleasedEventHandler(KeyEvent key) {
        if (key.getCode() == KeyCode.UP) {
            movement.north = false;
        }
        if (key.getCode() == KeyCode.DOWN) {
            movement.south = false;
        }
        if (key.getCode() == KeyCode.LEFT) {
            movement.west = false;
        }
        if (key.getCode() == KeyCode.RIGHT) {
            movement.east = false;
        }
    }

    public void tick(GraphicsContext gc) {

        if (this.movement.north && y >= 0) {
            y -= 5;
        }

        if (this.movement.south && y + 50 <= 500) {
            y += 5;
        }

        if (this.movement.east && x + 50 <= 500) {
            x += 5;
        }

        if (this.movement.west && x >= 0) {
            x -= 5;
        }

        gc.setFill(Color.rgb(255, 255, 255));
        gc.fillRect(0, 0, 500, 500);
        gc.setFill(Color.rgb(255, 0, 0));
        gc.fillRect(x, y, 50, 50);
    }

    public static void main(String[] args) {
        launch();
    }
}