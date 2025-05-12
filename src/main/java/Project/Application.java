package Project;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Application extends javafx.application.Application {
    private static Scene scene;
    private static final String fxmlPath = "/Views/";

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath.concat("app-view.fxml")));
        Parent mainWindow = fxmlLoader.load();

        scene = new Scene(mainWindow, 900, 600);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxmlFile) throws Exception{
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource(fxmlPath.concat(fxmlFile)));
            Parent root = fxmlLoader.load();

            scene.setRoot(root);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        launch();
    }
}