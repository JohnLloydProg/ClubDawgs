package com.unidawgs.le5.clubdawgs.rooms;

import com.unidawgs.le5.clubdawgs.Game;
import com.unidawgs.le5.clubdawgs.objects.ClickableObject;
import com.unidawgs.le5.clubdawgs.objects.DropBox;
import com.unidawgs.le5.clubdawgs.Firebase;
import com.unidawgs.le5.clubdawgs.Main;
import com.unidawgs.le5.clubdawgs.objects.Player;
import javafx.event.Event;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;

public abstract class Room extends Canvas {
    protected Image bgImg;
    protected ArrayList<DropBox> dropBoxes = new ArrayList<>();
    protected ArrayList<Player> players = new ArrayList<>();
    protected final String roomId;
    protected ArrayList<ClickableObject> clickableObjects = new ArrayList<>();

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

    public void mouseClickHandler(MouseEvent mouse) {
        Room room = (Room) mouse.getSource();
        for (DropBox box : this.dropBoxes) {
            if (box.isClicked(mouse)) {
                box.interact(this.roomId);
                room.fireEvent(new Event(Game.MOUSE_EXIT));
            }else if (box.rightClicked(mouse)) {
                box.delete(this.roomId);
                room.fireEvent(new Event(Game.MOUSE_EXIT));
            }
        }
    }

    public void mouseMoveHandler(MouseEvent mouse) {
        for (DropBox box : this.dropBoxes) {
            box.enters(mouse);
            box.exits(mouse);
        }

        for (ClickableObject clickableObject : this.clickableObjects) {
            clickableObject.enters(mouse);
            clickableObject.exits(mouse);
        }
    }

    public abstract void drawRoom(Player player);

}
