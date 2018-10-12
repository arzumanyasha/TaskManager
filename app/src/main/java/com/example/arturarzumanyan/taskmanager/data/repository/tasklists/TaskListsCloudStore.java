package com.example.arturarzumanyan.taskmanager.data.repository.tasklists;

import android.content.Context;
import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.UserDataAsyncTask;
import com.example.arturarzumanyan.taskmanager.networking.util.TaskListsParser;

import org.json.JSONException;

import java.util.ArrayList;

public class TaskListsCloudStore {
    private static final String BASE_TASK_LISTS_URL = "https://www.googleapis.com/tasks/v1/users/@me/lists";

    private UserDataAsyncTask mUserTaskListsAsyncTask;
    private Context mContext;

    public TaskListsCloudStore(Context context) {
        this.mContext = context;
    }

    public void getTaskLists(final OnTaskCompletedListener listener) {
        mUserTaskListsAsyncTask = new UserDataAsyncTask();
        RepositoryLoadHelper repositoryLoadHelper = new RepositoryLoadHelper(mContext);
        repositoryLoadHelper.requestUserData(mUserTaskListsAsyncTask, BASE_TASK_LISTS_URL);
        mUserTaskListsAsyncTask.setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) throws JSONException {
                if (!response.equals("")) {
                    TaskListsParser taskListsParser = new TaskListsParser();
                    listener.onSuccess(taskListsParser.parseTaskLists(response));
                }
            }
        });
    }

    public void addTaskList(TaskList taskList) {

    }

    public void updateTaskList(TaskList taskList) {

    }

    public void deleteTaskList(TaskList taskList) {

    }

    public interface OnTaskCompletedListener {
        void onSuccess(ArrayList<TaskList> taskListArrayList);

        void onfail();
    }
}
