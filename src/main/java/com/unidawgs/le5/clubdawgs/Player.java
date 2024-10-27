package com.unidawgs.le5.clubdawgs;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Player implements DrawableEntity {
    private double xPos, yPos;
    private boolean[] directions = {false, false, false, false};
    private String userName;
    private double xSpeed = 0;
    private double ySpeed = 0;
    private double width = 70;
    private double height = 20;

    // Images for different directions
    private Image leftImage;
    private Image rightImage;
    private Image curImage;

    public Player(double xPos, double yPos, String userName) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.userName = userName;
        this.leftImage = new Image(Main.class.getResource("dog-sprite-big-left.png").toString());
        this.rightImage = new Image(Main.class.getResource("dog-sprite-big-right.png").toString());
        this.curImage = this.rightImage;
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillOval(this.xPos + 5, this.yPos + 10, 60, 20);
        gc.drawImage(this.curImage, this.xPos, this.yPos - 50, this.width, this.height + 50);
    }

    public void getMove() {
        if (this.directions[0] && yPos > 0) {
            ySpeed = -7;
        }

        if (this.directions[1] && xPos + this.width < Settings.gameWidth) {
            xSpeed = 7;
        }

        if (this.directions[2] && yPos + this.height < Settings.gameHeight) {
            ySpeed = 7;
        }

        if (this.directions[3] && xPos > 0) {
            xSpeed = -7;
        }
    }

    public void move() {
        this.xPos += this.xSpeed;
        this.yPos += this.ySpeed;
        if (this.xSpeed > 0) {
            this.xSpeed--;
        }else if (this.xSpeed < 0) {
            this.xSpeed++;
        }
        if (this.ySpeed > 0) {
            this.ySpeed--;
        }else if (this.ySpeed < 0) {
            this.ySpeed++;
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

    public double getLeft() {
        return this.xPos;
    }

    public double getRight() {
        return this.xPos + this.width;
    }

    public double getTop() {
        return this.yPos;
    }

    public double getBottom() {
        return this.yPos + this.height;
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }

    public void setXSpeed(double xSpeed) {
        this.xSpeed = xSpeed;
    }

    public void setYSpeed(double ySpeed) {
        this.ySpeed = ySpeed;
    }

    public double getXSpeed() {
        return this.xSpeed;
    }

    public double getYSpeed() {
        return this.ySpeed;
    }
}
