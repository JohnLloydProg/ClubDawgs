package com.unidawgs.le5.clubdawgs;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Comparator;

public class Backyard extends Room{
    private WalkSpace houseEntry = new WalkSpace(150, 200, 65, 30);

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
    public String collisionHandler(Player player) {
        super.collisionHandler(player);
        if (this.houseEntry.isHit(player) && this.roomId.contains(player.getUserName())) {
            return "Entering House";
        }
        return "";
    }
}
