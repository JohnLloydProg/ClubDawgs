package com.unidawgs.le5.clubdawgs.objects;

import com.google.gson.JsonObject;
import com.unidawgs.le5.clubdawgs.Firebase;
import com.unidawgs.le5.clubdawgs.Main;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

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

    public boolean rightClicked(MouseEvent mouse) {
        if (mouse.isConsumed()) {
            return false;
        }
        this.enters(mouse);
        this.exits(mouse);
        if (mouse.getButton() == MouseButton.SECONDARY && this.inside) {
            mouse.consume();
            return true;
        }
        return false;
    }

    public void delete(String roomId) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete File");
        alert.setContentText("Are you sure you want to delete " + this.fileName + "?");

        Optional<ButtonType> result = alert.showAndWait();

        // Check if the user confirmed the download
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Download the file from Firebase
            Firebase.removeDropBox(Main.getUser().getIdToken(), roomId, this);
        } else {
            // Handle the case where the user cancelled the download
            System.out.println("Delete cancelled");
            //create a dialogue box + if statement to confirm if there is download
            //firebase.getFile(user.getIdToken(), roomId, box.getFileName().replace("-", "."), box.getDownloadToken());
        }
    }

    public void interact(String roomId) {
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
            Firebase.getFile(Main.getUser().getIdToken(), roomId, this.fileName.replace("-", "."), this.getDownloadToken());
        } else {
            // Handle the case where the user cancelled the download
            System.out.println("Download cancelled");
            //create a dialogue box + if statement to confirm if there is download
            //firebase.getFile(user.getIdToken(), roomId, box.getFileName().replace("-", "."), box.getDownloadToken());
        }
    }

}
