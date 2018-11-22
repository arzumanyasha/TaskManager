package com.example.arturarzumanyan.taskmanager.data.repository.tasks;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.data.db.DbHelper;
import com.example.arturarzumanyan.taskmanager.domain.Task;

import java.util.ArrayList;

public class TasksDbStore {
    private Context mContext;

    public TasksDbStore(Context context) {
        this.mContext = context;
    }

    public ArrayList<Task> getTasksFromTaskList(int taskListId) {
        return DbHelper.getDbHelper(mContext).getTasksFromList(taskListId);
    }

    public void addTasks(ArrayList<Task> tasks) {
        DbHelper.getDbHelper(mContext).insertTasks(tasks);
    }

    public void addTask(Task task) {
        DbHelper.getDbHelper(mContext).insertTask(task);
    }

    public void updateTask(Task task) {
        DbHelper.getDbHelper(mContext).updateTask(task);
    }

    public void deleteTask(Task task) {
        DbHelper.getDbHelper(mContext).deleteTask(task);
    }
}
