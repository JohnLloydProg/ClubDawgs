package com.unidawgs.le5.clubdawgs.overlays;

import com.unidawgs.le5.clubdawgs.Firebase;
import com.unidawgs.le5.clubdawgs.Game;
import com.unidawgs.le5.clubdawgs.Main;
import com.unidawgs.le5.clubdawgs.Settings;
import com.unidawgs.le5.clubdawgs.objects.TextBtn;
import com.unidawgs.le5.clubdawgs.objects.User;
import com.unidawgs.le5.clubdawgs.rooms.Room;
import javafx.event.Event;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Random;

public class Gacha implements Overlay{
    private TextBtn closeBtn = new TextBtn(800, 20, 50, 50, "X", Color.WHITE, 20, Color.RED);
    private TextBtn pullBtn = new TextBtn(385, 550, 100, 50, "160", Color.WHITE, 20, Color.GREEN);
    private boolean pulling = false;
    private int animationCounter = 0;
    private ArrayList<Image> cosmetics = new ArrayList<>();
    private ColorAdjust darken = new ColorAdjust();
    private int wonCosmetic;
    private ArrayList<Integer> ownedCosmetics;

    public Gacha() {
        for (int i = 1; i < 35; i++) {
            cosmetics.add(new Image(Main.class.getResource("cosmetics/sprite accessories-"+ i +".png").toString()));
        }
        this.darken.setBrightness(-1);
        this.ownedCosmetics = Firebase.getCosmetics(Main.getUser().getLocalId(), Main.getUser().getIdToken());
        if (this.ownedCosmetics.size() == 34) {
            this.pullBtn.setClickable(false);
        }
    }

    @Override
    public void mouseClickHandler(MouseEvent mouse) {
        User user = Main.getUser();
        if (this.closeBtn.isClicked(mouse)) {
            ((Room) mouse.getSource()).fireEvent(new Event(Game.HIDE_OVERLAY));
        }else if (this.pullBtn.isClicked(mouse)) {
            if (user.getCurrency() >= 160 && Firebase.getCosmetics(user.getLocalId(), user.getIdToken()).size() < 35) {
                user.setCurrency(Firebase.changeCurrency(user.getLocalId(), user.getIdToken(), -160));
                do {
                    this.wonCosmetic = new Random().nextInt(1, this.cosmetics.size());
                } while (this.ownedCosmetics.contains(this.wonCosmetic));
                Firebase.addCosmetic(user.getLocalId(), user.getIdToken(), this.wonCosmetic);
                this.animationCounter = 0;
                this.pulling = true;
            }
        }
    }

    @Override
    public void mouseMoveHandler(MouseEvent mouse) {
        closeBtn.enters(mouse);
        closeBtn.exits(mouse);
        pullBtn.enters(mouse);
        pullBtn.exits(mouse);
    }

    @Override
    public void scrollHandler(ScrollEvent scroll) {
        return;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillRect(0, 0, Settings.gameWidth, Settings.gameHeight);

        gc.setFill(Color.WHEAT);
        gc.fillRect(95, 210, 680, 200);

        gc.setFill(Color.GRAY);
        for (int i = 0; i < 3; i++) {
            gc.fillRoundRect(115 + (i * 220), 210, 200, 200, 20, 20);
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < this.cosmetics.size(); j++) {
                gc.save();
                if (this.pulling || this.animationCounter == 0) {
                    gc.setEffect(darken);
                }
                gc.drawImage(this.cosmetics.get(j), 115 + (i * 220), 210 + (j * 210) - (this.animationCounter % ((this.cosmetics.size() - 1) * 210)), 200, 200);
                gc.restore();
            }
        }
        if (this.pulling) {
            this.animationCounter += 30;
        }
        if (this.animationCounter >= (210 * (this.cosmetics.size() + this.wonCosmetic - 2))) {
            this.pulling = false;
        }

        gc.setFill(Color.WHEAT);
        gc.fillRect(95, 0, 680, 210);
        gc.fillRect(95, 410, 680, 210);

        closeBtn.draw(gc);
        pullBtn.draw(gc);
    }
}
