package com.unidawgs.le5.clubdawgs;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KeyEventHandler {

    public static void keyPressed(KeyEvent key, Player player) {
        if (key.getCode() == KeyCode.UP) {
            player.setNorth(true);
        }
        if (key.getCode() == KeyCode.DOWN) {
            player.setSouth(true);

        }
        if (key.getCode() == KeyCode.LEFT) {
            player.setWest(true);
        }
        if (key.getCode() == KeyCode.RIGHT) {
            player.setEast(true);
        }
    }

    public static void keyReleased(KeyEvent key, Player player) {
        if (key.getCode() == KeyCode.UP) {
            player.setNorth(false);
        }
        if (key.getCode() == KeyCode.DOWN) {
            player.setSouth(false);

        }
        if (key.getCode() == KeyCode.LEFT) {
            player.setWest(false);
        }
        if (key.getCode() == KeyCode.RIGHT) {
            player.setEast(false);
        }
    }
}
