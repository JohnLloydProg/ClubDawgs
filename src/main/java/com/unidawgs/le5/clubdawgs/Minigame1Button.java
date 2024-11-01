package com.unidawgs.le5.clubdawgs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Minigame1Button extends Button {

    private String BUTTON_PRESSED_STYLE = "-fx-background-color: transparent;-fx-background-image: url('com/unidawgs/le5/clubdawgs/rocketButton.png');";
    private String BUTTON_FREE_STYLE ="-fx-background-color: transparent;-fx-background-image: url('com/unidawgs/le5/clubdawgs/rocketButton.png');";

    public Minigame1Button(String text, String path) {
        BUTTON_PRESSED_STYLE = "-fx-background-color: transparent;-fx-background-image: url('"+path+"');-fx-background-repeat: no-repeat;";
        BUTTON_FREE_STYLE = "-fx-background-color: transparent;-fx-background-image: url('"+path+"');-fx-background-repeat: no-repeat;";
        setStyle(BUTTON_FREE_STYLE);
        setBackground(getBackground());
        setText(text);
        setButtonFont();
        //setPrefWidth(190);
        setPrefWidth(190);
        setPrefHeight(49);
        initialiseButtonListeners();
    }

    private void setButtonFont() {
        try {
            // Load the font using the URL directly
            Font font = Font.loadFont(getClass().getResourceAsStream("/path/to/VCR_OSD_MONO_1.001.ttf"), 25);
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

    private void setButtonPressedStyle() {
        setStyle(BUTTON_PRESSED_STYLE);
        setPrefHeight(49);
        setLayoutY(getLayoutY() + 4);

    }

    private void setButtonReleasedStyle() {
        setStyle(BUTTON_FREE_STYLE);
        setPrefHeight(49);
        setLayoutY(getLayoutY() - 4);
    }

    private void initialiseButtonListeners() {
        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    setButtonPressedStyle();
                }
            }
        });

        setOnMouseReleased(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    setButtonReleasedStyle();
                }
            }

        });
        setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setEffect(new DropShadow(50, Color.CYAN));
            }
        });
        setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setEffect(null);
            }
        });
    }
}