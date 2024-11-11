package com.unidawgs.le5.clubdawgs;

import com.unidawgs.le5.clubdawgs.objects.Player;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KeyEventHandler {

    public static void keyPressed(KeyEvent key, Player player) {
        if (key.getCode() == KeyCode.UP || key.getCode() == KeyCode.W) {
            player.setNorth(true);
        }
        if (key.getCode() == KeyCode.DOWN || key.getCode() == KeyCode.S) {
            player.setSouth(true);

        }
        if (key.getCode() == KeyCode.LEFT || key.getCode() == KeyCode.A) {
            player.setWest(true);
        }
        if (key.getCode() == KeyCode.RIGHT || key.getCode() == KeyCode.D) {
            player.setEast(true);
        }
    }

    public static void keyReleased(KeyEvent key, Player player) {
        if (key.getCode() == KeyCode.UP || key.getCode() == KeyCode.W) {
            player.setNorth(false);
        }
        if (key.getCode() == KeyCode.DOWN || key.getCode() == KeyCode.S) {
            player.setSouth(false);

        }
        if (key.getCode() == KeyCode.LEFT || key.getCode() == KeyCode.A) {
            player.setWest(false);
        }
        if (key.getCode() == KeyCode.RIGHT || key.getCode() == KeyCode.D) {
            player.setEast(false);
        }
    }
}
