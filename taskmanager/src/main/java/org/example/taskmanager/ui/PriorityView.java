package org.example.taskmanager.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.taskmanager.app.MainController;
import org.example.taskmanager.data.DataManager;
import org.example.taskmanager.tasks.Priority;
import org.example.taskmanager.tasks.Task;

public class PriorityView {
    private DataManager dataManager;
    private ListView<String> priorityList;
    private MainController mainController;
    private TaskView taskView;

    public PriorityView(DataManager dataManager, MainController mainController) {
        this.dataManager = dataManager;
        this.mainController = mainController;
        this.priorityList = new ListView<>();
        taskView = new TaskView(dataManager);
        mainController = new MainController();
    }

    public void managePriorities() {
        Stage editPrioritiesWindow = createEditPrioritiesWindow();
        editPrioritiesWindow.showAndWait();
    }

    private Stage createEditPrioritiesWindow() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Edit Priorities");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        // List to display priorities
        priorityList = new ListView<>();
        loadPriorityList();

        // Buttons for add, edit, delete
        HBox buttonsContainer = createPriorityButtonsContainer(window);
        layout.getChildren().addAll(new Label("Priorities List"), priorityList, buttonsContainer);

        Scene scene = new Scene(layout, 400, 400);
        window.setScene(scene);
        return window;
    }

    private HBox createPriorityButtonsContainer(Stage window) {
        Button addPriorityButton = new Button("Add Priority");
        addPriorityButton.setOnAction(e -> addPriority());

        Button editPriorityButton = new Button("Edit Priority");
        editPriorityButton.setOnAction(e -> editPriority());

        Button deletePriorityButton = new Button("Delete Priority");
        deletePriorityButton.setOnAction(e -> deletePriority());

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> window.close());

        return new HBox(10, addPriorityButton, editPriorityButton, deletePriorityButton, closeButton);
    }

    private void addPriority() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Add Priority");
        VBox vBox = createPriorityForm(null, window);
        Scene scene = new Scene(vBox, 400, 300);
        window.setScene(scene);
        window.showAndWait();
    }
    //Method to edit priorities
    private void editPriority() {
        int index = priorityList.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            Priority selectedPriority = dataManager.getPriorities().get(index);
            if (selectedPriority.getName().equalsIgnoreCase("Default")) {
                mainController.createErrorDialog("'Default' priority can't be edited!");
                return;
            }
            Stage window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("Edit Priority");
            VBox vbox = createPriorityForm(selectedPriority, window);
            Scene scene = new Scene(vbox, 400, 300);
            window.setScene(scene);
            window.showAndWait();
        }
    }
    //Method that creates the form for the add/edit priority methods
    private VBox createPriorityForm(Priority priority, Stage window) {
        boolean exists = false;
        String oldPriorityName;
        if (priority != null) {
            oldPriorityName = priority.getName();
        } else {
            oldPriorityName = "";
        }
        Label name = new Label("Priority Name:");
        TextField nameField = new TextField(priority != null ? priority.getName() : "");
        HBox hbox = new HBox(10, name, nameField);
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            if (nameField.getText().isEmpty()){
                mainController.createErrorDialog("Fill the form");
                return;
            }
            if (!nameField.getText().isEmpty()) {
                for (Priority prio : dataManager.getPriorities()) {
                    if (prio.getName().equalsIgnoreCase(nameField.getText())) {
                        mainController.createErrorDialog("Priority already exists!");
                        return;
                    }
                }
                if (priority == null) {
                    Priority newPriority = new Priority(nameField.getText());
                    dataManager.getPriorities().add(newPriority);
                } else {
                    priority.setName(nameField.getText());
                    for (Task task : dataManager.getTasks()) {
                        if (task.getPriority().equalsIgnoreCase(oldPriorityName)) {
                            task.setPriority(nameField.getText());
                        }
                    }
                }
            }
            dataManager.saveData();
            loadPriorityList();
            window.close();
        });
        VBox vbox = new VBox(10, hbox, saveButton);
        vbox.setPadding(new Insets(10));
        return vbox;
    }
    //Method to delete priorities
    private void deletePriority() {
        int index = priorityList.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            if (dataManager.getPriorities().get(index).getName().equalsIgnoreCase("Default")) {
                mainController.createErrorDialog("'Default' priority can't be deleted!");
                return;
            }
            dataManager.deletePriority(dataManager.getPriorities().get(index));
            loadPriorityList();
            taskView.loadTaskList();
        }
    }

    private void loadPriorityList() {
        priorityList.getItems().clear();
        dataManager.getPriorities().forEach(priority -> priorityList.getItems().add(priority.getName()));
    }
    public Button createManagePrioritiesButton(){
        Button managePrioritesButton = new Button("Manage Priorities");
        managePrioritesButton.setOnAction(e -> managePriorities());
        return managePrioritesButton;
    }
}
