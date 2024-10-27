package com.unidawgs.le5.clubdawgs;

import javafx.scene.canvas.GraphicsContext;

public class WalkSpace extends Item{

    public WalkSpace(double xPos, double yPos, double width, double height) {
        super("Walkspace", xPos, yPos, width, height);
        this.isObstacle = false;
    }

    @Override
    public void draw(GraphicsContext gc) {
        return;
    }
}
