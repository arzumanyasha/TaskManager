package com.example.arturarzumanyan.taskmanager.networking.util;

import com.example.arturarzumanyan.taskmanager.domain.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import static com.example.arturarzumanyan.taskmanager.networking.util.TaskListsParser.ID_KEY;
import static com.example.arturarzumanyan.taskmanager.networking.util.TaskListsParser.ITEMS_KEY;
import static com.example.arturarzumanyan.taskmanager.networking.util.TaskListsParser.TITLE_KEY;

public class TasksParser {
    public static final String NOTES_KEY = "notes";
    public static final String STATUS_KEY = "status";
    public static final String COMPLETED_KEY = "completed";
    public static final String DUE_KEY = "due";

    public ArrayList<Task> parseTasks(String buffer, int taskListId) {

        ArrayList<Task> tasksList = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(buffer);
            JSONArray jsonArray = jsonobject.getJSONArray(ITEMS_KEY);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject explrObject = jsonArray.getJSONObject(i);
                tasksList.add(parseTask(explrObject, taskListId));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tasksList;
    }

    private Task parseTask(JSONObject jsonObject, int taskListId) throws JSONException {

        String description;
        if (!jsonObject.isNull(NOTES_KEY)) {
            description = jsonObject.getString(NOTES_KEY);
        } else {
            description = "";
        }

        int isExecuted;
        if (jsonObject.getString(STATUS_KEY).equals(COMPLETED_KEY)) {
            isExecuted = 1;
        } else {
            isExecuted = 0;
        }

        Task task;

        String id = jsonObject.getString(ID_KEY);
        String name = jsonObject.getString(TITLE_KEY);

        if (!jsonObject.isNull(DUE_KEY)) {
            Date date = DateUtils.getTaskDateFromString(jsonObject.getString(DUE_KEY));

            task = new Task(id,
                    name,
                    description,
                    isExecuted,
                    taskListId,
                    date);
        } else {
            task = new Task(id,
                    name,
                    description,
                    isExecuted,
                    taskListId);
        }

        return task;
    }

    public Task parseTask(String buffer, int taskListId) {
        try {
            JSONObject jsonobject = new JSONObject(buffer);
            return parseTask(jsonobject, taskListId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
