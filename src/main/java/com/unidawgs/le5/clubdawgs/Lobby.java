package com.unidawgs.le5.clubdawgs;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Comparator;

public class Lobby extends Room{

    public Lobby(double width, double height, String roomId) {
        super(width, height, roomId, new Image(Main.class.getResource("lobby layout.png").toString()));
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
}
