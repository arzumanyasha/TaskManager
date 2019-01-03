package com.example.arturarzumanyan.taskmanager.data.repository.tasklists;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.data.db.DbHelper;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.specification.TaskListsSpecification;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;

import java.util.List;

public class TaskListsDbStore {
    private Context mContext;

    public TaskListsDbStore(Context context) {
        this.mContext = context;
    }

    public List<TaskList> getTaskLists(TaskListsSpecification taskListsSpecification){
       return DbHelper.getDbHelper(mContext).getTaskLists(taskListsSpecification);
    }

    public void addOrUpdateTaskLists(List<TaskList> taskLists){
        DbHelper.getDbHelper(mContext).addOrUpdateTaskLists(taskLists);
    }

    public void deleteTaskList(TaskList taskList) {
        DbHelper.getDbHelper(mContext).deleteTaskList(taskList);
    }
}
