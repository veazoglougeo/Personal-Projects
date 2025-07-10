package org.example.taskmanager.tasks;

import java.util.ArrayList;
import java.util.List;

public class Task {
    private String title;
    private String description;
    private String category;
    private String priority;
    private String dueDate;
    private String state;
    private List<Reminder> reminders;
    public Task(){}
    public Task(String title, String description, String category, String priority, String dueDate, String state){
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.dueDate = dueDate;
        this.state = state;
        reminders = new ArrayList<>();
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getTitle(){
        return title;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public String getDescription(){
        return description;
    }
    public void setCategory(String category){
        this.category = category;
    }
    public String getCategory(){
        return category;
    }
    public void setPriority(String priority){
        this.priority = priority;
    }
    public String getPriority(){
        return priority;
    }
    public void setDueDate(String dueDate){
        this.dueDate = dueDate;
    }
    public String getDueDate(){
        return dueDate;
    }
    public void setState(String state){
        this.state = state;
        if(state == "Completed"){
            this.reminders = null;
        }
    }
    public String getState(){
        return state;
    }
    public void setReminders(List<Reminder> reminders) {
        this.reminders = reminders;
    }
    public List<Reminder> getReminders() {
        return reminders;
    }
}