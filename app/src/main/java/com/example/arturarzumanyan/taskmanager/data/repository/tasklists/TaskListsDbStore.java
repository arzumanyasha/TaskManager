package com.example.arturarzumanyan.taskmanager.data.repository.tasklists;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.data.db.SQLiteDbHelper;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;

import java.util.ArrayList;

public class TaskListsDbStore {
    private SQLiteDbHelper mSqliteDbHelper;
    private Context mContext;

    public TaskListsDbStore(Context context) {
        this.mContext = context;
        mSqliteDbHelper = new SQLiteDbHelper(mContext);
    }

    public ArrayList<TaskList> getTaskLists() {
        return mSqliteDbHelper.getTaskLists();
    }

    public void addTaskLists(ArrayList<TaskList> taskListArrayList) {
        mSqliteDbHelper.insertTaskLists(taskListArrayList);
    }

    public void addTaskList(TaskList taskList) {
        mSqliteDbHelper.insertTaskList(taskList);
    }

    public TaskList getTaskList(int id) {
        return mSqliteDbHelper.getTaskList(id);
    }

    public void updateTaskList(TaskList taskList) {
        mSqliteDbHelper.updateTaskList(taskList);
    }

    public void deleteTaskList(TaskList taskList) {
        mSqliteDbHelper.deleteTaskList(taskList);
    }
}
