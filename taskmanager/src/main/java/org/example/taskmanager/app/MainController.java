package org.example.taskmanager.app;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.taskmanager.data.DataManager;
import org.example.taskmanager.tasks.Task;
import org.example.taskmanager.ui.CategoryView;
import org.example.taskmanager.ui.PriorityView;
import org.example.taskmanager.ui.ReminderView;
import org.example.taskmanager.ui.TaskView;

import java.util.List;

public class MainController {
    private Stage primaryStage;
    private DataManager dataManager;
    private TaskView taskView;
    private CategoryView categoryView;
    private PriorityView priorityView;
    private ReminderView reminderView;

    public MainController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.dataManager = new DataManager();
        this.taskView = new TaskView(dataManager);
        this.categoryView = new CategoryView(dataManager, this, taskView);
        this.priorityView = new PriorityView(dataManager, this);
        this.reminderView = new ReminderView(dataManager);
    }
    public MainController(){}

    public void initialize() {
        dataManager.loadData();
        dataManager.checkAndUpdateState();
        checkDelayedTasks();
        HBox bottomPane = new HBox(10, categoryView.createManageCategoriesButton(), priorityView.createManagePrioritiesButton());
        VBox root = new VBox(10, taskView.createTopPane(), taskView.createCenterPane(), bottomPane);
        root.setPadding(new Insets(10));
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("MediaLab Assistant");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    //Method to create the error dialog with a String
    public void createErrorDialog(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(errorMessage);
        alert.showAndWait();
    }
    //Method to check delayed tasks before the main app
    public void checkDelayedTasks() {
        List<Task> delayedTasks = dataManager.getTasks().stream()
                .filter(task -> task.getState().equalsIgnoreCase("Delayed"))
                .toList();
        if (!delayedTasks.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
             alert.setTitle("Delayed Tasks");
            alert.setHeaderText("There are " + delayedTasks.size() + " delayed tasks!");
            alert.showAndWait();
        }
    }
}