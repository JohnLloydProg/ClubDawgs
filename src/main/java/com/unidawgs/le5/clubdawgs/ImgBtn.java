package com.unidawgs.le5.clubdawgs;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;


// Image button for inside the game screen. Not a JavaFX node/element
public class ImgBtn extends BtnParent{
    private final Image image;
    private Color backgroundColor = null;

    public ImgBtn(double xPos, double yPos, double width, double height, Image image) {
        super(xPos, yPos, width, height);
        this.image = image;
    }

    public ImgBtn(double xPos, double yPos, double width, double height, Image image, Color backgroundColor) {
        super(xPos, yPos, width, height);
        this.image = image;
        this.backgroundColor = backgroundColor;
    }

    public void draw(GraphicsContext gc) {
        if (this.backgroundColor != null) {
            gc.setFill(backgroundColor);
            gc.fillRoundRect(this.xPos, this.yPos, this.width, this.height, 15, 15);
        }
        gc.drawImage(this.image, this.xPos, this.yPos, this.width, this.height);
    }
}
