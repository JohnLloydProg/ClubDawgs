package com.unidawgs.le5.clubdawgs.objects;


import com.unidawgs.le5.clubdawgs.Firebase;
import com.unidawgs.le5.clubdawgs.Main;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class Request {
    private final double xPos = 310;
    private final double yPos = 20;
    private final double width = 336;
    private final double height = 120;
    private final String requestLocalId;
    private final String idToken;
    private final String roomId;
    private final String userName;
    private int counter = 100;
    private Image bgImage = new Image(Main.class.getResource("request-bg2.png").toString());
    private final ImgBtn acceptBtn = new ImgBtn(this.xPos + 52, this.yPos + 66, 100, 35, new Image(Main.class.getResource("request-accept.png").toString()));
    private final ImgBtn rejectBtn = new ImgBtn(this.xPos + 199, this.yPos + 66, 100, 35, new Image(Main.class.getResource("request-reject.png").toString()));
    private Font textFont = Font.loadFont(Main.class.getResource("ARCADE_N.ttf").toString(), 20);


    public Request(String requestLocalId, String idToken, String roomId) {
        this.requestLocalId = requestLocalId;
        this.idToken = idToken;
        this.roomId = roomId;
        this.userName = Firebase.getUsername(requestLocalId, idToken);
    }

    public void eventHandler(MouseEvent e) {
        if (this.acceptBtn.isClicked(e)) {
            Firebase.acceptRequest(this.idToken, this.roomId, this.requestLocalId);
            e.consume();
        }else if (this.rejectBtn.isClicked(e)) {
            Firebase.rejectRequest(this.idToken, this.roomId, this.requestLocalId);
            e.consume();
        }
    }

    public void draw(GraphicsContext gc) {
        gc.drawImage(this.bgImage, this.xPos, this.yPos);
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.TOP);
        gc.setFont(this.textFont);
        gc.fillText(this.userName, this.xPos + 53, this.yPos + 27, this.width - 20);
        this.acceptBtn.draw(gc);
        this.rejectBtn.draw(gc);
    }

}
