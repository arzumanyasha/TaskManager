package com.example.arturarzumanyan.taskmanager.domain;

import java.util.Date;

public class Task {
    private String id;
    private String name;
    private String description;
    private boolean isExecuted;
    private Date date;
    private String listName;

    public Task(String id,
                String name,
                String description,
                boolean isExecuted,
                Date date,
                String listName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isExecuted = isExecuted;
        this.date = date;
        this.listName = listName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isExecuted() {
        return isExecuted;
    }

    public void setExecuted(boolean executed) {
        isExecuted = executed;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }
}
