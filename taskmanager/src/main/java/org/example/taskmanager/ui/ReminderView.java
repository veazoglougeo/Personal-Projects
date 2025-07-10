package org.example.taskmanager.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.taskmanager.app.MainController;
import org.example.taskmanager.data.DataManager;
import org.example.taskmanager.tasks.Reminder;
import org.example.taskmanager.tasks.Task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReminderView {
    private DataManager dataManager;
    private MainController mainController;

    public ReminderView(DataManager dataManager) {
        this.dataManager = dataManager;
        mainController = new MainController();
    }
    public void manageReminders(Task task) {
        if (task.getState().equalsIgnoreCase("Completed")) {
            mainController.createErrorDialog("Cannot set reminders for completed tasks!");
            return;
        }

        Stage reminderWindow = createReminderWindow(task);
        reminderWindow.showAndWait();
    }

    private Stage createReminderWindow(Task task) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Manage Reminders");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        // List to display reminders
        ListView<String> reminderList = new ListView<>();
        if (task.getReminders() != null) {
            task.getReminders().forEach(reminder -> reminderList.getItems().add(reminder.toString()));
        } else {
            task.setReminders(new ArrayList<>()); // Ensure it is initialized
        }
        // Buttons for add, delete
        HBox buttonsContainer = createReminderButtonsContainer(task, reminderList, window);
        layout.getChildren().addAll(new Label("Reminders List"), reminderList, buttonsContainer);

        Scene scene = new Scene(layout, 400, 400);
        window.setScene(scene);
        return window;
    }

    private HBox createReminderButtonsContainer(Task task, ListView<String> reminderList, Stage window) {
        Button addReminderButton = new Button("Add Reminder");
        addReminderButton.setOnAction(e -> addReminder(task, reminderList));

        Button editReminderButton = new Button("Edit Reminder");
        editReminderButton.setOnAction(e -> editReminder(task, reminderList));

        Button deleteReminderButton = new Button("Delete Reminder");
        deleteReminderButton.setOnAction(e -> deleteReminder(task, reminderList));

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> window.close());

        return new HBox(10, addReminderButton, editReminderButton, deleteReminderButton, closeButton);
    }

    // Method to add a reminder
    private void addReminder(Task task, ListView<String> reminderList) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Add Reminder");
        VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(10));
        TextField label = new TextField();
        ComboBox<String> reminderTypeBox = new ComboBox<>();
        reminderTypeBox.getItems().addAll("One day before", "One week before", "One month before", "Custom date");
        reminderTypeBox.setValue("One day before");
        DatePicker customDatePicker = new DatePicker();
        customDatePicker.setDisable(true);
        reminderTypeBox.setOnAction(e -> {
            if (reminderTypeBox.getValue().equals("Custom date")) {
                customDatePicker.setDisable(false);
                // Restrict past dates
                customDatePicker.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        if (date.isBefore(LocalDate.now())) { // Disable past dates
                            setDisable(true);
                            setStyle("-fx-background-color: #d3d3d3;"); // Optional: Gray out past dates
                        }
                    }
                });
            } else {
                customDatePicker.setDisable(true);
            }
        });
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            if ((label.getText().isEmpty() && reminderTypeBox.getValue().isEmpty())||
                    (label.getText().isEmpty() && customDatePicker.getValue()==null)){
                mainController.createErrorDialog("Fill the form");
                return;
            }
            LocalDate dueDate = LocalDate.parse(task.getDueDate());
            LocalDate reminderDate = null;
            switch (reminderTypeBox.getValue()) {
                case "One day before":
                    reminderDate = dueDate.minusDays(1);
                    break;
                case "One week before":
                    reminderDate = dueDate.minusWeeks(1);
                    break;
                case "One month before":
                    reminderDate = dueDate.minusMonths(1);
                    break;
                case "Custom date":
                    reminderDate = customDatePicker.getValue();
                    break;
            }
            if (reminderDate != null && reminderDate.isBefore(dueDate)) {
                Reminder newReminder = new Reminder(reminderDate.toString(), reminderTypeBox.getValue(), label.getText());
                task.getReminders().add(newReminder);
                reminderList.getItems().add(newReminder.toString());
                dataManager.saveData();
                window.close();
            } else {
                mainController.createErrorDialog("Reminder date must be before the due date!");
            }
        });

        vBox.getChildren().addAll(new Label("Label: "), label, new Label("Reminder Type:"), reminderTypeBox, new Label("Custom Date:"), customDatePicker, saveButton);
        Scene scene = new Scene(vBox, 300, 300);
        window.setScene(scene);
        window.showAndWait();
    }

    // Method to delete a reminder
    private void deleteReminder(Task task, ListView<String> reminderList) {
        int index = reminderList.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            task.getReminders().remove(index);
            reminderList.getItems().remove(index);
            dataManager.saveData();
        }
    }

    private void editReminder(Task task, ListView<String> reminderList) {
        int index = reminderList.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            mainController.createErrorDialog("Please select a reminder to edit.");
            return;
        }

        Reminder selectedReminder = task.getReminders().get(index);
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Edit Reminder");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField label = new TextField(selectedReminder.getLabel());

        ComboBox<String> reminderTypeBox = new ComboBox<>();
        reminderTypeBox.getItems().addAll("One day before", "One week before", "One month before", "Custom date");
        reminderTypeBox.setValue(selectedReminder.getType());

        DatePicker customDatePicker = new DatePicker();
        customDatePicker.setDisable(!selectedReminder.getType().equals("Custom date"));
        if (selectedReminder.getType().equals("Custom date")) {
            customDatePicker.setValue(LocalDate.parse(selectedReminder.getDate()));
        }

        reminderTypeBox.setOnAction(e -> {
            if (reminderTypeBox.getValue().equals("Custom date")) {
                customDatePicker.setDisable(false);
                // Restrict past dates
                customDatePicker.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        if (date.isBefore(LocalDate.now())) { // Disable past dates
                            setDisable(true);
                            setStyle("-fx-background-color: #d3d3d3;"); 
                        }
                    }
                });
            } else {
                customDatePicker.setDisable(true);
            }
        });

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            if ((label.getText().isEmpty() && reminderTypeBox.getValue().isEmpty())||
                    (label.getText().isEmpty() && customDatePicker.getValue()==null)) {
                mainController.createErrorDialog("Fill the form");
                return;
            }
            LocalDate dueDate = LocalDate.parse(task.getDueDate());
            LocalDate newReminderDate = null;

            switch (reminderTypeBox.getValue()) {
                case "One day before":
                    newReminderDate = dueDate.minusDays(1);
                    break;
                case "One week before":
                    newReminderDate = dueDate.minusWeeks(1);
                    break;
                case "One month before":
                    newReminderDate = dueDate.minusMonths(1);
                    break;
                case "Custom date":
                    newReminderDate = customDatePicker.getValue();
                    break;
            }

            if (newReminderDate != null && newReminderDate.isBefore(dueDate)) {
                selectedReminder.setLabel(label.getText());
                selectedReminder.setType(reminderTypeBox.getValue());
                selectedReminder.setDate(newReminderDate.toString());

                reminderList.getItems().set(index, selectedReminder.toString());
                dataManager.saveData();
                window.close();
            } else {
                mainController.createErrorDialog("Reminder date must be before the due date!");
            }
        });

        layout.getChildren().addAll(
                new Label("Label:"), label,
                new Label("Reminder Type:"), reminderTypeBox,
                new Label("Custom Date:"), customDatePicker,
                saveButton
        );

        Scene scene = new Scene(layout, 300, 300);
        window.setScene(scene);
        window.showAndWait();
    }
    public void showAllReminders() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("All Reminders");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        ListView<String> allRemindersList = new ListView<>();
        List<Task> tasks = dataManager.getTasks();
        for (Task task : tasks) {
            if (task.getReminders() != null) {
                for (Reminder reminder : task.getReminders()) {
                    String displayText = "Task: " + task.getTitle() + " | Label: " + reminder.getLabel() + " | Date: " + reminder.getDate();
                    allRemindersList.getItems().add(displayText);
                }
            }
        }

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> window.close());

        layout.getChildren().addAll(new Label("All Reminders from All Tasks"), allRemindersList, closeButton);
        Scene scene = new Scene(layout, 400, 300);
        window.setScene(scene);
        window.showAndWait();
    }

}