package com.unidawgs.le5.clubdawgs;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class BtnParent {
    protected double xPos;
    protected double yPos;
    protected final double width;
    protected final double height;

    public BtnParent(double xPos, double yPos, double width, double height) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
    }

    public boolean isClicked(MouseEvent e) {
        if (this.xPos <= e.getX() && e.getX() <= this.xPos + this.width) {
            if (this.yPos <= e.getY() && e.getY() <= this.yPos + this.height) {
                return e.getButton() == MouseButton.PRIMARY;
            }
        }
        return false;
    }
}
