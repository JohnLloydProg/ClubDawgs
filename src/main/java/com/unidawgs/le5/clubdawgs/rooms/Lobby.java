package com.unidawgs.le5.clubdawgs.rooms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.unidawgs.le5.clubdawgs.Game;
import com.unidawgs.le5.clubdawgs.Main;
import com.unidawgs.le5.clubdawgs.Minigame1;
import com.unidawgs.le5.clubdawgs.Minigame2_Main;
import com.unidawgs.le5.clubdawgs.objects.DrawableEntity;
import com.unidawgs.le5.clubdawgs.objects.Item;
import com.unidawgs.le5.clubdawgs.objects.Player;
import com.unidawgs.le5.clubdawgs.objects.WalkSpace;

import javafx.event.Event;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class Lobby extends Room {
    private Item gachaItem = new Item("Gacha",360, 135, 50, 20, new Image(Main.class.getResource("drawer.jfif").toString()), 0, -30, 50, 50);
    private Item playground1 = new Item("Playground 1", 150, 300, 104, 20, new Image(Main.class.getResource("playground 1.png").toString()), 0, -81, 104, 101);
    private Item playground2 = new Item("Playground 2", 270, 440, 262, 20, new Image(Main.class.getResource("playground 2.png").toString()), 0, -93, 262, 113);
    private Item playground3 = new Item("Playground 3", 405, 250, 95, 20, new Image(Main.class.getResource("playground 3.png").toString()), 0, -91, 95, 111);
    private Item playground4 = new Item("Playground 4", 630, 450, 101, 20, new Image(Main.class.getResource("playground 4.png").toString()), 0, -36, 101, 56);
    private Item playground5 = new Item("Playground 5", 650, 300, 110, 20, new Image(Main.class.getResource("playground 5.png").toString()), 0, -79, 110, 99);
    private Item minigame1 = new Item("Minigame 1", 630, 410, 256, 30, new Image(Main.class.getResource("playground mg1.png").toString()), 0, -182, 256, 212);
    private Item minigame2 = new Item("Minigame 2", 117, 395, 120, 30, new Image(Main.class.getResource("playground mg2.png").toString()), 0, -81, 120, 111);
    private ArrayList<Item> playgroundItems = new ArrayList<>(List.of(playground1, playground2, playground3, playground4, playground5));
    private ArrayList<WalkSpace> barriers = new ArrayList<>(List.of(
            new WalkSpace(261, 51, 321, 22),
            new WalkSpace(584, 78, 138, 23),
            new WalkSpace(722, 55, 46, 17),
            new WalkSpace(770, 76, 24, 24),
            new WalkSpace(798, 103, 25, 60),
            new WalkSpace(830, 162, 26, 92),
            new WalkSpace(802, 332, 68, 19),
            new WalkSpace(802, 351, 23, 118),
            new WalkSpace(827, 470, 23, 95),
            new WalkSpace(707, 547, 121, 21),
            new WalkSpace(603, 574, 105, 18),
            new WalkSpace(136, 545, 467, 24),
            new WalkSpace(72, 516, 64, 26),
            new WalkSpace(44, 452, 28, 64),
            new WalkSpace(11, 362, 30, 90),
            new WalkSpace(11, 156, 28, 66),
            new WalkSpace(44, 144, 57, 18),
            new WalkSpace(102, 112, 61, 21),
            new WalkSpace(164, 76, 94, 24)
    ));

    public Lobby(double width, double height, String roomId) {
        super(width, height, roomId, new Image(Main.class.getResource("base layer lobby.png").toString()));
        this.clickableObjects.add(gachaItem);
        this.clickableObjects.add(this.minigame1);
        this.clickableObjects.add(this.minigame2);
    }

    @Override
    public void drawRoom(Player player) {
        ArrayList<DrawableEntity> entities = new ArrayList<>(this.dropBoxes);
        entities.addAll(this.players);
        entities.addAll(this.playgroundItems);
        entities.add(player);
        entities.add(this.gachaItem);
        entities.add(this.minigame1);
        entities.add(this.minigame2);
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
            this.fireEvent(new Event(Game.SHOW_GACHA));
        }else if (this.minigame1.isClicked(mouse)) {
            new Minigame1(this.roomId).start(new Stage());
        }else if (this.minigame2.isClicked(mouse)) {
            int boardWidth = 360;
            int boardHeight = 640;

            Stage primaryStage = new Stage();

            Minigame2_Main minigame2Main = new Minigame2_Main(boardWidth, boardHeight, this.roomId);
            minigame2Main.start(primaryStage);

            primaryStage.setTitle("Flappy Dawg");
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.show();
        }
    }

    @Override
    public void collisionHandler(Player player) {
        super.collisionHandler(player);
        this.gachaItem.isHit(player);
        for (Item playground : this.playgroundItems) {
            playground.isHit(player);
        }
        for (WalkSpace barrier : this.barriers) {
            barrier.isHit(player);
        }
        this.minigame1.isHit(player);
        //this.minigame2.isHit(player);
    }
}
