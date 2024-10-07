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


    opens com.unidawgs.le5.clubdawgs to javafx.fxml;
    exports com.unidawgs.le5.clubdawgs;
}