package com.example.arturarzumanyan.taskmanager.networking.util;

import android.content.ContentValues;
import android.content.Context;

import com.example.arturarzumanyan.taskmanager.db.EventsContract.*;
import com.example.arturarzumanyan.taskmanager.db.EventsDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EventsParser {
    private Context mContext;

    public void storeEvents(Context context, String buffer) throws JSONException {
        mContext = context;
        JSONObject jsonobject = new JSONObject(buffer);
        JSONArray jsonArray = jsonobject.getJSONArray("items");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject explrObject = jsonArray.getJSONObject(i);
            storeEvent(explrObject);
        }
    }

    private void storeEvent(JSONObject jsonObject) throws JSONException {
        ContentValues cv = new ContentValues();
        cv.put(EventsTable.COLUMN_EVENT_ID, jsonObject.getString("id"));
        cv.put(EventsTable.COLUMN_NAME, jsonObject.getString("summary"));

        if (!jsonObject.isNull("description")) {
            cv.put(EventsTable.COLUMN_DESCRIPTION, jsonObject.getString("description"));
        } else {
            cv.put(EventsTable.COLUMN_DESCRIPTION, "");
        }

        if (!jsonObject.isNull("colorId")) {
            cv.put(EventsTable.COLUMN_COLOR_ID, jsonObject.getInt("colorId"));
        } else {
            cv.put(EventsTable.COLUMN_COLOR_ID, 9);
        }

        JSONObject startTimeJsonObject = jsonObject.getJSONObject("start");
        cv.put(EventsTable.COLUMN_START_TIME, startTimeJsonObject.getString("dateTime"));

        JSONObject endTimeJsonObject = jsonObject.getJSONObject("end");
        cv.put(EventsTable.COLUMN_END_TIME, endTimeJsonObject.getString("dateTime"));

        JSONObject reminderJsonObject = jsonObject.getJSONObject("reminders");
        if (!reminderJsonObject.isNull("overrides")) {
            cv.put(EventsTable.COLUMN_REMINDER, 1);
        } else {
            cv.put(EventsTable.COLUMN_REMINDER, 0);
        }

        EventsDbHelper eventsDbHelper = new EventsDbHelper(mContext);
        eventsDbHelper.insertEvent(cv);
    }
}
