package com.example.arturarzumanyan.taskmanager.data.repository.tasklists.specification;

public class TaskListFromIdSpecification implements TaskListsSpecification {
    private String taskListId;

    @Override
    public String getSelectionArgs() {
        return taskListId;
    }

    public void setTaskListId(String taskListId) {
        this.taskListId = taskListId;
    }
}
