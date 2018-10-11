package com.example.arturarzumanyan.taskmanager.data.repository.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.auth.TokenStorage;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.UserDataAsyncTask;
import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;
import com.example.arturarzumanyan.taskmanager.networking.util.TasksParser;

import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

public class TasksCloudStore {
    private static final String BASE_TASKS_URL = "https://www.googleapis.com/tasks/v1/lists/";
    private static final String AUTHORIZATION_KEY = "Authorization";

    private ArrayList<UserDataAsyncTask> mUserTasksAsyncTaskList = new ArrayList<>();

    public void getTasksFromTaskList(Context context, TaskList taskList, final OnTaskCompletedListener listener) {
        String url = BASE_TASKS_URL + taskList.getTaskListId() + "/tasks?showHidden=true";
        final int taskListId = taskList.getId();
        mUserTasksAsyncTaskList.add(new UserDataAsyncTask());
        int position = mUserTasksAsyncTaskList.size() - 1;
        requestUserData(context, mUserTasksAsyncTaskList.get(position), url);
        mUserTasksAsyncTaskList.get(position).setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) throws JSONException, ParseException {
                TasksParser tasksParser = new TasksParser();
                listener.onSuccess(tasksParser.parseTasks(response, taskListId));
            }
        });

    }

    public void addTask(Task task) {

    }

    public void updateTaskList(Task task) {

    }

    public void deleteTask(Task task) {

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
        void onSuccess(ArrayList<Task> taskArrayList);

        void onfail();
    }
}
