package com.example.todolist;

public class Task {
    String taskId;
    String taskDescription;

    public Task() {
    }

    public Task(String taskId, String taskDescription) {
        this.taskId = taskId;
        this.taskDescription = taskDescription;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}

