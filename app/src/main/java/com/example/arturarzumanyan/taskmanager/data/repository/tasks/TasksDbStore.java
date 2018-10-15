package com.example.arturarzumanyan.taskmanager.data.repository.tasks;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.data.db.SQLiteDbHelper;
import com.example.arturarzumanyan.taskmanager.domain.Task;

import java.text.ParseException;
import java.util.ArrayList;

public class TasksDbStore {
    private SQLiteDbHelper mSqliteDbHelper;
    private Context mContext;

    public TasksDbStore(Context context) {
        this.mContext = context;
        mSqliteDbHelper = new SQLiteDbHelper(mContext);
    }

    public ArrayList<Task> getTasksFromTaskList(int taskListId) {
        return mSqliteDbHelper.getTasksFromList(taskListId);
    }

    public void addTasks(ArrayList<Task> tasks) {
        mSqliteDbHelper.insertTasks(tasks);
    }

    public void addTask(Task task) {

    }

    public void updateTaskList(Task task) {

    }

    public void deleteTask(Task task) {

    }
}
