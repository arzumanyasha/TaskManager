package com.example.arturarzumanyan.taskmanager.data.repository.tasklists.specification;

import com.example.arturarzumanyan.taskmanager.data.db.contract.TasksContract;

public class TaskListFromIdSpecification implements TaskListsSpecification {
    private int taskListId;

    @Override
    public String getSqlQuery() {
        return "SELECT * FROM " + TasksContract.TaskListTable.TABLE_NAME +
                " WHERE " + TasksContract.TaskListTable._ID + " = ?";
    }

    @Override
    public String getSelectionArgs() {
        return Integer.toString(taskListId);
    }

    public void setTaskListId(int taskListId) {
        this.taskListId = taskListId;
    }
}
