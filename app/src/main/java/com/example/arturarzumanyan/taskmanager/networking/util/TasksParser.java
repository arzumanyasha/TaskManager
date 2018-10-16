package com.example.arturarzumanyan.taskmanager.networking.util;

import com.example.arturarzumanyan.taskmanager.domain.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.arturarzumanyan.taskmanager.networking.util.TaskListsParser.ID_KEY;
import static com.example.arturarzumanyan.taskmanager.networking.util.TaskListsParser.ITEMS_KEY;
import static com.example.arturarzumanyan.taskmanager.networking.util.TaskListsParser.TITLE_KEY;

public class TasksParser {
    private static final String NOTES_KEY = "notes";
    private static final String STATUS_KEY = "status";
    private static final String COMPLETED_KEY = "completed";
    private static final String DUE_KEY = "due";

    public ArrayList<Task> parseTasks(String buffer, int taskListId) throws JSONException, ParseException {

        ArrayList<Task> tasksList = new ArrayList<>();
        JSONObject jsonobject = new JSONObject(buffer);
        JSONArray jsonArray = jsonobject.getJSONArray(ITEMS_KEY);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject explrObject = jsonArray.getJSONObject(i);
            tasksList.add(parseTask(explrObject, taskListId));
        }
        return tasksList;
    }

    private Task parseTask(JSONObject jsonObject, int taskListId) throws JSONException, ParseException {

        String description;
        if (!jsonObject.isNull(NOTES_KEY)) {
            description = jsonObject.getString(NOTES_KEY);
        } else {
            description = "";
        }

        Boolean isExecuted;
        if (jsonObject.getString(STATUS_KEY).equals(COMPLETED_KEY)) {
            isExecuted = true;
        } else {
            isExecuted = false;
        }

        Task task;

        if (!jsonObject.isNull(DUE_KEY)) {
            Date date = DateUtils.getTaskDateFromString(jsonObject.getString(DUE_KEY));

            task = new Task(jsonObject.getString(ID_KEY),
                    jsonObject.getString(TITLE_KEY),
                    description,
                    isExecuted,
                    taskListId,
                    date);
        } else {
            task = new Task(jsonObject.getString(ID_KEY),
                    jsonObject.getString(TITLE_KEY),
                    description,
                    isExecuted,
                    taskListId);
        }

        return task;
    }
}
