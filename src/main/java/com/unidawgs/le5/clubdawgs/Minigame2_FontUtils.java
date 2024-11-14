package com.unidawgs.le5.clubdawgs;

import javafx.scene.text.Font;

public class Minigame2_FontUtils {
    private static Font retroGamingFont;

    public static Font getRetroGamingFont(double size) {
        if (retroGamingFont == null) {
            retroGamingFont = Font.loadFont(Minigame2_FontUtils.class.getResource("RetroGaming.ttf").toExternalForm(), size);
        }
        return Font.font(retroGamingFont.getFamily(), size);
    }
}
