package com.unidawgs.le5.clubdawgs.objects;

import com.unidawgs.le5.clubdawgs.Main;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class CategoryBtn extends ImgBtn{
    private final String text;
    private final Color textColor;
    private final Font font = Font.loadFont(Main.class.getResource("ARCADE_N.ttf").toString(), 12);

    public CategoryBtn(double xPos, double yPos, double width, double height, String text, Color textColor) {
        super(xPos, yPos, width, height, new Image(Main.class.getResource("buttonContainer.png").toString()));
        this.text = text;
        this.textColor = textColor;
    }

    @Override
    public void draw(GraphicsContext gc) {
        super.draw(gc);
        gc.setFill(this.textColor);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(this.font);
        gc.fillText(this.text, this.xPos + (this.width / 2.0), this.yPos + (this.height / 2.0), this.width);
    }
}
