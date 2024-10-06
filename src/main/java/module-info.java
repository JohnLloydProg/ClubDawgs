module com.unidawgs.le5.clubdawgs {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.unidawgs.le5.clubdawgs to javafx.fxml;
    exports com.unidawgs.le5.clubdawgs;
}