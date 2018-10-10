package com.example.arturarzumanyan.taskmanager.data.repository.tasklists;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.auth.TokenStorage;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.UserDataAsyncTask;
import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;
import com.example.arturarzumanyan.taskmanager.networking.util.TaskListsParser;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskListsCloudRepository {
    private static final String BASE_TASK_LISTS_URL = "https://www.googleapis.com/tasks/v1/users/@me/lists";
    private static final String AUTHORIZATION_KEY = "Authorization";

    private UserDataAsyncTask mUserTaskListsAsyncTask;

    public void getTaskLists(Context context, final OnTaskCompletedListener listener) {
        mUserTaskListsAsyncTask = new UserDataAsyncTask();
        requestUserData(context, mUserTaskListsAsyncTask, BASE_TASK_LISTS_URL);
        mUserTaskListsAsyncTask.setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) throws JSONException {
                if (!response.equals("")) {
                    //storeTaskLists(response);
                    //loadTasks();
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

    private void requestUserData(Context context, UserDataAsyncTask asyncTask, String url) {
        TokenStorage tokenStorage = new TokenStorage();

        FirebaseWebService.RequestMethods requestMethod = FirebaseWebService.RequestMethods.GET;
        HashMap<String, String> requestBodyParameters = new HashMap<>();
        HashMap<String, String> requestHeaderParameters = new HashMap<>();
        String token = tokenStorage.getAccessToken(context);
        requestHeaderParameters.put(AUTHORIZATION_KEY, "Bearer " + tokenStorage.getAccessToken(context));
        RequestParameters requestParameters = new RequestParameters(url,
                requestMethod,
                requestBodyParameters,
                requestHeaderParameters);
        asyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, requestParameters);
    }

    public interface OnTaskCompletedListener {
        void onSuccess(ArrayList<TaskList> taskListArrayList);

        void onfail();
    }
}
