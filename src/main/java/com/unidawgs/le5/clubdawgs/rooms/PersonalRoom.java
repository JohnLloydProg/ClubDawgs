package com.unidawgs.le5.clubdawgs.rooms;

import com.unidawgs.le5.clubdawgs.*;
import com.unidawgs.le5.clubdawgs.events.OverlayEvent;
import com.unidawgs.le5.clubdawgs.events.RoomEvent;
import com.unidawgs.le5.clubdawgs.objects.DrawableEntity;
import com.unidawgs.le5.clubdawgs.objects.Item;
import com.unidawgs.le5.clubdawgs.objects.Player;
import com.unidawgs.le5.clubdawgs.objects.WalkSpace;
import com.unidawgs.le5.clubdawgs.overlays.CosmeticSelection;
import com.unidawgs.le5.clubdawgs.overlays.LevelUpOverlay;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PersonalRoom extends Room {
    private WalkSpace houseExit = new WalkSpace(315, 600, 200, 20);
    private Item wall = new Item("Wall", 0, 0, Settings.gameWidth, 145, new Image(Main.class.getResource("furniture/base.png").toString()), 0, 0, Settings.gameWidth, 145);
    private Item bed = new Item("Bed", 338, 147, 193, 130, new Image(Main.class.getResource("furniture/level 2.png").toString()), 0, -80, 193, 202);
    private Item leftCabinet = new Item("Left Cabinet", 79, 73, 86, 105, new Image(Main.class.getResource("furniture/level 6.png").toString()), 0, 0, 86, 105);
    private Item tallPlant = new Item("Tall Plant", 23, 154, 48, 38, new Image(Main.class.getResource("furniture/level 14.png").toString()), 0, -46, 48, 84);
    private Item leftSmallPlant = new Item("Left Snall Plant", 176, 102, 71, 71, new Image(Main.class.getResource("furniture/level 13.png").toString()), 0, 0, 71, 71);
    private Item leftLamp = new Item("Left Lamp", 284, 73, 40, 88, new Image(Main.class.getResource("furniture/level 4.png").toString()), 0, 0, 40, 88);
    private Item rightChair = new Item("Right Chair", 772, 102, 60, 77, new Image(Main.class.getResource("furniture/level 10.png").toString()), 0, 0, 60, 77);
    private Item rightCabinet = new Item("Right Cabinet", 676, 79, 89, 100, new Image(Main.class.getResource("furniture/level 7.png").toString()), 0, 0, 89, 100);
    private Item rightSmallPlant = new Item("Right Small Plant", 628, 125, 32, 43, new Image(Main.class.getResource("furniture/level 11.png").toString()), 0, 0, 32, 43);
    private Item rightBedTable = new Item("Right Bed Table", 540, 114, 44, 58, new Image(Main.class.getResource("furniture/level 5.png").toString()), 0, 0, 44, 58);
    private Item leftPainting = new Item("Left Painting", 181, 37, 44, 51, new Image(Main.class.getResource("furniture/level 15.png").toString()), 0, 0, 44, 51);
    private Item leftShelf = new Item("Left Shelf", 239, 36, 38, 48, new Image(Main.class.getResource("furniture/level 8.png").toString()), 0, 0, 38, 48);
    private Item leftWindow = new Item("Left Window", 75, 32, 55, 55, new Image(Main.class.getResource("furniture/window.png").toString()), 0, 0, 55, 55);
    private Item rightShelf = new Item("Right Shelf", 617, 39, 37, 48, new Image(Main.class.getResource("furniture/level 9.png").toString()), 0, 0, 37, 48);
    private Item rightWindow = new Item("Right Window", 776, 37, 55, 55, new Image(Main.class.getResource("furniture/window.png").toString()), 0, 0, 55, 55);
    private Item leftTable = new Item("Left Table", 32, 290, 206, 130, new Image(Main.class.getResource("furniture/level 12.png").toString()), 0, 0, 206, 130);
    private Item rightTable = new Item("Right Table", 627, 273, 206, 147, new Image(Main.class.getResource("furniture/level 3.png").toString()), 0, 0, 206, 147);
    private Item carpet = new Item("Carpet", 255, 191, 360, 232, new Image(Main.class.getResource("furniture/level 1.png").toString()), 0, 0, 360, 232);
    private ArrayList<Item> levelFurnitures = new ArrayList<>(List.of(
            carpet, rightTable, leftLamp, rightBedTable, rightCabinet,
            leftShelf, rightShelf, rightChair, rightSmallPlant,
            leftTable, leftSmallPlant, tallPlant, leftPainting
            ));
    private Image roomFrame = new Image(Main.class.getResource("furniture/room frame.png").toString());
    private int roomLevel;

    public PersonalRoom(double width, double height, String roomId, int roomLevel) {
        super(width, height, roomId, new Image(Main.class.getResource("room.png").toString()));
        this.roomLevel = roomLevel;
        this.clickableObjects.add(this.leftCabinet);
        this.clickableObjects.add(this.bed);
        this.carpet.setObstacle(false);
    }

    @Override
    public void drawRoom(Player player) {
        ArrayList<DrawableEntity> entities = new ArrayList<>(this.dropBoxes);
        entities.addAll(this.players);
        entities.add(player);
        entities.add(this.wall);
        entities.add(this.bed);
        entities.add(this.leftCabinet);
        entities.add(this.leftWindow);
        entities.add(this.rightWindow);

        for (int i = 1; i < this.roomLevel; i++) {
            entities.add(this.levelFurnitures.get(i));
        }

        this.getGraphicsContext2D().drawImage(this.bgImg, 0, 0, this.getWidth(), this.getHeight());
        this.carpet.draw(this.getGraphicsContext2D());

        entities.sort(Comparator.comparingDouble(DrawableEntity::getTop));

        for (DrawableEntity entity : entities) {
            entity.draw(this.getGraphicsContext2D());
        }

        this.getGraphicsContext2D().drawImage(this.roomFrame, 0, 0, this.getWidth(), this.getHeight());
    }

    @Override
    public void mouseClickHandler(MouseEvent mouse) {
        super.mouseClickHandler(mouse);
        if (this.leftCabinet.isClicked(mouse)) {
            this.fireEvent(new OverlayEvent(Game.DISPLAYING_OVERLAY, new CosmeticSelection()));
        }else if (this.bed.isClicked(mouse)) {
            this.fireEvent(new OverlayEvent(Game.DISPLAYING_OVERLAY, new LevelUpOverlay(this.roomLevel)));
        }
    }

    // Code collision handling for exiting room
    @Override
    public void collisionHandler(Player player) {
        super.collisionHandler(player);
        this.leftCabinet.isHit(player);
        this.wall.isHit(player);
        this.bed.isHit(player);

        for (int i = 1; i < this.roomLevel; i++) {
            this.levelFurnitures.get(i).isHit(player);
        }

        if (this.houseExit.isHit(player)) {
            this.fireEvent(new RoomEvent(Game.ROOM_TRANSITION, Main.getUser().getUsername() + "-r"));
        }
    }

    public int getRoomLevel() {
        return this.roomLevel;
    }

    public void setRoomLevel(int roomLevel) {
        this.roomLevel = roomLevel;
    }
}
