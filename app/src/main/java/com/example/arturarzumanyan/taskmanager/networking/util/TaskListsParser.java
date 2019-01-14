package com.example.arturarzumanyan.taskmanager.networking.util;

import com.example.arturarzumanyan.taskmanager.domain.TaskList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TaskListsParser {
    static final String ITEMS_KEY = "items";
    static final String ID_KEY = "id";
    public static final String TITLE_KEY = "title";

    public ArrayList<TaskList> parseTaskLists(String buffer) {

        ArrayList<TaskList> taskListArrayList = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(buffer);
            JSONArray jsonArray = jsonobject.getJSONArray(ITEMS_KEY);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject explrObject = jsonArray.getJSONObject(i);
                taskListArrayList.add(parseTaskList(explrObject));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return taskListArrayList;
    }

    private TaskList parseTaskList(JSONObject jsonObject) throws JSONException {
        return new TaskList(jsonObject.getString(ID_KEY),
                jsonObject.getString(TITLE_KEY));
    }

    public TaskList parseTaskList(String buffer) {
        try {
            JSONObject jsonobject = new JSONObject(buffer);
            return parseTaskList(jsonobject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
