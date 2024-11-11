package com.unidawgs.le5.clubdawgs.objects;

import com.unidawgs.le5.clubdawgs.Game;
import com.unidawgs.le5.clubdawgs.rooms.Room;
import javafx.event.Event;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class Item implements DrawableEntity, ClickableObject {
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
    protected boolean clickable = true;
    private boolean inside = false;

    public Item(String itemName, double xPos, double yPos, double width, double height) {
        this.itemName = itemName;
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
    }

    public Item(String itemName, double xPos, double yPos, double width, double height, Image image, double xOffset, double yOffset, double imgWidth, double imgHeight) {
        this.itemName = itemName;
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
        this.image = image;
        this.xImg = xPos + xOffset;
        this.yImg = yPos + yOffset;
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
    }

    public void setObstacle(boolean obstacle) {
        this.isObstacle = obstacle;
    }

    public boolean enters(MouseEvent mouse) {
        if (!this.inside && this.clickable) {
            if (this.xImg <= mouse.getX() && mouse.getX() <= this.xImg + this.imgWidth) {
                if (this.yImg <= mouse.getY() && mouse.getY() <= this.yImg + this.imgHeight) {
                    ((Room) mouse.getSource()).fireEvent(new Event(Game.MOUSE_ENTER));
                    this.inside = true;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean exits(MouseEvent mouse) {
        if (this.inside && this.clickable) {
            if (mouse.getX() < this.xImg || mouse.getX() > this.xImg + this.imgWidth || mouse.getY() < this.yImg || mouse.getY() > this.yImg + this.imgHeight) {
                ((Room) mouse.getSource()).fireEvent(new Event(Game.MOUSE_EXIT));
                this.inside = false;
                return true;
            }
        }
        return false;
    }

    public boolean isClicked(MouseEvent mouse) {
        if (mouse.isConsumed()) {
            return false;
        }
        this.enters(mouse);
        this.exits(mouse);
        if (mouse.getButton() == MouseButton.PRIMARY && this.inside) {
            mouse.consume();
            return true;
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

    public void draw(GraphicsContext gc) {
        gc.drawImage(this.image, this.xImg, this.yImg, this.imgWidth, this.imgHeight);
    }
}
