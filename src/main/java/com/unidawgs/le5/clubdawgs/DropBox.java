package com.unidawgs.le5.clubdawgs;

import com.google.gson.JsonObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;

import java.util.Optional;


public class DropBox extends Item{
    private String downloadToken;
    private String fileName;


    public DropBox(String itemName, String fileName, String downloadToken, double xPos, double yPos) {
        super(itemName, xPos, yPos, 50, 25);
        this.fileName = fileName;
        this.downloadToken = downloadToken;
        this.image = new Image(Main.class.getResource("item.png").toString());
        this.imgWidth = 50;
        this.imgHeight = 50;
    }

    public JsonObject getJson() {
        JsonObject details = new JsonObject();
        details.addProperty("downloadToken", this.downloadToken);
        details.addProperty("xPos", this.xPos);
        details.addProperty("yPos", this.yPos);
        return details;
    }

    public String getFileName() {
        return this.fileName.replace(".", "-");
    }

    public String getDownloadToken() {
        return this.downloadToken;
    }

    @Override
    public void draw(GraphicsContext gc) {
        this.xImg = this.xPos;
        this.yImg = this.yPos - 25;
        gc.drawImage(this.image, this.xImg, this.yImg, this.width, this.height + 25);
    }

    public void interact(User user, String roomId) {
        System.out.println("Clicked:" + this.fileName);
        // Create a confirmation dialogue box
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Download");
        alert.setHeaderText("Download File");
        alert.setContentText("Are you sure you want to download " + this.fileName + "?");

        // Show the dialogue box and wait for the user's response
        Optional<ButtonType> result = alert.showAndWait();

        // Check if the user confirmed the download
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Download the file from Firebase
            Firebase.getFile(user.getIdToken(), roomId, this.fileName.replace("-", "."), this.getDownloadToken());
        } else {
            // Handle the case where the user cancelled the download
            System.out.println("Download cancelled");
            //create a dialogue box + if statement to confirm if there is download
            //firebase.getFile(user.getIdToken(), roomId, box.getFileName().replace("-", "."), box.getDownloadToken());
        }
    }

}
