package com.unidawgs.le5.clubdawgs.overlays;

import com.unidawgs.le5.clubdawgs.Firebase;
import com.unidawgs.le5.clubdawgs.Game;
import com.unidawgs.le5.clubdawgs.Main;
import com.unidawgs.le5.clubdawgs.Settings;
import com.unidawgs.le5.clubdawgs.objects.TextBtn;
import com.unidawgs.le5.clubdawgs.objects.User;
import com.unidawgs.le5.clubdawgs.rooms.PersonalRoom;
import javafx.event.Event;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class LevelUpOverlay implements Overlay{
    private TextBtn closeBtn = new TextBtn(800, 20, 50, 50, "X", Color.WHITE, 20, Color.RED);
    private TextBtn levelUpBtn = new TextBtn(385, 390, 100, 50, "Level Up", Color.WHITE, 20, Color.GREEN);
    private Font levelFont = new Font(40);
    private int roomLevel;

    public LevelUpOverlay(int roomLevel){
        this.roomLevel = roomLevel;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillRect(0, 0, Settings.gameWidth, Settings.gameHeight);

        gc.setFill(Color.WHEAT);
        gc.fillRoundRect(235, 160, 400, 300, 20, 20);
        gc.setFill(Color.BLACK);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(this.levelFont);
        gc.fillText(this.roomLevel + "", 310, 270, 30);
        gc.fillText((this.roomLevel + 1) + "", 545, 270, 30);
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
