package com.unidawgs.le5.clubdawgs.overlays;

import com.unidawgs.le5.clubdawgs.Firebase;
import com.unidawgs.le5.clubdawgs.Game;
import com.unidawgs.le5.clubdawgs.Main;
import com.unidawgs.le5.clubdawgs.Settings;
import com.unidawgs.le5.clubdawgs.objects.ImgBtn;
import com.unidawgs.le5.clubdawgs.objects.TextBtn;
import com.unidawgs.le5.clubdawgs.objects.User;
import com.unidawgs.le5.clubdawgs.rooms.PersonalRoom;
import javafx.event.Event;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

public class LevelUpOverlay implements Overlay{
    private double xPos = 158;
    private double yPos = 51;
    private ImgBtn closeBtn = new ImgBtn(800, 20, 50, 50, new Image(Main.class.getResource("exitButton.png").toString()));
    private ImgBtn levelUpBtn = new ImgBtn(this.xPos + 143, this.yPos + 365, 268, 71, new Image(Main.class.getResource("level-btn.png").toString()));
    private Image bgImage = new Image(Main.class.getResource("level-bg2.png").toString());
    private Font levelFont = Font.loadFont(Main.class.getResource("ARCADE_N.ttf").toString(), 83);
    private ArrayList<Image> arrowSprites = new ArrayList<>(List.of(
            new Image(Main.class.getResource("level-arrow-1.png").toString()),
            new Image(Main.class.getResource("level-arrow-2.png").toString()),
            new Image(Main.class.getResource("level-arrow-3.png").toString()),
            new Image(Main.class.getResource("level-arrow-4.png").toString())
    ));
    private int animationCounter = 0;
    private int roomLevel;

    public LevelUpOverlay(int roomLevel){
        this.roomLevel = roomLevel;
    }

    @Override
    public void draw(GraphicsContext gc) {
        this.animationCounter++;
        if (this.animationCounter == (4 * 10)) {
            this.animationCounter = 0;
        }
        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillRect(0, 0, Settings.gameWidth, Settings.gameHeight);

        gc.setFill(Color.WHEAT);
        gc.fillRoundRect(235, 160, 400, 300, 20, 20);
        gc.drawImage(this.bgImage, this.xPos, this.yPos);
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(this.levelFont);
        gc.fillText(this.roomLevel + "", this.xPos + 116, this.yPos + 195);
        gc.fillText((this.roomLevel + 1) + "", this.xPos + 430, this.yPos + 195);
        gc.drawImage(this.arrowSprites.get(this.animationCounter/10), this.xPos + 207, this.yPos + 193);
        this.closeBtn.draw(gc);
        this.levelUpBtn.draw(gc);
    }

    @Override
    public void mouseClickHandler(MouseEvent mouse) {
        PersonalRoom room = (PersonalRoom) mouse.getSource();
        User user = Main.getUser();
        if (this.closeBtn.isClicked(mouse)) {
            room.fireEvent(new Event(Game.HIDE_OVERLAY));
        }else if (this.levelUpBtn.isClicked(mouse)) {
            int cost = 300 + (100 * room.getRoomLevel());
            if (user.getCurrency() >= cost && room.getRoomLevel() < 13) {
                user.setCurrency(Firebase.changeCurrency(user.getLocalId(), user.getIdToken(), cost * -1));
                Firebase.upgradeRoom(user.getLocalId(), user.getIdToken());
                room.setRoomLevel(room.getRoomLevel() + 1);
                this.roomLevel++;
            }
        }
    }

    @Override
    public void mouseMoveHandler(MouseEvent mouse) {
        this.closeBtn.enters(mouse);
        this.closeBtn.exits(mouse);
        this.levelUpBtn.enters(mouse);
        this.levelUpBtn.exits(mouse);
    }

    @Override
    public void scrollHandler(ScrollEvent scroll) {
        return;
    }
}
