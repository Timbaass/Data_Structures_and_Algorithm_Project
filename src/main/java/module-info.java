module Project {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens Project to javafx.fxml;
    exports Project;
    exports Project.Controllers;
    opens Project.Controllers to javafx.fxml;
}