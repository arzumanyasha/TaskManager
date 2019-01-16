package com.example.arturarzumanyan.taskmanager.data.repository.tasklists;

import com.example.arturarzumanyan.taskmanager.data.db.DbHelper;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.specification.TaskListsSpecification;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;

import java.util.List;

public class TaskListsDbStore {

    TaskListsDbStore() {
    }

    public List<TaskList> getTaskLists(TaskListsSpecification taskListsSpecification) {
        return DbHelper.getDbHelperInstance().getTaskLists(taskListsSpecification);
    }

    public void addOrUpdateTaskLists(List<TaskList> taskLists) {
        DbHelper.getDbHelperInstance().addOrUpdateTaskLists(taskLists);
    }

    public void deleteTaskList(TaskList taskList) {
        DbHelper.getDbHelperInstance().deleteTaskList(taskList);
    }
}
