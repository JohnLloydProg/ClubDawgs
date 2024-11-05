package com.unidawgs.le5.clubdawgs;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public abstract class Item implements DrawableEntity {
    protected double xPos;
    protected double yPos;
    protected double width;
    protected double height;
    protected String itemName;
    protected boolean isObstacle = true;
    protected Image image;
    protected double xImg;
    protected double yImg;
    protected double imgWidth;
    protected double imgHeight;

    public Item(String itemName, double xPos, double yPos, double width, double height) {
        this.itemName = itemName;
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
    }

    public boolean clicked(MouseEvent mouse) {
        if (this.xImg <= mouse.getX() && mouse.getX() <= this.xImg + this.imgWidth) {
            if (this.yImg <= mouse.getY() && mouse.getY() <= this.yImg + this.imgHeight) {
                return mouse.getButton() == MouseButton.PRIMARY;
            }
        }
        return false;
    }

    public boolean isHit(Player player) {
        return this.isHitLeft(player) || this.isHitRight(player) || this.isHitBottom(player) || this.isHitTop(player);
    }

    public boolean isHitLeft(Player player) {
        if (player.getRight() + player.getXSpeed() >= this.getLeft() && player.getLeft() + player.getXSpeed() <= this.getRight()) {
            if (player.getBottom() >= this.getTop() && player.getTop() <= this.getBottom()) {
                if (player.getXSpeed() > 0) {
                    if (this.isObstacle) {
                        player.setXSpeed(0);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isHitRight(Player player) {
        if (player.getRight() + player.getXSpeed() >= this.getLeft() && player.getLeft() + player.getXSpeed() <= this.getRight()) {
            if (player.getBottom() >= this.getTop() && player.getTop() <= this.getBottom()) {
                if (player.getXSpeed() < 0) {
                    if (this.isObstacle) {
                        player.setXSpeed(0);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isHitTop(Player player) {
        if (player.getRight() >= this.getLeft() && player.getLeft() <= this.getRight()) {
            if (player.getBottom() + player.getYSpeed() >= this.getTop() && player.getTop() + player.getYSpeed() <= this.getBottom()) {
                if (player.getYSpeed() > 0) {
                    if (this.isObstacle) {
                        player.setYSpeed(0);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isHitBottom(Player player) {
        if (player.getRight() >= this.getLeft() && player.getLeft() <= this.getRight()) {
            if (player.getBottom() + player.getYSpeed() >= this.getTop() && player.getTop() + player.getYSpeed() <= this.getBottom()) {
                if (player.getYSpeed() < 0) {
                    if (this.isObstacle) {
                        player.setYSpeed(0);
                    }
                    return true;
                }
            }
        }
        return false;
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

    public abstract void draw(GraphicsContext gc);
}
