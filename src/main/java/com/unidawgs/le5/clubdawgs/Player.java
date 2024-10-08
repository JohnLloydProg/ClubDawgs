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

    // Images for different directions
    private Image leftImage;
    private Image rightImage;

    public Player(int xPos, int yPos, String userName) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.userName = userName;

        //this.leftImage = new Image("path/to/left_image.png");
        //this.rightImage = new Image("path/to/right_image.png");
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(Color.rgb(255, 0, 0));
        gc.fillRect(this.xPos, this.yPos, 50, 50);

        gc.setFill(Color.rgb(0, 0, 0));
        gc.fillText(this.userName, this.xPos, this.yPos - 10, 15);
    }

    //public void draw(GraphicsContext gc) {
    //    Image currentImage;
    //
    //    if (xDir > 0) {
    //        currentImage = rightImage;
    //    } else {
    //        currentImage = leftImage;
    //    }
    //
    //    gc.drawImage(currentImage, xPos, yPos);
    //}

    public void move() {
        if (this.directions[0] && yPos > 0) {
            yPos -= speed;
        }

        if (this.directions[1] && xPos + 50 < Settings.gameWidth) {
            xPos += speed;
        }

        if (this.directions[2] && yPos + 50 < Settings.gameHeight) {
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
    }

    public void setSouth(boolean south) {
        this.directions[2] = south;
    }

    public void setWest(boolean west) {
        this.directions[3] = west;
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
}
