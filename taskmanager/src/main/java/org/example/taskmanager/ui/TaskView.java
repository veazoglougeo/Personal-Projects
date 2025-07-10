package org.example.taskmanager.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.taskmanager.app.MainController;
import org.example.taskmanager.data.DataManager;
import org.example.taskmanager.tasks.Category;
import org.example.taskmanager.tasks.Priority;
import org.example.taskmanager.tasks.Task;

import java.time.LocalDate;
import java.util.List;


public class TaskView {
    private DataManager dataManager;
    private ListView<String> taskList;
    private Label taskStatistics;
    private ReminderView reminderView;
    private MainController mainController;

    /**
     * Constructor for Task View
     * @param dataManager
     */

    public TaskView(DataManager dataManager) {
        this.dataManager = dataManager;
        this.taskList = new ListView<>();
        this.taskStatistics = new Label();
        this.reminderView = new ReminderView(dataManager);
        mainController = new MainController();
    }

    /**
     * Returns the selected task from the list
     * @return String of the task's name or null
     */
    public String getSelectedTask() {
        return taskList.getSelectionModel().getSelectedItem();
    }

    /**
     * Returns the taskList view
     * @return ListView with task names
     */
    public ListView<String> getTaskList() {
        return taskList;
    }

    /**
     * Created the top pane of the UI containing task statistics and buttons
     * @return VBox container with statistics and buttons
     */
    public VBox createTopPane() {
        taskStatistics.setText(getTaskStatistics());
        taskStatistics.setStyle("-fx-font-size: 15px;");

        Button searchTaskButton = new Button("Search Task");
        searchTaskButton.setOnAction(e -> searchTaskWindow());

        Button newTaskButton = new Button("New Task");
        newTaskButton.setOnAction(e -> taskCreationWindow());

        Button editTaskButton = new Button("Edit Task");
        editTaskButton.setOnAction(e -> editTask());

        Button deleteTaskButton = new Button("Delete Task");
        deleteTaskButton.setOnAction(e -> deleteTask());

        Button manageReminders = new Button("Manage Reminder");
        manageReminders.setOnAction(e -> manageReminders());

        Button allReminders = new Button("Show Reminders");
        allReminders.setOnAction(e -> reminderView.showAllReminders());

        HBox topButtonContainer = new HBox(10, searchTaskButton, newTaskButton, editTaskButton, deleteTaskButton,
                manageReminders, allReminders);
        VBox topPane = new VBox(10, taskStatistics, topButtonContainer);
        topPane.setPadding(new Insets(10));
        return topPane;
    }

    /**
     * Creates the center pane of the UI containing the task list
     * @return VBox with the task list
     */
    public VBox createCenterPane() {
        loadTaskList();
        VBox layout = new VBox(10, taskList);
        layout.setPadding(new Insets(10));
        return layout;
    }

    /**
     * Loads the task list from the data manager. Updates UI
     */
    public void loadTaskList() {
        taskList.getItems().clear();
        dataManager.loadData();
        List<Task> tasks = dataManager.getTasks();
        tasks.forEach(task -> taskList.getItems().add(task.getTitle()));
    }

    /**
     * Retrieves and formats the tasks statistics for the top pane
     * @return String containing task statistics
     */
    public String getTaskStatistics() {
        long totalTasks = dataManager.getTasks().size();
        long openTasks = dataManager.getTasks().stream().filter(t -> t.getState().equalsIgnoreCase("Open")).count();
        long inProgressTasks = dataManager.getTasks().stream().filter(t -> t.getState().equalsIgnoreCase("In Progress")).count();
        long postponedTasks = dataManager.getTasks().stream().filter(t -> t.getState().equalsIgnoreCase("Postponed")).count();
        long completedTasks = dataManager.getTasks().stream().filter(t -> t.getState().equalsIgnoreCase("Completed")).count();
        long delayedTasks = dataManager.getTasks().stream().filter(t -> t.getState().equalsIgnoreCase("Delayed")).count();
        long dueInSevenDays = dataManager.getTasks().stream().filter(t -> dataManager.isTaskDueInDays(t, 7)).count();
        return String.format("Total tasks: %d | Open: %d | In Progress: %d | Postponed: %d | Completed: %d | Delayed: %d | Due in seven days: %d",
                totalTasks, openTasks, inProgressTasks, postponedTasks, completedTasks, delayedTasks, dueInSevenDays);
    }

    /**
     * Opens the task creation window
     */
    public void taskCreationWindow() {
        Stage taskCreationWindow = new Stage();
        taskCreationWindow.initModality(Modality.APPLICATION_MODAL);
        taskCreationWindow.setTitle("Add Task");
        GridPane grid = manageTaskForm(null, taskCreationWindow);
        Scene scene = new Scene(grid, 600, 400);
        taskCreationWindow.setScene(scene);
        taskCreationWindow.showAndWait();
        updateStatistics();
    }

    /**
     *  Creates the form to edit or create a task
     * @param task if null then a new is created. If not the task is edited
     * @param window
     * @return Grid pane of the window
     */
    private GridPane manageTaskForm(Task task, Stage window) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);
        TextField nameField = new TextField(task != null ? task.getTitle() : "");
        TextArea descriptionField = new TextArea(task != null ? task.getDescription() : "");
        descriptionField.setPrefRowCount(2);
        ComboBox<String> categoryBox = new ComboBox<>();
        for (Category category : dataManager.getCategories()) {
            categoryBox.getItems().add(category.getName());
        }
        if (task != null) {
            categoryBox.setValue(task.getCategory());
        }
        ComboBox<String> priorityBox = new ComboBox<>();
        for (Priority priority : dataManager.getPriorities()) {
            priorityBox.getItems().add(priority.getName());
        }
        if (task != null) {
            priorityBox.setValue(task.getPriority());
        }
        DatePicker dueDatePicker = new DatePicker(task != null && task.getDueDate().isEmpty() ? LocalDate.parse(task.getDueDate()) : null);
        if (task != null) {
            dueDatePicker.setValue(dataManager.parseStringAsDate(task.getDueDate()));
        }
        // Restrict past dates
        dueDatePicker.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) { // Disable past dates
                    setDisable(true);
                    setStyle("-fx-background-color: #d3d3d3;"); // Optional: Gray out past dates
                }
            }
        });
        ComboBox<String> stateBox = new ComboBox<>();
        if (task != null) {
            stateBox.setValue(task.getState());
            stateBox.getItems().addAll("Open", "In Progress", "Postponed", "Completed", "Delayed");
        } else {
            stateBox.setValue("Open"); //New tasks can only be set to "Open"
        }
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            if(nameField.getText().isEmpty() || descriptionField.getText().isEmpty()||
                    categoryBox.getValue() == null  ||categoryBox.getValue().isEmpty() || priorityBox.getValue().isEmpty()||
                dueDatePicker.getValue() == null || stateBox.getValue().isEmpty()){
                mainController.createErrorDialog("Fill the form");
                return;
            }
            if (task == null) {
                Task newTask = new Task(nameField.getText(), descriptionField.getText(), categoryBox.getValue(), priorityBox.getValue(), dueDatePicker.getValue().toString(), stateBox.getValue());
                dataManager.getTasks().add(newTask);
            } else {
                task.setTitle(nameField.getText());
                task.setDescription(descriptionField.getText());
                task.setCategory(categoryBox.getValue());
                task.setPriority(priorityBox.getValue());
                task.setDueDate(dueDatePicker.getValue().toString());
                task.setState(stateBox.getValue());
            }
            dataManager.saveData();
            loadTaskList();
            window.close();
        });
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Category"), 0, 2);
        grid.add(categoryBox, 1, 2);
        grid.add(new Label("Priority:"), 0, 3);
        grid.add(priorityBox, 1, 3);
        grid.add(new Label("Due date:"), 0, 4);
        grid.add(dueDatePicker, 1, 4);
        grid.add(new Label("State:"), 0, 5);
        grid.add(stateBox, 1, 5);
        grid.add(saveButton, 0, 6);
        return grid;
    }

    /**
     * Opens the task editing window for the selected task
     */
    public void editTask() {
        int index = taskList.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            Task selectedTask = dataManager.getTasks().get(index);
            Stage editTaskWindow = new Stage();
            editTaskWindow.initModality(Modality.APPLICATION_MODAL);
            editTaskWindow.setTitle("Edit Task");
            GridPane grid = manageTaskForm(selectedTask, editTaskWindow);
            Scene scene = new Scene(grid, 600, 400);
            editTaskWindow.setScene(scene);
            editTaskWindow.showAndWait();
            updateStatistics();
        }
    }

    /**
     * Deletes the selected task from the task list
     */
    public void deleteTask() {
        int index = taskList.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            dataManager.removeTask(index);
            loadTaskList();
            updateStatistics();
        }
    }

    /**
     * Updates the task statistics for the top pane after each change
     */
    public void updateStatistics() {
        taskStatistics.setText(getTaskStatistics());
    }

    /**
     * Opens the window to search tasks
     */
    public void searchTaskWindow() {
        Stage searchWindow = new Stage();
        searchWindow.initModality(Modality.APPLICATION_MODAL);
        searchWindow.setTitle("Search Task");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        TextField titleField = new TextField();
        titleField.setPromptText("Enter task title");
        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().add("All");
        for (Category category : dataManager.getCategories()) {
            categoryBox.getItems().add(category.getName());
        }
        categoryBox.setValue("All");
        ComboBox<String> priorityBox = new ComboBox<>();
        priorityBox.getItems().add("All");
        for (Priority priority : dataManager.getPriorities()) {
            priorityBox.getItems().add(priority.getName());
        }
        priorityBox.setValue("All");
        ListView<String> resultsList = new ListView<>();
        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            String title = titleField.getText().trim();
            String category = categoryBox.getValue();
            String priority = priorityBox.getValue();
            searchTasks(title, category, priority, resultsList);
        });

        layout.getChildren().addAll(
                new Label("Title:"), titleField,
                new Label("Category:"), categoryBox,
                new Label("Priority:"), priorityBox,
                searchButton,
                new Label("Results:"), resultsList
        );
        Scene scene = new Scene(layout, 550, 400);
        searchWindow.setScene(scene);
        searchWindow.show();
    }

    /**
     *
     * @param title filter
     * @param category filter
     * @param priority filter
     * @param resultsList retrieved tasks
     */
    private void searchTasks(String title, String category, String priority, ListView<String> resultsList) {
        resultsList.getItems().clear();
        List<Task> filteredTasks = dataManager.getTasks().stream()
                .filter(task -> title.isEmpty() || task.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(task -> category.equals("All") || task.getCategory().equalsIgnoreCase(category))
                .filter(task -> priority.equals("All") || task.getPriority().equalsIgnoreCase(priority))
                .toList();
        for (Task task : filteredTasks) {
            String result = String.format("Title: %s | Priority: %s | Category: %s | Due Date: %s",
                    task.getTitle(), task.getPriority(), task.getCategory(), task.getDueDate());
            resultsList.getItems().add(result);
        }
    }

    /**
     * Opens the reminder management window for a selected task
     */
    public void manageReminders(){
        int index = taskList.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            Task selectedTask = dataManager.getTasks().get(index);
            reminderView.manageReminders(selectedTask);
        }
    }
}