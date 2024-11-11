package com.unidawgs.le5.clubdawgs.objects;

import javafx.scene.input.MouseEvent;

public interface ClickableObject {

    boolean isClicked(MouseEvent mouse);

    boolean enters(MouseEvent mouse);

    boolean exits(MouseEvent mouse);
}
