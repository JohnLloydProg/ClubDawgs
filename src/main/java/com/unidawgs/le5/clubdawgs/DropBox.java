package com.unidawgs.le5.clubdawgs;

import com.google.gson.JsonObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;


public class DropBox extends Item{
    private String downloadToken;
    private String fileName;
    private Image image = new Image(Main.class.getResource("item.png").toString());
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
        return details;
    }

    public boolean clicked(MouseEvent mouse) {
        if (this.xPos <= mouse.getX() && mouse.getX() <= this.xPos + 50) {
            if (this.yPos <= mouse.getY() && mouse.getY() <= this.yPos + 50) {
                return true;
            }
        }
        return false;
    }

    public String getFileName() {
        return this.fileName.replace(".", "-");
    }

    public String getDownloadToken() {
        return this.downloadToken;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.drawImage(this.image, this.xPos, this.yPos, 50, 50);
    }


    public void interact(Firebase firebase, User user, String roomId) {
        firebase.getFile(user.getIdToken(), roomId, this.fileName, this.downloadToken);
    }

}
