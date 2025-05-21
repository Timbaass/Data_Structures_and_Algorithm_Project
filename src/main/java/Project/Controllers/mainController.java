package Project.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import Project.Models.Call;
import Project.Models.Operator;
import Project.Enums.Priority;
import java.util.*;
import Project.Scripts.myPriorityQueue;

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

    @FXML private GridPane visualGrid;

    private myPriorityQueue callQueue = new myPriorityQueue();
    private List<Operator> operatorList = new ArrayList<>();

    private static final int MAX_CALL_SLOTS = 10;
    private int operatorColumnIndex = 0;
    private Map<Operator, List<Label>> operatorCells = new HashMap<>();

    @FXML
    public void initialize() {
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
            callQueue.enqueue(call);
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

        // GridPane'e yeni sütun ekle
        int col = operatorColumnIndex++;
        Label header = new Label("👷 " + name);
        header.setStyle("-fx-font-weight: bold;");
        visualGrid.add(header, col, 0);

        List<Label> cells = new ArrayList<>();
        for (int row = 1; row <= MAX_CALL_SLOTS; row++) {
            Label cell = new Label();
            cell.setMinSize(100, 30);
            cell.setStyle("-fx-border-color: gray; -fx-background-color: white;");
            visualGrid.add(cell, col, row);
            cells.add(cell);
        }
        operatorCells.put(operator, cells);
    }

    private void startSimulation() {
        new Thread(() -> {
            while (!callQueue.isEmpty()) {
                for (Operator operator : operatorList) {
                    if (!operator.isBusy() && !callQueue.isEmpty()) {
                        Call call = callQueue.dequeue();
                        operator.assignCall(call);

                        Platform.runLater(() -> {
                            callQueueView.getItems().remove(call);
                            operatorListView.refresh();
                            updateOperatorCells(operator);
                        });

                        try {
                            Thread.sleep(call.getDurationInSeconds() * 1000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        operator.finishCall();
                        Platform.runLater(() -> {
                            updateOperatorCells(operator);
                            operatorListView.refresh();
                        });
                    }
                }
            }
        }).start();
    }

    private void updateOperatorCells(Operator operator) {
        List<Label> cells = operatorCells.get(operator);
        if (cells == null) return;

        if (operator.isBusy()) {
            cells.get(0).setText("📞 " + operator.getCurrentCall().getCallerName());
            cells.get(0).setStyle("-fx-background-color: tomato; -fx-border-color: black;");
        } else {
            cells.get(0).setText("");
            cells.get(0).setStyle("-fx-background-color: white; -fx-border-color: gray;");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
