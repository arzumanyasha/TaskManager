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

public class TasksParser {
    public ArrayList<Task> parseTasks(String buffer, int taskListId) throws JSONException, ParseException {

        ArrayList<Task> tasksList = new ArrayList<>();
        JSONObject jsonobject = new JSONObject(buffer);
        JSONArray jsonArray = jsonobject.getJSONArray("items");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject explrObject = jsonArray.getJSONObject(i);
            tasksList.add(parseTask(explrObject, taskListId));
        }
        return tasksList;
    }

    private Task parseTask(JSONObject jsonObject, int taskListId) throws JSONException, ParseException {

        String description;
        if (!jsonObject.isNull("notes")) {
            description = jsonObject.getString("notes");
        } else {
            description = "";
        }

        Boolean isExecuted;
        if (jsonObject.getString("status").equals("completed")) {
            isExecuted = true;
        } else {
            isExecuted = false;
        }

        Task task;

        if (!jsonObject.isNull("due")) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date date = dateFormat.parse(jsonObject.getString("due"));

            task = new Task(jsonObject.getString("id"),
                    jsonObject.getString("title"),
                    description,
                    isExecuted,
                    date,
                    taskListId);
        } else task = new Task(jsonObject.getString("id"),
                jsonObject.getString("title"),
                description,
                isExecuted,
                taskListId);

        return task;
    }
}
