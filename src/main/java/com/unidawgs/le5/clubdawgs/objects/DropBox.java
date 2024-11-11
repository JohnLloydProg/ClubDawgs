package com.unidawgs.le5.clubdawgs.objects;

import com.google.gson.JsonObject;
import com.unidawgs.le5.clubdawgs.Firebase;
import com.unidawgs.le5.clubdawgs.Main;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;

import java.util.Optional;


public class DropBox extends Item {
    private String downloadToken;
    private String fileName;


    public DropBox(String itemName, String fileName, String downloadToken, double xPos, double yPos) {
        super(itemName, xPos, yPos, 50, 25, new Image(Main.class.getResource("item.png").toString()), 0, -25, 50, 50);
        this.fileName = fileName;
        this.downloadToken = downloadToken;
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
