module com.palacio.webservicegui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires com.google.gson;

    opens com.palacio.webservicegui to javafx.fxml;
    exports com.palacio.webservicegui;
}
