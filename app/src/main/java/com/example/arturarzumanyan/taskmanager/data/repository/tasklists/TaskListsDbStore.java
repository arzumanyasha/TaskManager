package com.example.arturarzumanyan.taskmanager.data.repository.tasklists;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.data.db.SQLiteDbHelper;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;

import java.util.ArrayList;

public class TaskListsDbStore {
    private SQLiteDbHelper sqliteDbHelper;
    private Context mContext;

    public TaskListsDbStore(Context context) {
        this.mContext = context;
    }

    public ArrayList<TaskList> getTaskLists() {
        sqliteDbHelper = new SQLiteDbHelper(mContext);
        return sqliteDbHelper.getTaskLists();
    }

    public void addTaskLists(ArrayList<TaskList> taskListArrayList) {
        sqliteDbHelper = new SQLiteDbHelper(mContext);
        sqliteDbHelper.insertTaskLists(taskListArrayList);
    }

    public void addTaskList(TaskList taskList) {

    }


    public void updateTaskList(TaskList taskList) {

    }

    public void deleteTaskList(TaskList taskList) {

    }
}
