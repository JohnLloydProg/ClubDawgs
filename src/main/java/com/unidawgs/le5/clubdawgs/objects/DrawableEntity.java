package com.unidawgs.le5.clubdawgs.objects;

import javafx.scene.canvas.GraphicsContext;

public interface DrawableEntity {
    void draw(GraphicsContext gc);

    double getTop();
}
