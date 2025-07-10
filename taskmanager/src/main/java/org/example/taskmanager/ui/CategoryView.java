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
import org.example.taskmanager.tasks.Category;
import org.example.taskmanager.tasks.Task;

public class CategoryView {
    private DataManager dataManager;
    private ListView<String> categoryList;
    private MainController mainController;
    private TaskView taskView;

    public CategoryView(DataManager dataManager, MainController mainController, TaskView taskView) {
        this.dataManager = dataManager;
        this.mainController = mainController;
        this.categoryList = new ListView<>();
        this.taskView = taskView; // Use the existing TaskView instance
    }
    public Button createManageCategoriesButton() {
        Button manageCategoriesButton = new Button("Manage Categories");
        manageCategoriesButton.setOnAction(e -> manageCategories());
        return manageCategoriesButton;
    }
    public void manageCategories() {
        Stage editCategoriesWindow = createEditCategoriesWindow();
        editCategoriesWindow.showAndWait();
        taskView.loadTaskList();
    }

    private Stage createEditCategoriesWindow() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Edit Categories");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        // List to display categories
        categoryList = new ListView<>();
        loadCategoryList();

        // Buttons for add, edit, delete
        HBox buttonsContainer = createCategoryButtonsContainer(window);
        layout.getChildren().addAll(new Label("Categories List"), categoryList, buttonsContainer);

        Scene scene = new Scene(layout, 400, 400);
        window.setScene(scene);
        return window;
    }

    private HBox createCategoryButtonsContainer(Stage window) {
        Button addCategoryButton = new Button("Add Category");
        addCategoryButton.setOnAction(e -> addCategory());

        Button editCategoryButton = new Button("Edit Category");
        editCategoryButton.setOnAction(e -> editCategory());

        Button deleteCategoryButton = new Button("Delete Category");
        deleteCategoryButton.setOnAction(e -> deleteCategory());

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> window.close());

        return new HBox(10, addCategoryButton, editCategoryButton, deleteCategoryButton, closeButton);
    }
    private void addCategory() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Add Category");
        VBox vBox = createCategoryForm(null, window);
        Scene scene = new Scene(vBox, 400, 300);
        window.setScene(scene);
        window.showAndWait();
    }
    //Method to edit categories
    private void editCategory() {
        int index = categoryList.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            Category selectedCategory = dataManager.getCategories().get(index);
            Stage window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("Edit Category");
            VBox vbox = createCategoryForm(selectedCategory, window);
            Scene scene = new Scene(vbox, 400, 300);
            window.setScene(scene);
            window.showAndWait();
        }
    }
    private VBox createCategoryForm(Category category, Stage window) {
        String oldCategoryName;
        if (category != null) {
            oldCategoryName = category.getName();
        } else {
            oldCategoryName = "";
        }
        Label name = new Label("Category Name:");
        TextField nameField = new TextField(category != null ? category.getName() : "");
        HBox hbox = new HBox(10, name, nameField);
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            if (nameField.getText().isEmpty()){
                mainController.createErrorDialog("Fill the form");
                return;
            }
            if (!nameField.getText().isEmpty()) {
                for (Category cat : dataManager.getCategories()) {
                    if (cat.getName().equalsIgnoreCase(nameField.getText())) {
                        mainController.createErrorDialog("Category already exists!");
                        return;
                    }
                }
                if (category == null) {
                    Category newCategory = new Category(nameField.getText());
                    dataManager.getCategories().add(newCategory);
                } else {
                    category.setName(nameField.getText());
                    for (Task task : dataManager.getTasks()) {
                        if (task.getCategory().equalsIgnoreCase(oldCategoryName)) {
                            task.setCategory(nameField.getText());
                        }
                    }
                }
            }
            dataManager.saveData();
            loadCategoryList();
            window.close();
        });
        VBox vbox = new VBox(10, hbox, saveButton);
        vbox.setPadding(new Insets(10));
        return vbox;
    }
    //Method to delete categories
    private void deleteCategory() {
        int index = categoryList.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            Category categoryToDelete = dataManager.getCategories().get(index);

            // Delete all tasks corresponding to the category
            dataManager.getTasks().removeIf(task -> task.getCategory().equalsIgnoreCase(categoryToDelete.getName()));

            // Delete the category
            dataManager.deleteCategory(categoryToDelete);

            // Save the updated data
            dataManager.saveData();

            // Reload the category list
            loadCategoryList();

            // Notify the TaskView to refresh its task list
            taskView.loadTaskList();
        }
    }


    private void loadCategoryList() {
        categoryList.getItems().clear();
        dataManager.getCategories().forEach(category -> categoryList.getItems().add(category.getName()));
    }
}
