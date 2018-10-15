package com.example.arturarzumanyan.taskmanager.domain;

public class TaskList {
    private int id;
    private String taskListId;
    private String title;

    public TaskList(String taskListId, String title) {
        this.taskListId = taskListId;
        this.title = title;
    }

    public TaskList(int id, String taskListId, String title) {
        this.id = id;
        this.taskListId = taskListId;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskListId() {
        return taskListId;
    }

    public void setTaskListId(String taskListId) {
        this.taskListId = taskListId;
    }
}
