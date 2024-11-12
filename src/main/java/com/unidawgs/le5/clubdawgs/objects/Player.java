package com.unidawgs.le5.clubdawgs.objects;

import com.unidawgs.le5.clubdawgs.Main;
import com.unidawgs.le5.clubdawgs.Settings;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Arrays;

public class Player implements DrawableEntity {
    // Images for different directions
    private ArrayList<Image> leftSprite = new ArrayList<>(Arrays.asList(
            new Image(Main.class.getResource("dog-sprite-left-idle.png").toString()),
            new Image(Main.class.getResource("dog-sprite-left-walk1.png").toString()),
            new Image(Main.class.getResource("dog-sprite-left-walk2.png").toString())
    ));
    private ArrayList<Image> rightSprite = new ArrayList<>(Arrays.asList(
            new Image(Main.class.getResource("dog-sprite-right-idle.png").toString()),
            new Image(Main.class.getResource("dog-sprite-right-walk1.png").toString()),
            new Image(Main.class.getResource("dog-sprite-right-walk2.png").toString())
    ));
    private double xPos, yPos;
    private boolean[] directions = {false, false, false, false};
    private String userName;
    private String state = null;
    private double xSpeed = 0;
    private double ySpeed = 0;
    private double width = 70;
    private double height = 20;
    private Image curImage;
    private Image cosmeticImage;
    private int animationCounter = 0;
    private boolean userCharacter;
    private int cosmetic;

    public Player(double xPos, double yPos, String userName, int cosmetic) {
        this.userCharacter = true;
        this.xPos = xPos;
        this.yPos = yPos;
        this.userName = userName;
        this.state = "R";
        this.cosmetic = cosmetic;
        if (cosmetic != 0) {
            this.cosmeticImage = new Image(Main.class.getResource("cosmetics/sprite accessories-"+ cosmetic +".png").toString());
        }
    }

    public Player(double xPos, double yPos, String userName, int cosmetic, int animationCounter, String state) {
        this.userCharacter = false;
        this.xPos = xPos;
        this.yPos = yPos;
        this.userName = userName;
        this.animationCounter = animationCounter;
        this.state = state;
        this.cosmetic = cosmetic;
        if (cosmetic != 0) {
            this.cosmeticImage = new Image(Main.class.getResource("cosmetics/sprite accessories-"+ cosmetic +".png").toString());
        }
    }

    public void draw(GraphicsContext gc) {
        if (this.userCharacter) {
            this.animationCounter++;
            if (this.animationCounter == (7 * 3) || this.xSpeed == 0) {
                this.animationCounter = 0;
            }
        }
        if (this.state.contentEquals("R")) {
            this.curImage = this.rightSprite.get(this.animationCounter/7);
        }else {
            this.curImage = this.leftSprite.get(this.animationCounter/7);
        }
        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillOval(this.xPos + 5, this.yPos + 10, 60, 20);
        gc.drawImage(this.curImage, this.xPos, this.yPos - 50, this.width, this.height + 50);
        if (this.cosmetic != 0) {
            if (this.state.contentEquals("R")) {
                gc.save();
                gc.translate(this.xPos + 92, this.yPos - 68 - (this.animationCounter/14.0));
                gc.scale(-1, 1);
                gc.drawImage(this.cosmeticImage, 0, 0, 115, 115);
                gc.restore();
            }else {
                gc.drawImage(this.cosmeticImage, this.xPos - 22, this.yPos - 68 - (this.animationCounter/14.0), 115, 115);
            }
        }
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.BLACK);
        gc.setFont(new Font(15));
        //gc.fillText(this.userName, this.xPos + (this.width / 2), this.yPos - 50);
    }

    public void changeCosmetic(int cosmetic) {
        this.cosmetic = cosmetic;
        if (cosmetic != 0) {
            this.cosmeticImage = new Image(Main.class.getResource("cosmetics/sprite accessories-"+ cosmetic +".png").toString());
        }
    }

    public void getMove() {
        if (this.directions[0] && yPos > 70) {
            ySpeed = -7;
        }

        if (this.directions[1] && xPos + this.width < Settings.gameWidth - 15) {
            this.state = "R";
            xSpeed = 7;
        }

        if (this.directions[2] && yPos + this.height < Settings.gameHeight - 15) {
            ySpeed = 7;
        }

        if (this.directions[3] && xPos > 15) {
            this.state = "L";
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

    public String getUserName() {
        return this.userName;
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

    public String getState() {
        return this.state;
    }

    public int getAnimationCounter() {
        return this.animationCounter;
    }

    public int getCosmetic() {
        return this.cosmetic;
    }
}
