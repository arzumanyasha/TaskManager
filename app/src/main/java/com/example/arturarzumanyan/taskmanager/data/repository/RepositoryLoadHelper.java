package com.example.arturarzumanyan.taskmanager.data.repository;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.auth.TokenStorage;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.UserDataAsyncTask;
import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.arturarzumanyan.taskmanager.networking.base.BaseHttpUrlConnection.JSON_CONTENT_TYPE_VALUE;
import static com.example.arturarzumanyan.taskmanager.networking.util.TaskListsParser.TITLE_KEY;
import static com.example.arturarzumanyan.taskmanager.networking.util.TasksParser.DUE_KEY;
import static com.example.arturarzumanyan.taskmanager.networking.util.TasksParser.NOTES_KEY;

public class RepositoryLoadHelper {
    public static final String AUTHORIZATION_KEY = "Authorization";
    private static final String CONTENT_TYPE_KEY = "Content-Type";

    private Context mContext;

    public RepositoryLoadHelper(Context context) {
        this.mContext = context;
    }

    public void requestUserData(UserDataAsyncTask asyncTask, String url) {
        FirebaseWebService.RequestMethods requestMethod = FirebaseWebService.RequestMethods.GET;
        RequestParameters requestParameters = new RequestParameters(mContext,
                url,
                requestMethod,
                new HashMap<String, Object>()
        );
        requestParameters.setRequestHeaderParameters(new HashMap<String, String>());
        asyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, requestParameters);
    }

    public RequestParameters getEventCreateOrUpdateParameters(Event event,
                                                              String url,
                                                              FirebaseWebService.RequestMethods requestMethod) {
        HashMap<String, Object> requestBody = new HashMap<>();

        HashMap<String, String> startTimeMap = new HashMap<>();
        HashMap<String, String> endTimeMap = new HashMap<>();
        startTimeMap.put("dateTime", DateUtils.formatEventTime(event.getStartTime()));
        endTimeMap.put("dateTime", DateUtils.formatEventTime(event.getEndTime()));

        requestBody.put("summary", event.getName());

        if (!event.getDescription().isEmpty()) {
            requestBody.put("description", event.getDescription());
        }

        requestBody.put("colorId", Integer.toString(event.getColorId()));

        requestBody.put("start", startTimeMap);
        requestBody.put("end", endTimeMap);

        if (event.getIsNotify() == 1) {
            HashMap<String, Object> remindersMap = new HashMap<>();
            List<Object> overrides = new ArrayList<>();

            HashMap<String, Object> overridesMap = new HashMap<>();
            overridesMap.put("method", "popup");
            overridesMap.put("minutes", 10);
            overrides.add(overridesMap);
            remindersMap.put("overrides", overrides);
            remindersMap.put("useDefault", false);

            requestBody.put("reminders", remindersMap);
        } else {
            HashMap<String, Object> remindersMap = new HashMap<>();
            remindersMap.put("useDefault", false);
            requestBody.put("reminders", remindersMap);
        }

        HashMap<String, String> requestHeaderParameters = new HashMap<>();

        requestHeaderParameters.put(CONTENT_TYPE_KEY, JSON_CONTENT_TYPE_VALUE);

        RequestParameters requestParameters = new RequestParameters(mContext,
                url,
                requestMethod,
                requestBody);
        requestParameters.setRequestHeaderParameters(requestHeaderParameters);

        return requestParameters;
    }

    public RequestParameters getTaskCreateOrUpdateParameters(Task task,
                                                             String url,
                                                             FirebaseWebService.RequestMethods requestMethod) {

        HashMap<String, Object> requestBody = new HashMap<>();

        requestBody.put(TITLE_KEY, task.getName());

        if (!task.getDescription().isEmpty()) {
            requestBody.put(NOTES_KEY, task.getDescription());
        }

        if (task.getDate() != null) {
            requestBody.put(DUE_KEY, DateUtils.formatTaskDate(task.getDate()));
        }

        HashMap<String, String> requestHeaderParameters = new HashMap<>();

        requestHeaderParameters.put(CONTENT_TYPE_KEY, JSON_CONTENT_TYPE_VALUE);

        RequestParameters requestParameters = new RequestParameters(mContext,
                url,
                requestMethod,
                requestBody);
        requestParameters.setRequestHeaderParameters(requestHeaderParameters);

        return requestParameters;
    }

    public RequestParameters getTaskListCreateOrUpdateParameters(TaskList taskList,
                                                                 String url,
                                                                 FirebaseWebService.RequestMethods requestMethod) {
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put(TITLE_KEY, taskList.getTitle());

        HashMap<String, String> requestHeaderParameters = new HashMap<>();

        requestHeaderParameters.put(CONTENT_TYPE_KEY, JSON_CONTENT_TYPE_VALUE);

        RequestParameters requestParameters = new RequestParameters(mContext,
                url,
                requestMethod,
                requestBody);
        requestParameters.setRequestHeaderParameters(requestHeaderParameters);

        return requestParameters;
    }

    public RequestParameters getDeleteParameters(String url) {
        RequestParameters requestParameters = new RequestParameters(
                mContext,
                url,
                FirebaseWebService.RequestMethods.DELETE,
                new HashMap<String, Object>()
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
