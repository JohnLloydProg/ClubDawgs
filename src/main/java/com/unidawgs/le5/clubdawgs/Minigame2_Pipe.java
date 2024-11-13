package com.unidawgs.le5.clubdawgs;

import javafx.scene.image.Image;

public class Minigame2_Pipe {
    private int x, y;
    private final int width, height;
    private final Image img;
    private boolean passed = false;

    public Minigame2_Pipe(Image img, int x, int y, int width, int height) {
        this.img = img;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Image getImage() { return img; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }

    public void move(double velocityX) {
        x += velocityX;
    }
}
