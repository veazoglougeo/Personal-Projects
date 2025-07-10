package org.example.taskmanager.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.taskmanager.tasks.Category;
import org.example.taskmanager.tasks.Priority;
import org.example.taskmanager.tasks.Reminder;
import org.example.taskmanager.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager instance = null;

    private static final String DATA_DIR = "medialab";
    private static final String CATEGORIES_FILE = DATA_DIR + "/categories.json";
    private static final String PRIORITIES_FILE = DATA_DIR + "/priorities.json";
    private static final String TASKS_FILE = DATA_DIR + "/tasks.json";

    private List<Category> categories;
    private List<Priority> priorities;
    private List<Reminder> reminders;
    private List<Task> tasks;
    public DataManager(){
        categories = new ArrayList<>();
        priorities = new ArrayList<>();
        reminders = new ArrayList<>();
        tasks = new ArrayList<>();
    }

    // Load data from JSON files
    public void loadData() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            categories = mapper.readValue(new File(CATEGORIES_FILE),
                    mapper.getTypeFactory().constructCollectionType(List.class, Category.class));
            priorities = mapper.readValue(new File(PRIORITIES_FILE),
                    mapper.getTypeFactory().constructCollectionType(List.class, Priority.class));
            tasks = mapper.readValue(new File(TASKS_FILE),
                    mapper.getTypeFactory().constructCollectionType(List.class, Task.class));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Method that saves data to JSON files
    public void saveData() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(CATEGORIES_FILE), categories);
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(PRIORITIES_FILE), priorities);
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(TASKS_FILE), tasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<Category> getCategories() {
        return categories;
    }
    public List<Priority> getPriorities() {
        return priorities;
    }
    public List<Reminder> getReminders() {
        return reminders;
    }
    public List<Task> getTasks() {
        return tasks;
    }
    public void deleteCategory(Category category) {
        if (categories.contains(category)) {
            categories.remove(category);
            System.out.println(category.getName() + " removed.");
            tasks.removeIf(task -> task.getCategory().equals(category.getName()));
            saveData();
        }else{
            System.out.println("Category does not exist.");
        }
    }
    public void deletePriority(Priority priority){
        if(priorities.contains(priority)){
            priorities.remove(priority);
            System.out.println(priority.getName() + " removed");
            for (Task task : tasks){
                if(task.getPriority().equalsIgnoreCase(priority.getName())){
                    task.setPriority("Default");
                }
            }
            saveData();
        }else{
            System.out.println("Priority does not exist.");
        }
    }
    public LocalDate parseStringAsDate(String date) {
        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            return LocalDate.parse(date, dtf1);
        } catch (DateTimeParseException e1) {
            try {
                return LocalDate.parse(date, dtf2);
            } catch (DateTimeParseException e2) {
                System.out.println("Invalid date format: " + date);
                return null;
            }
        }
    }
    public void removeTask(int index){
        tasks.remove(index);
        saveData();
    }
    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }
    public boolean isTaskDueInDays(Task task, int days) {
        LocalDate today = LocalDate.now();
        LocalDate deadline = parseStringAsDate(task.getDueDate());
        long daysBetween = ChronoUnit.DAYS.between(today, deadline);
        return daysBetween >= 0 && daysBetween <= days;
    }

    //Method to check and update task states
    public void checkAndUpdateState(){
        LocalDate today = LocalDate.now();
        for (Task task : tasks){
            if(!task.getState().equalsIgnoreCase("Completed")){
                LocalDate dueDate = LocalDate.parse(task.getDueDate());
                if (dueDate.isBefore(today)){
                    task.setState("Delayed");
                }
            }
        }
        saveData();
    }
}
