package Project.Controllers;

import Project.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class appController {
    public Button startButton;

    @FXML
    protected void onStartButtonClick() throws Exception {
        Application.setRoot("main-view.fxml");
    }
}
