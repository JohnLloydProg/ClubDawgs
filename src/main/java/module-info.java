module com.unidawgs.le5.clubdawgs {
    requires javafx.controls;
    requires javafx.fxml;
    requires firebase.admin;
    requires jdk.compiler;
    requires com.google.auth.oauth2;
    requires java.net.http;
    requires com.google.api.client;
    requires com.google.gson;
    requires org.checkerframework.checker.qual;
    requires java.desktop;
    requires javafx.media;
    requires javafx.graphics;
    requires proto.google.common.protos;
   requires javafx.base;


    opens com.unidawgs.le5.clubdawgs to javafx.fxml;
    exports com.unidawgs.le5.clubdawgs;
    exports com.unidawgs.le5.clubdawgs.events;
    opens com.unidawgs.le5.clubdawgs.events to javafx.fxml;
    exports com.unidawgs.le5.clubdawgs.rooms;
    opens com.unidawgs.le5.clubdawgs.rooms to javafx.fxml;
    exports com.unidawgs.le5.clubdawgs.objects;
    opens com.unidawgs.le5.clubdawgs.objects to javafx.fxml;
    exports com.unidawgs.le5.clubdawgs.overlays;
    opens com.unidawgs.le5.clubdawgs.overlays to javafx.fxml;
}