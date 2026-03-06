module com.example.ui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.ui to javafx.fxml;
    exports com.ui;
    exports com.core;
    opens com.core to javafx.fxml;
}