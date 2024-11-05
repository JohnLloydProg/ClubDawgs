package com.unidawgs.le5.clubdawgs;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Comparator;

public class PersonalRoom extends Room{
    private WalkSpace houseExit = new WalkSpace(315, 570, 200, 50);

    public PersonalRoom(double width, double height, String roomId) {
        super(width, height, roomId, new Image(Main.class.getResource("room.png").toString()));
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

    // Code collision handling for exiting room
    @Override
    public String collisionHandler(Player player) {
        super.collisionHandler(player);
        if (this.houseExit.isHit(player)) {
            return "Exiting House";
        }
        return "";
    }
}
