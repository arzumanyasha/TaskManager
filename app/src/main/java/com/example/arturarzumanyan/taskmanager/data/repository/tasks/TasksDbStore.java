package com.example.arturarzumanyan.taskmanager.data.repository.tasks;

import com.example.arturarzumanyan.taskmanager.data.db.DbHelper;
import com.example.arturarzumanyan.taskmanager.domain.Task;

import java.util.List;

import io.reactivex.Single;

public class TasksDbStore {

    public TasksDbStore() {
    }

    public Single<List<Task>> getTasksFromTaskList(int taskListId) {
        return Single.fromCallable(() -> DbHelper.getDbHelperInstance().getTasksFromList(taskListId));
    }

    public Single<Boolean> addOrUpdateTasks(List<Task> tasks) {
        return Single.fromCallable(() -> DbHelper.getDbHelperInstance().addOrUpdateTasks(tasks));
    }

    public Single<Boolean> deleteTask(Task task) {
        return Single.fromCallable(() -> DbHelper.getDbHelperInstance().deleteTask(task));
    }
}
