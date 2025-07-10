package org.example.taskmanager.tasks;

public class Reminder {
    private String date;
    private String type;
    private String label;

    // Default constructor
    public Reminder() {}
    public Reminder(String date, String type, String label){
        this.type = type;
        this.date = date;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String toString() {
        return label + " on " + date;
    }
}