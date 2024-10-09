package com.unidawgs.le5.clubdawgs;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Set;

public class Player {
    private int xPos, yPos;
    private boolean[] directions = {false, false, false, false};
    private String userName;
    private int speed = 5;
    private int width = 70;
    private int height = 70;

    // Images for different directions
    private Image leftImage;
    private Image rightImage;
    private Image curImage;

    public Player(int xPos, int yPos, String userName) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.userName = userName;
        this.leftImage = new Image(Main.class.getResource("dog-sprite-big-left.png").toString());
        this.rightImage = new Image(Main.class.getResource("dog-sprite-big-right.png").toString());
        this.curImage = this.rightImage;
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillOval(this.xPos + 5, this.yPos + 60, 60, 20);
        gc.drawImage(this.curImage, this.xPos, this.yPos, this.width, this.height);
    }

    public void move() {
        if (this.directions[0] && yPos > 0) {
            yPos -= speed;
        }

        if (this.directions[1] && xPos + this.width < Settings.gameWidth) {
            xPos += speed;
        }

        if (this.directions[2] && yPos + this.height < Settings.gameHeight) {
            yPos += speed;
        }

        if (this.directions[3] && xPos > 0) {
            xPos -= speed;
        }
    }

    public void setNorth(boolean north) {
        this.directions[0] = north;
    }

    public void setEast(boolean east) {
        this.directions[1] = east;
        this.curImage = this.rightImage;
    }

    public void setSouth(boolean south) {
        this.directions[2] = south;
    }

    public void setWest(boolean west) {
        this.directions[3] = west;
        this.curImage = this.leftImage;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public boolean isMoving() {
        for (boolean direction : directions) {
            if (direction) {
                return true;
            }
        }
        return false;
    }

    public void setPos(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }
}
