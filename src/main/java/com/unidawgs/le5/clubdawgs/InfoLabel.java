package com.unidawgs.le5.clubdawgs;

import java.io.FileNotFoundException;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class InfoLabel extends Label{
    public InfoLabel(String text) {
        setPrefWidth(380); //Changed 600 -> 380
        setPrefHeight(49);
        // setPadding(new Insets(10, 40, 40, 50)); //Removed
        setText(text);
        setWrapText(true);
        setLabelFont();
        setAlignment(Pos.CENTER); //Changed /added

        // BackgroundImage bgImage = new BackgroundImage(new Image(getClass().getResource("mg-banner.png").toExternalForm(), 380, 50, false, true),
        //         BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, null);

        // setBackground(new Background(bgImage));

    }

    private void setLabelFont() {
        try {
            // Load the font using the URL directly
            Font font = Font.loadFont(getClass().getResourceAsStream("ARCADE_N.TTF"), 20);
            setTextFill(Color.WHITE);
            if (font != null) {
                setFont(font);
            } else {
                throw new FileNotFoundException("Font not found");
            }
        } catch (FileNotFoundException e) {
            setFont(Font.font("Verdana", 25));
            System.out.println("Font not found or could not be loaded. Using default \"Verdana\"");
        }
    }
}