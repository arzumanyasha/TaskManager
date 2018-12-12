package com.example.arturarzumanyan.taskmanager.data.repository.tasks;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.data.db.DbHelper;
import com.example.arturarzumanyan.taskmanager.domain.Task;

import java.util.ArrayList;
import java.util.List;

public class TasksDbStore {
    private Context mContext;

    public TasksDbStore(Context context) {
        this.mContext = context;
    }

    public List<Task> getTasksFromTaskList(int taskListId) {
        return DbHelper.getDbHelper(mContext).getTasksFromList(taskListId);
    }
/*
    public void addTasks(List<Task> tasks) {
        DbHelper.getDbHelper(mContext).insertTasks(tasks);
    }

    public void addTask(Task task) {
        DbHelper.getDbHelper(mContext).insertTask(task);
    }

    public void updateTask(Task task) {
        DbHelper.getDbHelper(mContext).updateTask(task);
    }*/

    public void addOrUpdateTasks(List<Task> tasks) {
        DbHelper.getDbHelper(mContext).addOrUpdateTasks(tasks);
    }

    public void deleteTask(Task task) {
        DbHelper.getDbHelper(mContext).deleteTask(task);
    }
}
