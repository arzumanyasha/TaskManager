package com.example.arturarzumanyan.taskmanager.data.repository.tasklists.specification;

public class TaskListFromIdSpecification implements TaskListsSpecification {
    private int taskListId;

    @Override
    public int getSelectionArgs() {
        return taskListId;
    }

    public void setTaskListId(int taskListId) {
        this.taskListId = taskListId;
    }
}
