package org.example.taskmanager.tasks;

public class Priority {
    private String name;
    public Priority(){}
    public Priority(String name){
        this.name = name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
}