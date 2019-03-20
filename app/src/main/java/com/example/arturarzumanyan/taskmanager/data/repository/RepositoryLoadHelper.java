package com.example.arturarzumanyan.taskmanager.data.repository;

import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static final String AUTHORIZATION_KEY = "Authorization";
    public static final String TOKEN_TYPE = "Bearer ";
    public static final String CONTENT_TYPE_KEY = "Content-Type";
    public static final String JSON_CONTENT_TYPE_VALUE = "application/json";
    private static final String NEEDS_ACTION_KEY = "needsAction";
    private static final String POPUP_KEY = "popup";
    private static final String METHOD_KEY = "method";
    private static final String USE_DEFAULT_KEY = "useDefault";
    private static final String MINUTES_KEY = "minutes";

    public RepositoryLoadHelper() {
    }

    public Map<String, Object> getEventBodyParameters(Event event) {
        Map<String, Object> requestBody = new HashMap<>();

        Map<String, String> startTimeMap = new HashMap<>();
        Map<String, String> endTimeMap = new HashMap<>();
        startTimeMap.put(DATETIME_KEY, event.getStartTime());
        endTimeMap.put(DATETIME_KEY, event.getEndTime());

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
        }

        return requestBody;
    }

    public Map<String, Object> getTaskBodyParameters(Task task) {

        Map<String, Object> requestBody = new HashMap<>();

        requestBody.put(TITLE_KEY, task.getName());

        if (!task.getDescription().isEmpty()) {
            requestBody.put(NOTES_KEY, task.getDescription());
        }

        if (task.getDate() != null) {
            requestBody.put(DUE_KEY, task.getDate());
        }

        if (task.getIsExecuted() == 1) {
            requestBody.put(STATUS_KEY, COMPLETED_KEY);
        } else {
            requestBody.put(STATUS_KEY, NEEDS_ACTION_KEY);
            requestBody.put(COMPLETED_KEY, null);
        }

        return requestBody;
    }

    public Map<String, Object> getTaskListCreateOrUpdateParameters(TaskList taskList) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put(TITLE_KEY, taskList.getTitle());
        return requestBody;
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
