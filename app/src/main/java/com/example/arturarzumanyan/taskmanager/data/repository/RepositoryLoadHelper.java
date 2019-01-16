package com.example.arturarzumanyan.taskmanager.data.repository;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.arturarzumanyan.taskmanager.networking.base.BaseHttpUrlConnection.JSON_CONTENT_TYPE_VALUE;
import static com.example.arturarzumanyan.taskmanager.networking.util.EventsParser.COLOR_ID_KEY;
import static com.example.arturarzumanyan.taskmanager.networking.util.EventsParser.DATETIME_KEY;
import static com.example.arturarzumanyan.taskmanager.networking.util.EventsParser.DESCRIPTION_KEY;
import static com.example.arturarzumanyan.taskmanager.networking.util.EventsParser.END_KEY;
import static com.example.arturarzumanyan.taskmanager.networking.util.EventsParser.OVERRIDES_KEY;
import static com.example.arturarzumanyan.taskmanager.networking.util.EventsParser.REMINDERS_KEY;
import static com.example.arturarzumanyan.taskmanager.networking.util.EventsParser.START_KEY;
import static com.example.arturarzumanyan.taskmanager.networking.util.EventsParser.SUMMARY_KEY;
import static com.example.arturarzumanyan.taskmanager.networking.util.TaskListsParser.TITLE_KEY;
import static com.example.arturarzumanyan.taskmanager.networking.util.TasksParser.COMPLETED_KEY;
import static com.example.arturarzumanyan.taskmanager.networking.util.TasksParser.DUE_KEY;
import static com.example.arturarzumanyan.taskmanager.networking.util.TasksParser.NOTES_KEY;
import static com.example.arturarzumanyan.taskmanager.networking.util.TasksParser.STATUS_KEY;

public class RepositoryLoadHelper {
    public static final String BASE_GOOGLE_APIS_URL = "https://www.googleapis.com/";
    public static final String AUTHORIZATION_KEY = "Authorization";
    private static final String CONTENT_TYPE_KEY = "Content-Type";
    private static final String NEEDS_ACTION_KEY = "needsAction";
    private static final String POPUP_KEY = "popup";
    private static final String METHOD_KEY = "method";
    private static final String USE_DEFAULT_KEY = "useDefault";
    private static final String MINUTES_KEY = "minutes";

    public RepositoryLoadHelper() {
    }

    public RequestParameters getEventCreateOrUpdateParameters(Event event,
                                                              String url,
                                                              FirebaseWebService.RequestMethods requestMethod) {
        Map<String, Object> requestBody = new HashMap<>();

        Map<String, String> startTimeMap = new HashMap<>();
        Map<String, String> endTimeMap = new HashMap<>();
        startTimeMap.put(DATETIME_KEY, DateUtils.formatEventTime(event.getStartTime()));
        endTimeMap.put(DATETIME_KEY, DateUtils.formatEventTime(event.getEndTime()));

        requestBody.put(SUMMARY_KEY, event.getName());

        if (!event.getDescription().isEmpty()) {
            requestBody.put(DESCRIPTION_KEY, event.getDescription());
        }

        requestBody.put(COLOR_ID_KEY, Integer.toString(event.getColorId()));

        requestBody.put(START_KEY, startTimeMap);
        requestBody.put(END_KEY, endTimeMap);

        if (event.getIsNotify() == 1) {
            Map<String, Object> remindersMap = new HashMap<>();
            List<Object> overrides = new ArrayList<>();

            Map<String, Object> overridesMap = new HashMap<>();
            overridesMap.put(METHOD_KEY, POPUP_KEY);
            overridesMap.put(MINUTES_KEY, 10);
            overrides.add(overridesMap);
            remindersMap.put(OVERRIDES_KEY, overrides);
            remindersMap.put(USE_DEFAULT_KEY, false);

            requestBody.put(REMINDERS_KEY, remindersMap);
        } else {
            Map<String, Object> remindersMap = new HashMap<>();
            remindersMap.put(USE_DEFAULT_KEY, false);
            requestBody.put(REMINDERS_KEY, remindersMap);
        }

        Map<String, String> requestHeaderParameters = new HashMap<>();

        requestHeaderParameters.put(CONTENT_TYPE_KEY, JSON_CONTENT_TYPE_VALUE);

        RequestParameters requestParameters = new RequestParameters(
                url,
                requestMethod,
                requestBody);
        requestParameters.setRequestHeaderParameters(requestHeaderParameters);

        return requestParameters;
    }

    public RequestParameters getTaskCreateOrUpdateParameters(Task task,
                                                             String url,
                                                             FirebaseWebService.RequestMethods requestMethod) {

        Map<String, Object> requestBody = new HashMap<>();

        requestBody.put(TITLE_KEY, task.getName());

        if (!task.getDescription().isEmpty()) {
            requestBody.put(NOTES_KEY, task.getDescription());
        }

        if (task.getDate() != null) {
            requestBody.put(DUE_KEY, DateUtils.formatTaskDate(task.getDate()));
        }

        if (task.getIsExecuted() == 1) {
            requestBody.put(STATUS_KEY, COMPLETED_KEY);
        } else {
            requestBody.put(STATUS_KEY, NEEDS_ACTION_KEY);
            if (requestMethod.equals(FirebaseWebService.RequestMethods.PATCH)) {
                requestBody.put(COMPLETED_KEY, null);
            }
        }

        Map<String, String> requestHeaderParameters = new HashMap<>();

        requestHeaderParameters.put(CONTENT_TYPE_KEY, JSON_CONTENT_TYPE_VALUE);

        RequestParameters requestParameters = new RequestParameters(
                url,
                requestMethod,
                requestBody);
        requestParameters.setRequestHeaderParameters(requestHeaderParameters);

        return requestParameters;
    }

    public RequestParameters getTaskListCreateOrUpdateParameters(TaskList taskList,
                                                                 String url,
                                                                 FirebaseWebService.RequestMethods requestMethod) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put(TITLE_KEY, taskList.getTitle());

        Map<String, String> requestHeaderParameters = new HashMap<>();

        requestHeaderParameters.put(CONTENT_TYPE_KEY, JSON_CONTENT_TYPE_VALUE);

        RequestParameters requestParameters = new RequestParameters(
                url,
                requestMethod,
                requestBody);
        requestParameters.setRequestHeaderParameters(requestHeaderParameters);

        return requestParameters;
    }

    public RequestParameters getDeleteParameters(String url) {
        RequestParameters requestParameters = new RequestParameters(
                url,
                FirebaseWebService.RequestMethods.DELETE,
                new HashMap<String, Object>()
        );

        requestParameters.setRequestHeaderParameters(new HashMap<String, String>());
        return requestParameters;
    }

    public static boolean isOnline() {
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
