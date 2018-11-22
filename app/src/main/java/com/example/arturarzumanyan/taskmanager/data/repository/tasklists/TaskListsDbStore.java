package com.example.arturarzumanyan.taskmanager.data.repository.tasklists;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.data.db.DbHelper;
import com.example.arturarzumanyan.taskmanager.data.db.SQLiteDbHelper;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;

import java.util.ArrayList;

public class TaskListsDbStore {
    private Context mContext;

    public TaskListsDbStore(Context context) {
        this.mContext = context;
    }

    public ArrayList<TaskList> getTaskLists() {
        return DbHelper.getDbHelper(mContext).getTaskLists();
    }

    public void addTaskLists(ArrayList<TaskList> taskListArrayList) {
        DbHelper.getDbHelper(mContext).insertTaskLists(taskListArrayList);
    }

    public void addTaskList(TaskList taskList) {
        DbHelper.getDbHelper(mContext).insertTaskList(taskList);
    }

    public TaskList getTaskList(String title){
        return DbHelper.getDbHelper(mContext).getTaskList(title);
    }

    public TaskList getTaskList(int id) {
        return DbHelper.getDbHelper(mContext).getTaskList(id);
    }

    public void updateTaskList(TaskList taskList) {
        DbHelper.getDbHelper(mContext).updateTaskList(taskList);
    }

    public void deleteTaskList(TaskList taskList) {
        DbHelper.getDbHelper(mContext).deleteTaskList(taskList);
    }
}
