package com.unidawgs.le5.clubdawgs;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Player {
    private int xPos, yPos;
    private int xDir, yDir;  // Direction of movement
    private boolean moving = false;
    private User user;

    // Images for different directions
    private Image upImage;
    private Image downImage;
    private Image leftImage;
    private Image rightImage;

    public Player(int xPos, int yPos, User user) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.user = user;

        this.upImage = new Image("path/to/up_image.png");
        this.downImage = new Image("path/to/down_image.png");
        this.leftImage = new Image("path/to/left_image.png");
        this.rightImage = new Image("path/to/right_image.png");
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public void draw(GraphicsContext gc) {
        Image currentImage;

        if (xDir > 0) {
            currentImage = rightImage;
        } else if (xDir < 0) {
            currentImage = leftImage;
        } else if (yDir > 0) {
            currentImage = downImage;
        } else if (yDir < 0) {
            currentImage = upImage;
        } else {
            currentImage = downImage;
        }

        gc.drawImage(currentImage, xPos, yPos);
    }

    public void move() {
        if (moving) {
            xPos += xDir;  // Update x position
            yPos += yDir;  // Update y position
        }
    }

    public void setDirection(int xDir, int yDir) {
        this.xDir = xDir;
        this.yDir = yDir;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public boolean isMoving() {
        return moving;
    }
}
