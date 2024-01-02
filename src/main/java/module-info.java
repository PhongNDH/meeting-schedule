module com.example.calendlygui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires jbcrypt;
    requires kotlin.stdlib;

    opens com.calendlygui to javafx.fxml;
    exports com.calendlygui.controller;
    opens com.calendlygui.controller to javafx.fxml;
    exports com.calendlygui;
    exports com.calendlygui.controller.student;
    opens com.calendlygui.controller.student to javafx.fxml;
    exports com.calendlygui.controller.teacher;
    opens com.calendlygui.controller.teacher to javafx.fxml;
}