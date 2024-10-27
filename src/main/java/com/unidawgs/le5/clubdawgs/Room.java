package com.unidawgs.le5.clubdawgs;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

public abstract class Room extends Canvas {
    protected Image bgImg;
    protected ArrayList<DropBox> dropBoxes = new ArrayList<>();
    protected ArrayList<Player> players = new ArrayList<>();
    protected final String roomId;

    public Room(double width, double height, String roomId, Image bgImg) {
        super(width, height);
        this.roomId = roomId;
        this.bgImg = bgImg;
    }

    public String getRoomId() {
        return this.roomId;
    }

    public void updateDropBoxes(String idToken) {
        Runnable getDropBoxes = (Runnable) () -> {
            Firebase.getDropBoxes(idToken, this.roomId, this.dropBoxes);
        };
        Thread newThread = new Thread(getDropBoxes);
        newThread.setDaemon(true);
        newThread.start();
    }

    public void updatePlayers(String localId, String idToken) {
        Firebase.getPlayers(localId, idToken, this.roomId, this.players);
    }

    public ArrayList<DropBox> getDropBoxes() {
        return this.dropBoxes;
    }

    public void collisionHandler(Player player) {
        for (DropBox dropBox : this.dropBoxes) {
            dropBox.isHit(player);
        }
    }

    public abstract void drawRoom(Player player);

}
