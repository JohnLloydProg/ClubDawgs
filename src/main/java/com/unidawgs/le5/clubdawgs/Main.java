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
    int x = 0;
    int y = 0;
    boolean moving = false;
    int yDir = 1;
    int xDir = 1;

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



        stage.show();
    }

    public void tick(GraphicsContext gc) {

        if (this.moving && y >= 0 && yDir < 0) {
            y += 5 * yDir;
        }

        if (this.moving && y + 50 <= 500 && yDir > 0) {
            y += 5 * yDir;
        }

        if (this.moving && x + 50 <= 500 && xDir > 0) {
            x += 5 * xDir;
        }

        if (this.moving && x >= 0 && xDir < 0) {
            x += 5 * xDir;
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