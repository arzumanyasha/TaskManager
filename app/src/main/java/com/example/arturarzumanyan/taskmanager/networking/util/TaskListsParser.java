package com.example.arturarzumanyan.taskmanager.networking.util;

import com.example.arturarzumanyan.taskmanager.domain.TaskList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TaskListsParser {
    public ArrayList<TaskList> parseTaskLists(String buffer) throws JSONException {

        ArrayList<TaskList> taskListArrayList = new ArrayList<>();
        JSONObject jsonobject = new JSONObject(buffer);
        JSONArray jsonArray = jsonobject.getJSONArray("items");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject explrObject = jsonArray.getJSONObject(i);
            taskListArrayList.add(parseTaskLists(explrObject));
        }
        return taskListArrayList;
    }

    private TaskList parseTaskLists(JSONObject jsonObject) throws JSONException {

        TaskList taskList = new TaskList(jsonObject.getString("id"),
                jsonObject.getString("title"));

        return taskList;
    }
}
