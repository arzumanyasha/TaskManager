package com.example.arturarzumanyan.taskmanager.data.repository.tasklists.specification;

public interface TaskListsSpecification {
    String getSqlQuery();
    int getSelectionArgs();
}
