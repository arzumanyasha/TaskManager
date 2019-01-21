package com.example.arturarzumanyan.taskmanager.data.repository.tasks;

import com.example.arturarzumanyan.taskmanager.data.db.DbHelper;
import com.example.arturarzumanyan.taskmanager.domain.Task;

import java.util.List;

public class TasksDbStore {

    public TasksDbStore() {
    }

    public List<Task> getTasksFromTaskList(int taskListId) {
        return DbHelper.getDbHelperInstance().getTasksFromList(taskListId);
    }

    public void addOrUpdateTasks(List<Task> tasks) {
        DbHelper.getDbHelperInstance().addOrUpdateTasks(tasks);
    }

    public void deleteTask(Task task) {
        DbHelper.getDbHelperInstance().deleteTask(task);
    }
}
