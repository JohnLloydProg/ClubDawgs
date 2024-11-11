package com.unidawgs.le5.clubdawgs.rooms;

import com.unidawgs.le5.clubdawgs.*;
import com.unidawgs.le5.clubdawgs.events.RoomEvent;
import com.unidawgs.le5.clubdawgs.objects.DrawableEntity;
import com.unidawgs.le5.clubdawgs.objects.Player;
import com.unidawgs.le5.clubdawgs.objects.WalkSpace;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Comparator;

public class Backyard extends Room {
    private WalkSpace houseEntry = new WalkSpace(150, 200, 65, 30);
    //private WalkSpace mainLobbyEntry = new WalkSpace();

    public Backyard(double width, double height, String roomId) {
        super(width, height, roomId, new Image(Main.class.getResource("backyard layout.png").toString()));
    }

    @Override
    public void drawRoom(Player player) {
        ArrayList<DrawableEntity> entities = new ArrayList<>(this.dropBoxes);
        entities.addAll(this.players);
        entities.add(player);
        this.getGraphicsContext2D().drawImage(this.bgImg, 0, 0, this.getWidth(), this.getHeight());

        entities.sort(Comparator.comparingDouble(DrawableEntity::getTop));

        for (DrawableEntity entity : entities) {
            entity.draw(this.getGraphicsContext2D());
        }
    }

    @Override
    public void collisionHandler(Player player) {
        super.collisionHandler(player);
        if (this.houseEntry.isHit(player) && this.roomId.contains(player.getUserName())) {
            this.fireEvent(new RoomEvent(Game.ROOM_TRANSITION, Main.getUser().getUsername() + "-pr"));
        }
    }
}
