package com.unidawgs.le5.clubdawgs;

import javafx.scene.canvas.GraphicsContext;

public class Item {

    protected String itemName;

    public Item(String itemName) {
        this.itemName = itemName;
    }

    public void draw(GraphicsContext gc){
        //not sure pa dito
    }

    public void interact(){

        System.out.println("Item" + itemName + "interacted with. ");
    }

}
