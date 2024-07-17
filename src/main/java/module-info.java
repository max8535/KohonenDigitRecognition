module com.example.diplom {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires javafx.swing;
    requires com.google.gson;
    opens com.example.diplom to com.google.gson, javafx.fxml;


    exports com.example.diplom;
}