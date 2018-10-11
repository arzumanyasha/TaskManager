package com.example.arturarzumanyan.taskmanager.data.repository.tasklists;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.data.db.SQLiteDbHelper;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;

import java.util.ArrayList;

public class TaskListsDbStore {
    private SQLiteDbHelper sqliteDbHelper;

    public ArrayList<TaskList> getTaskLists(Context context) {
        sqliteDbHelper = new SQLiteDbHelper(context);
        return sqliteDbHelper.getTaskLists();
    }

    public void addTaskLists(Context context, ArrayList<TaskList> taskListArrayList) {
        sqliteDbHelper = new SQLiteDbHelper(context);
        sqliteDbHelper.insertTaskLists(taskListArrayList);
    }

    public void addTaskList(TaskList taskList) {

    }


    public void updateTaskList(TaskList taskList) {

    }

    public void deleteTaskList(TaskList taskList) {

    }
}
