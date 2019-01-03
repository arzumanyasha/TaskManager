package com.example.arturarzumanyan.taskmanager.data.repository.tasklists.specification;

import com.example.arturarzumanyan.taskmanager.data.db.contract.TasksContract;

public class AllTaskListsSpecification implements TaskListsSpecification {
    @Override
    public String getSqlQuery() {
        return "SELECT * FROM " + TasksContract.TaskListTable.TABLE_NAME;
    }

    @Override
    public String getSelectionArgs() {
        return null;
    }
}
