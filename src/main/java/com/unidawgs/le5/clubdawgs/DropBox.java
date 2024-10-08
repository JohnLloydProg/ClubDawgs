package com.unidawgs.le5.clubdawgs;

import com.google.gson.JsonObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.awt.event.MouseEvent;


public class DropBox extends Item{
    private String downloadToken;
    private String fileName;
    private Image image;
    private int xPos;
    private int yPos;

    public DropBox(String itemName, String fileName, String downloadToken, int xPos, int yPos) {
        super(itemName);
        this.fileName = fileName;
        this.downloadToken = downloadToken;
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public JsonObject getJson() {
        JsonObject details = new JsonObject();
        details.addProperty("downloadToken", this.downloadToken);
        details.addProperty("xPos", this.xPos);
        details.addProperty("yPos", this.yPos);
        JsonObject data = new JsonObject();
        data.add(this.fileName, details);
        return data;
    }

    public boolean clicked(MouseEvent mouse) {
        //
        return false;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.rgb(0, 255, 0));
        gc.fillRect(this.xPos, this.yPos, 30, 30);
        //gc.drawImage(this.image, this.xPos, this.yPos);
    }


    public void interact(Firebase firebase, User user, String roomId) {
        firebase.getFile(user.getIdToken(), roomId, this.fileName, this.downloadToken);
    }

}
