package com.unidawgs.le5.clubdawgs;


import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

// Text button for inside the game screen. Not a JavaFX node/element
public class TextBtn extends BtnParent {
    private final String text;
    private final Color textColor;
    private Color backgroundColor = null;
    private final Font textFont;

    public TextBtn(double xPos, double yPos, double width, double height, String text, Color textColor, int fontSize) {
        super(xPos, yPos, width, height);
        this.text = text;
        this.textColor = textColor;
        this.textFont = new Font(fontSize);
    }

    public TextBtn(double xPos, double yPos, double width, double height, String text, Color textColor, int fontSize, Color backgroundColor) {
        super(xPos, yPos, width, height);
        this.text = text;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.textFont = new Font(fontSize);
    }

    public void draw(GraphicsContext gc) {
        if (backgroundColor != null) {
            gc.setFill(this.backgroundColor);
            gc.fillRoundRect(this.xPos, this.yPos, this.width, this.height, 15, 15);
        }
        gc.setFill(this.textColor);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(this.textFont);
        gc.fillText(this.text, this.xPos + (this.width / 2.0), this.yPos + (this.height / 2.0), this.width);
    }

}
