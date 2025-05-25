package Project.Controllers;

import Project.Scripts.myHashMap;
import Project.Scripts.myAvlTree;
import Project.Scripts.myPriorityQueue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import Project.Models.Call;
import Project.Models.Operator;
import Project.Enums.Priority;
import java.util.*;

public class mainController {

    @FXML public TextField operatorNameField;
    @FXML private TextField callerNameField;
    @FXML private ComboBox<String> priorityBox;
    @FXML private TextField durationField;
    @FXML private Button addCallButton;
    @FXML private Button addOperatorButton;
    @FXML private Button startSimulationButton;
    @FXML private Button stopSimulationButton;
    @FXML private ListView<Call> callQueueView;
    @FXML private ListView<Operator> operatorListView;
    @FXML private GridPane visualGrid;

    private myPriorityQueue callQueue = new myPriorityQueue();
    private List<Operator> operatorList = new ArrayList<>();
    private myAvlTree workloadTree = new myAvlTree();
    private myHashMap<Operator, Integer> operatorWorkloads = new myHashMap<>();
    private myHashMap<Operator, List<Label>> operatorCells = new myHashMap<>();
    private myHashMap<Operator, List<Call>> operatorCallQueue = new myHashMap<>();
    private volatile boolean isSimulationRunning = false;
    private static final int MAX_CALL_SLOTS = 10;
    private int operatorColumnIndex = 0;
    private static final long ASSIGNMENT_DELAY = 2000; // 2 seconds delay between assignments

    @FXML
    public void initialize() {
        addCallButton.setOnAction(e -> addCall());
        addOperatorButton.setOnAction(e -> addOperator());
        startSimulationButton.setOnAction(e -> startSimulation());
        stopSimulationButton.setOnAction(e -> stopSimulation());
        prePopulateData();
    }

    private void prePopulateData() {
        String[] operatorNames = {"Operator1", "Operator2", "Operator3"};
        for (String name : operatorNames) {
            Operator operator = new Operator(name);
            operatorList.add(operator);
            Platform.runLater(() -> operatorListView.getItems().add(operator));
            operatorWorkloads.put(operator, 0);
            operatorCallQueue.put(operator, new ArrayList<>());
            workloadTree.insert(0);

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

        Random random = new Random();
        String[] callerPrefixes = {"Caller", "Customer", "User"};
        Priority[] priorities = Priority.values();
        for (int i = 1; i <= 30; i++) {
            String callerName = callerPrefixes[random.nextInt(callerPrefixes.length)] + i;
            Priority priority = priorities[random.nextInt(priorities.length)];
            int duration = random.nextInt(10) + 1;
            Call call = new Call(callerName, priority, duration);
            Operator leastLoadedOperator = findLeastLoadedOperator();
            if (leastLoadedOperator != null) {
                assignCallToOperator(leastLoadedOperator, call);
            } else {
                callQueue.enqueue(call);
                Platform.runLater(() -> callQueueView.getItems().add(call));
            }
        }
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
            if (isSimulationRunning && !operatorList.isEmpty()) {
                Operator leastLoadedOperator = findLeastLoadedOperator();
                if (leastLoadedOperator != null) {
                    assignCallToOperator(leastLoadedOperator, call);
                    // Do not add to callQueueView since it's assigned directly
                } else {
                    callQueue.enqueue(call);
                    Platform.runLater(() -> callQueueView.getItems().add(call));
                }
            } else {
                callQueue.enqueue(call);
                Platform.runLater(() -> callQueueView.getItems().add(call));
            }
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
        operatorWorkloads.put(operator, 0);
        operatorCallQueue.put(operator, new ArrayList<>());
        workloadTree.insert(0);
        operatorNameField.clear();

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
        if (isSimulationRunning) {
            showAlert("Simülasyon zaten çalışıyor", "Simülasyon zaten aktif.");
            return;
        }
        if (operatorList.isEmpty()) {
            showAlert("Operatör yok", "Lütfen en az bir operatör ekleyin.");
            return;
        }
        isSimulationRunning = true;
        startSimulationButton.setDisable(true);
        stopSimulationButton.setDisable(false);

        new Thread(() -> {
            while (isSimulationRunning && !callQueue.isEmpty()) {
                Operator leastLoadedOperator = findLeastLoadedOperator();
                if (leastLoadedOperator != null) {
                    Call call = callQueue.dequeue();
                    assignCallToOperator(leastLoadedOperator, call);
                    try {
                        Thread.sleep(ASSIGNMENT_DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            while (isSimulationRunning && anyOperatorHasCalls()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Platform.runLater(() -> {
                isSimulationRunning = false;
                startSimulationButton.setDisable(false);
                stopSimulationButton.setDisable(true);
            });
        }).start();

        for (Operator operator : operatorList) {
            processOperatorQueue(operator);
        }
    }

    private void stopSimulation() {
        isSimulationRunning = false;
        startSimulationButton.setDisable(false);
        stopSimulationButton.setDisable(true);
    }

    private Operator findLeastLoadedOperator() {
        Operator leastLoaded = null;
        int minWorkload = Integer.MAX_VALUE;
        for (Operator operator : operatorList) {
            Integer workload = operatorWorkloads.get(operator);
            if (workload != null && workload < minWorkload) {
                minWorkload = workload;
                leastLoaded = operator;
            }
        }
        return leastLoaded;
    }

    private void assignCallToOperator(Operator operator, Call call) {
        List<Call> queue = operatorCallQueue.get(operator);
        if (queue == null) return;

        queue.add(call);
        Integer currentWorkload = operatorWorkloads.get(operator);
        if (currentWorkload != null) {
            operatorWorkloads.put(operator, currentWorkload + 1);
            workloadTree.insert(currentWorkload + 1);
        }

        Platform.runLater(() -> {
            callQueueView.getItems().remove(call);
            updateOperatorCells(operator);
        });

        if (queue.size() == 1 && isSimulationRunning) {
            processOperatorQueue(operator);
        }
    }

    private void processOperatorQueue(Operator operator) {
        new Thread(() -> {
            while (isSimulationRunning) {
                List<Call> queue = operatorCallQueue.get(operator);
                if (queue == null || queue.isEmpty()) {
                    break;
                }

                Call currentCall = queue.get(0);
                operator.assignCall(currentCall);
                Platform.runLater(() -> {
                    operatorListView.refresh();
                    updateOperatorCells(operator);
                });

                try {
                    Thread.sleep(currentCall.getDurationInSeconds() * 1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!isSimulationRunning) break;

                operator.finishCall();
                queue.remove(0);
                Integer currentWorkload = operatorWorkloads.get(operator);
                if (currentWorkload != null && currentWorkload > 0) {
                    operatorWorkloads.put(operator, currentWorkload - 1);
                    workloadTree.insert(currentWorkload - 1);
                }

                Platform.runLater(() -> {
                    operatorListView.refresh();
                    updateOperatorCells(operator);
                });
            }
        }).start();
    }

    private boolean anyOperatorHasCalls() {
        for (Operator operator : operatorList) {
            List<Call> queue = operatorCallQueue.get(operator);
            if (queue != null && !queue.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void updateOperatorCells(Operator operator) {
        List<Label> cells = operatorCells.get(operator);
        List<Call> queue = operatorCallQueue.get(operator);
        if (cells == null || queue == null) return;

        Platform.runLater(() -> {
            for (int i = 0; i < MAX_CALL_SLOTS; i++) {
                cells.get(i).setText("");
                cells.get(i).setStyle("-fx-background-color: white; -fx-border-color: gray;");
            }

            for (int i = 0; i < queue.size() && i < MAX_CALL_SLOTS; i++) {
                Call call = queue.get(i);
                cells.get(i).setText("📞 " + call.getCallerName());
                if (i == 0 && operator.isBusy()) {
                    cells.get(i).setStyle("-fx-background-color: tomato; -fx-border-color: black;");
                } else {
                    cells.get(i).setStyle("-fx-background-color: lightyellow; -fx-border-color: gray;");
                }
            }
        });
    }

    private void showAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(title);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}