package com.unidawgs.le5.clubdawgs;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Set;

public class Game {
    private Player player;
    private String roomId;
    private AnimationTimer mainLoop;
    private Scene scene;

    public Game(Firebase firebase, User user) {
        HBox root = new HBox();

        VBox gameCol = new VBox();

        Canvas c = new Canvas(Settings.gameWidth, Settings.gameHeight);
        GraphicsContext gc = c.getGraphicsContext2D();
        gameCol.getChildren().add(c);

        HBox toolRow = new HBox();
        toolRow.setPrefWidth(Settings.gameWidth);
        toolRow.setPrefHeight(Settings.winHeight - Settings.gameHeight);
        toolRow.setStyle("-fx-background-color: #555555");
        gameCol.getChildren().add(toolRow);

        VBox chatCol = new VBox();
        chatCol.setPrefHeight(Settings.winHeight);
        chatCol.setPrefWidth(Settings.winWidth - Settings.gameWidth);
        chatCol.setStyle("-fx-background-color: #333333");

        root.getChildren().addAll(gameCol, chatCol);

        this.roomId = user.getUsername()+ "-r";
        player = new Player(0, 0, user.getUsername());
        firebase.updateLocation(player, user.getLocalId(), user.getIdToken(), this.roomId);
        ArrayList<Player> players = new ArrayList<>();

        this.mainLoop = new AnimationTimer() {
            long lastTick = 0;

            public void handle(long l) {

                if (lastTick == 0) {
                    lastTick = l;
                    return;
                }

                if (l - lastTick > 1000000000/30) {
                    if (player.isMoving()) {
                        firebase.updateLocation(player, user.getLocalId(), user.getIdToken(), roomId);
                    }
                    firebase.getPlayers(players, user.getLocalId(), user.getIdToken(), roomId);
                    tick(gc, players);
                    lastTick = l;
                }
            }
        };
        this.mainLoop.start();

        this.scene = new Scene(root, Settings.winWidth, Settings.winHeight);

        this.scene.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> KeyEventHandler.keyPressed(event, player));
        this.scene.addEventHandler(KeyEvent.KEY_RELEASED, (event) -> KeyEventHandler.keyReleased(event, player));
    }

    public AnimationTimer getMainLoop() {
        return this.mainLoop;
    }

    public Scene getScene() {
        return this.scene;
    }

    public String getRoomId() {
        return this.roomId;
    }

    public void tick(GraphicsContext gc, ArrayList<Player> players) {

        player.move();

        gc.setFill(Color.rgb(255, 255, 255));
        gc.fillRect(0, 0, Settings.gameWidth, Settings.gameHeight);
        player.draw(gc);
        for (Player player1 : players) {
            player1.draw(gc);
        }
    }

}
