module org.example.taskmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    opens org.example.taskmanager.app to javafx.graphics;
    exports org.example.taskmanager.ui to javafx.graphics;
    opens org.example.taskmanager.tasks to com.fasterxml.jackson.databind;
    opens org.example.taskmanager to javafx.fxml;
    opens org.example.taskmanager.data to com.fasterxml.jackson.databind;
}