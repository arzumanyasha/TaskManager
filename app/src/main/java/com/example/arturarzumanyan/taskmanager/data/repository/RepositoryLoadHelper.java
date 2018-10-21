package com.example.arturarzumanyan.taskmanager.data.repository;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.auth.TokenStorage;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.UserDataAsyncTask;
import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;

import java.io.IOException;
import java.util.HashMap;

import static com.example.arturarzumanyan.taskmanager.networking.base.BaseHttpUrlConnection.JSON_CONTENT_TYPE_VALUE;
import static com.example.arturarzumanyan.taskmanager.networking.util.TaskListsParser.TITLE_KEY;
import static com.example.arturarzumanyan.taskmanager.networking.util.TasksParser.DUE_KEY;
import static com.example.arturarzumanyan.taskmanager.networking.util.TasksParser.NOTES_KEY;

public class RepositoryLoadHelper {
    public static final String AUTHORIZATION_KEY = "Authorization";

    private Context mContext;

    public RepositoryLoadHelper(Context context) {
        this.mContext = context;
    }

    public void requestUserData(UserDataAsyncTask asyncTask, String url) {
        FirebaseWebService.RequestMethods requestMethod = FirebaseWebService.RequestMethods.GET;
        RequestParameters requestParameters = new RequestParameters(mContext,
                url,
                requestMethod,
                new HashMap<String, String>()
        );
        requestParameters.setRequestHeaderParameters(new HashMap<String, String>());
        asyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, requestParameters);
    }

    public RequestParameters getTaskCreateOrUpdateParameters(Task task,
                                                             String url,
                                                             FirebaseWebService.RequestMethods requestMethod) {

        HashMap<String, String> requestBody = new HashMap<>();

        requestBody.put(TITLE_KEY, task.getName());

        if (!task.getDescription().isEmpty()) {
            requestBody.put(NOTES_KEY, task.getDescription());
        }

        if (task.getDate() != null) {
            requestBody.put(DUE_KEY, DateUtils.formatTaskDate(task.getDate()));
        }

        HashMap<String, String> requestHeaderParameters = new HashMap<>();

        requestHeaderParameters.put("Content-Type", JSON_CONTENT_TYPE_VALUE);

        RequestParameters requestParameters = new RequestParameters(mContext,
                url,
                requestMethod,
                requestBody);
        requestParameters.setRequestHeaderParameters(requestHeaderParameters);

        return requestParameters;
    }

    public RequestParameters getTaskDeleteParameters(String url) {
        RequestParameters requestParameters = new RequestParameters(
                mContext,
                url,
                FirebaseWebService.RequestMethods.DELETE,
                new HashMap<String, String>()
        );

        requestParameters.setRequestHeaderParameters(new HashMap<String, String>());
        return requestParameters;
    }

    public RequestParameters getTaskListCreateOrUpdateParameters(TaskList taskList,
                                                                 String url,
                                                                 FirebaseWebService.RequestMethods requestMethod){
        HashMap<String, String> requestBody = new HashMap<>();
        requestBody.put(TITLE_KEY, taskList.getTitle());

        HashMap<String, String> requestHeaderParameters = new HashMap<>();

        requestHeaderParameters.put("Content-Type", JSON_CONTENT_TYPE_VALUE);

        RequestParameters requestParameters = new RequestParameters(mContext,
                url,
                requestMethod,
                requestBody);
        requestParameters.setRequestHeaderParameters(requestHeaderParameters);

        return requestParameters;
    }

    public RequestParameters getTaskListDeleteParameters(String url){
        RequestParameters requestParameters = new RequestParameters(
                mContext,
                url,
                FirebaseWebService.RequestMethods.DELETE,
                new HashMap<String, String>()
        );
        requestParameters.setRequestHeaderParameters(new HashMap<String, String>());
        return requestParameters;
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }
}
