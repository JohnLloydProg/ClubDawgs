package com.unidawgs.le5.clubdawgs.rooms;

import com.unidawgs.le5.clubdawgs.Game;
import com.unidawgs.le5.clubdawgs.events.OverlayEvent;
import com.unidawgs.le5.clubdawgs.objects.DrawableEntity;
import com.unidawgs.le5.clubdawgs.Main;
import com.unidawgs.le5.clubdawgs.objects.Item;
import com.unidawgs.le5.clubdawgs.objects.Player;
import com.unidawgs.le5.clubdawgs.overlays.Gacha;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.Comparator;

public class Lobby extends Room {
    private Item gachaItem = new Item("Gacha",360, 135, 50, 20, new Image(Main.class.getResource("drawer.jfif").toString()), 0, -30, 50, 50);

    public Lobby(double width, double height, String roomId) {
        super(width, height, roomId, new Image(Main.class.getResource("lobby layout.png").toString()));
        this.clickableObjects.add(gachaItem);
    }

    @Override
    public void drawRoom(Player player) {
        ArrayList<DrawableEntity> entities = new ArrayList<>(this.dropBoxes);
        entities.addAll(this.players);
        entities.add(player);
        entities.add(this.gachaItem);
        this.getGraphicsContext2D().drawImage(this.bgImg, 0, 0, this.getWidth(), this.getHeight());

        entities.sort(Comparator.comparingDouble(DrawableEntity::getTop));

        for (DrawableEntity entity : entities) {
            entity.draw(this.getGraphicsContext2D());
        }
    }

    @Override
    public void mouseClickHandler(MouseEvent mouse) {
        super.mouseClickHandler(mouse);
        if (this.gachaItem.isClicked(mouse)) {
            this.fireEvent(new OverlayEvent(Game.DISPLAYING_OVERLAY, new Gacha()));
        }
    }

    @Override
    public void collisionHandler(Player player) {
        super.collisionHandler(player);
        this.gachaItem.isHit(player);
    }
}
