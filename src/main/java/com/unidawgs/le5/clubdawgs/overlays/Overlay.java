package com.unidawgs.le5.clubdawgs.overlays;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public interface Overlay {

    void draw(GraphicsContext gc);

    void mouseClickHandler(MouseEvent mouse);

    void mouseMoveHandler(MouseEvent mouse);

    void scrollHandler(ScrollEvent scroll);
}
