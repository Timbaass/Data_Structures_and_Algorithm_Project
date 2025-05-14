package Project.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.application.Platform;
import javafx.scene.layout.VBox;
import Project.Models.Call;
import Project.Models.Operator;
import Project.Enums.Priority;
import java.util.*;

public class mainController {

    @FXML private TextField callerNameField;
    @FXML private ComboBox<String> priorityBox;
    @FXML private TextField durationField;
    @FXML private Button addCallButton;

    @FXML private TextField operatorNameField;
    @FXML private Button addOperatorButton;
    @FXML private Button startSimulationButton;

    @FXML private ListView<Call> callQueueView;
    @FXML private ListView<Operator> operatorListView;

    @FXML private VBox visualArea;

    private Queue<Call> callQueue = new LinkedList<>();
    private List<Operator> operatorList = new ArrayList<>();

    @FXML
    public void initialize() {
        priorityBox.getItems().addAll("HIGH", "MEDIUM", "LOW");

        addCallButton.setOnAction(e -> addCall());
        addOperatorButton.setOnAction(e -> addOperator());
        startSimulationButton.setOnAction(e -> startSimulation());
    }

    private void addCall() {
        String name = callerNameField.getText();
        String priorityStr = priorityBox.getValue();
        String durationStr = durationField.getText();

        if (name.isEmpty() || priorityStr == null || durationStr.isEmpty()) {
            showAlert("Eksik bilgi", "Lütfen tüm alanları doldurun.");
            return;
        }

        try {
            int duration = Integer.parseInt(durationStr);
            Priority priority = Priority.valueOf(priorityStr);
            Call call = new Call(name, priority, duration);
            callQueue.add(call);
            callQueueView.getItems().add(call);

            callerNameField.clear();
            durationField.clear();
        } catch (NumberFormatException ex) {
            showAlert("Hatalı süre", "Süre sayısal bir değer olmalıdır.");
        }
    }

    private void addOperator() {
        String name = operatorNameField.getText();
        if (name.isEmpty()) {
            showAlert("Eksik bilgi", "Operatör adı boş olamaz.");
            return;
        }

        Operator operator = new Operator(name);
        operatorList.add(operator);
        operatorListView.getItems().add(operator);
        operatorNameField.clear();
    }

    private void startSimulation() {
        new Thread(() -> {
            while (!callQueue.isEmpty()) {
                for (Operator operator : operatorList) {
                    if (!operator.isBusy() && !callQueue.isEmpty()) {
                        Call call = callQueue.poll();
                        operator.assignCall(call);

                        Platform.runLater(() -> {
                            callQueueView.getItems().remove(call);
                            operatorListView.refresh();

                            Label label = new Label(operator.getName() + " → " + call.toString());
                            visualArea.getChildren().add(label);
                        });

                        try {
                            Thread.sleep(call.getDurationInSeconds() * 1000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        operator.finishCall();
                        Platform.runLater(() -> operatorListView.refresh());
                    }
                }
            }
        }).start();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
