package com.unidawgs.le5.clubdawgs;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class Main extends Application {
    private Player player;

    @Override
    public void start(Stage stage) {
        VBox root = new VBox();
        Canvas c = new Canvas(500, 500);
        GraphicsContext gc = c.getGraphicsContext2D();
        root.getChildren().add(c);

        Firebase firebase = new Firebase();
        User user = firebase.signIn("johnlloydunida0@gmail.com", "45378944663215");
        player = new Player(0, 0, "Hello");
        firebase.updateLocation(player, user.getLocalId(), user.getIdToken(), "hotdog");
        ArrayList<Player> players = new ArrayList<>();

        AnimationTimer mainLoop = new AnimationTimer() {
            long lastTick = 0;

            public void handle(long l) {

                if (lastTick == 0) {
                    lastTick = l;
                    return;
                }

                if (l - lastTick > 1000000000/30) {
                    if (player.isMoving()) {
                        firebase.updateLocation(player, user.getLocalId(), user.getIdToken(), "hotdog");
                    }
                    firebase.getPlayers(players, user.getLocalId(), user.getIdToken(), "hotdog");
                    tick(gc, players);
                    lastTick = l;
                }
            }
        };
        mainLoop.start();

        Scene scene = new Scene(root, 500, 500);
        stage.setTitle("Hello!");
        stage.setScene(scene);

        stage.setOnHiding((event) -> {
            mainLoop.stop();
            firebase.quitPlayer(user.getLocalId(), user.getIdToken(), "hotdog");
        });
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> KeyEventHandler.keyPressed(event, player));
        scene.addEventHandler(KeyEvent.KEY_RELEASED, (event) -> KeyEventHandler.keyReleased(event, player));

        stage.show();
    }

    public void tick(GraphicsContext gc, ArrayList<Player> players) {

        player.move();

        gc.setFill(Color.rgb(255, 255, 255));
        gc.fillRect(0, 0, 500, 500);
        player.draw(gc);
        for (Player player1 : players) {
            player1.draw(gc);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}