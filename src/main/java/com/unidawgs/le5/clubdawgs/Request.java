package com.unidawgs.le5.clubdawgs;


import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class Request {
    private final double xPos = 310;
    private final double yPos = 20;
    private final double width = 250;
    private final double height = 90;
    private final String requestLocalId;
    private final String idToken;
    private final String roomId;
    private final String userName;
    private int counter = 100;
    private final TextBtn acceptBtn = new TextBtn(this.xPos + 140, this.yPos + this.height - 40, 100, 30, "Accept", Color.WHITE, 17, Color.GREEN);
    private final TextBtn rejectBtn = new TextBtn(this.xPos + 10, this.yPos + this.height - 40, 100, 30, "Reject", Color.WHITE, 17, Color.RED);


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
        gc.setFill(Color.GRAY);
        gc.fillRoundRect(this.xPos, this.yPos, this.width, this.height, 15, 15);
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.TOP);
        gc.setFont(new Font(20));
        gc.fillText(this.userName, this.xPos + 15, this.yPos + 10, this.width - 20);
        this.acceptBtn.draw(gc);
        this.rejectBtn.draw(gc);
    }

}
