package com.example.arturarzumanyan.taskmanager.data.repository.tasks;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.data.db.SQLiteDbHelper;
import com.example.arturarzumanyan.taskmanager.domain.Task;

import java.text.ParseException;
import java.util.ArrayList;

public class TasksDbStore {
    private SQLiteDbHelper sqliteDbHelper;

    public ArrayList<Task> getTasks() {
        return null;
    }

    public ArrayList<Task> getTasksFromTaskList(Context context, int taskListId) {
        sqliteDbHelper = new SQLiteDbHelper(context);
        return sqliteDbHelper.getTasksFromList(taskListId);
    }

    public void addTasks(Context context, ArrayList<Task> tasks) {
        sqliteDbHelper = new SQLiteDbHelper(context);
        sqliteDbHelper.insertTasks(tasks);
    }

    public void addTask(Task task) {

    }

    public void updateTaskList(Task task) {

    }

    public void deleteTask(Task task) {

    }
}
