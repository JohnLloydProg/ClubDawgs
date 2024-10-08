package com.unidawgs.le5.clubdawgs;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    private Game game;
    private Firebase firebase;
    private User user;

    @Override
    public void start(Stage stage) {
        this.firebase = new Firebase();
        this.user = this.firebase.signIn("johnlloydunida0@gmail.com", "45378944663215");
        this.game = new Game(this.firebase, this.user);

        stage.setTitle("ClubDawgs");
        stage.setScene(this.game.getScene());

        stage.setOnHiding((event) -> {
            if (stage.getScene() == this.game.getScene()) {
                this.game.getMainLoop().stop();
                firebase.quitPlayer(user.getLocalId(), user.getIdToken(), this.game.getRoomId());
            }
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}