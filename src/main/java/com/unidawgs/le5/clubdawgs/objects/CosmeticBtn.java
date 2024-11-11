package com.unidawgs.le5.clubdawgs.objects;

import com.unidawgs.le5.clubdawgs.Main;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;

public class CosmeticBtn extends ImgBtn {
    private boolean owned;
    private final int cosmetic;
    private final double origY;
    private final ArrayList<Image> leftSprite = new ArrayList<>(Arrays.asList(
            new Image(Main.class.getResource("dog-sprite-left-idle.png").toString()),
            new Image(Main.class.getResource("dog-sprite-left-walk1.png").toString()),
            new Image(Main.class.getResource("dog-sprite-left-walk2.png").toString())
    ));
    private int animationCounter = 0;
    private ColorAdjust darken = new ColorAdjust();

    public CosmeticBtn(int row, int col, Image image, int cosmetic, boolean owned) {
        super(170 + (col * 110), 290 + (row * 110), 90, 90, image, Color.GRAY);
        this.owned = owned;
        this.cosmetic = cosmetic;
        this.origY = 290 + (row * 110);
        this.darken.setBrightness(-1);
    }

    @Override
    public boolean enters(MouseEvent mouse) {
        if (this.yPos > 270) {
            return super.enters(mouse);
        }
        return false;
    }

    @Override
    public boolean isClicked(MouseEvent mouse) {
        if (this.owned) {
            return super.isClicked(mouse);
        }
        return false;
    }

    public int getCosmetic() {
        return this.cosmetic;
    }

    public Image getCosmeticImg() {
        return this.image;
    }

    public void draw(GraphicsContext gc, double offset) {
        this.yPos = this.origY + offset;
        if (this.inside) {
            this.animationCounter++;
            if (this.animationCounter == (7 * 3)) {
                this.animationCounter = 0;
            }
        }else {
            this.animationCounter = 0;
        }
        if (this.backgroundColor != null) {
            if (this.inside) {
                gc.setFill(Color.WHITE);
            }else {
                gc.setFill(Color.BLACK);
            }
            gc.fillRoundRect(this.xPos-3, this.yPos-3, this.width+6, this.height+6, 15, 15);
            gc.setFill(this.backgroundColor);
            gc.fillRoundRect(this.xPos, this.yPos, this.width, this.height, 15, 15);
        }
        if (!this.owned) {
            gc.save();
            gc.setEffect(this.darken);
        }
        gc.drawImage(this.leftSprite.get(this.animationCounter/7), this.xPos + 16, this.yPos + 13, this.width-31, this.height-31);
        gc.drawImage(this.image, this.xPos, this.yPos - (this.animationCounter/14.0), this.width, this.height);
        if (!this.owned) {
            gc.restore();
        }
    }

}
