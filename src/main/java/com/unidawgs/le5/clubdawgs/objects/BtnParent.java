package com.unidawgs.le5.clubdawgs.objects;

import com.unidawgs.le5.clubdawgs.Game;
import com.unidawgs.le5.clubdawgs.rooms.Room;
import javafx.event.Event;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class BtnParent implements ClickableObject {
    protected double xPos;
    protected double yPos;
    protected final double width;
    protected final double height;
    protected boolean inside = false;
    protected boolean clickable = true;

    public BtnParent(double xPos, double yPos, double width, double height) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    public boolean enters(MouseEvent mouse) {
        if (!this.inside && this.clickable) {
            if (this.xPos <= mouse.getX() && mouse.getX() <= this.xPos + this.width) {
                if (this.yPos <= mouse.getY() && mouse.getY() <= this.yPos + this.height) {
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
            if (mouse.getX() < this.xPos || mouse.getX() > this.xPos + this.width || mouse.getY() < this.yPos || mouse.getY() > this.yPos + this.height) {
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
}
