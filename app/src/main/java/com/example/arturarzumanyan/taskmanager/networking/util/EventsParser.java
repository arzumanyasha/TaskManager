package com.example.arturarzumanyan.taskmanager.networking.util;

import android.content.ContentValues;
import android.content.Context;

import com.example.arturarzumanyan.taskmanager.db.EventsContract.*;
import com.example.arturarzumanyan.taskmanager.db.EventsDbHelper;
import com.example.arturarzumanyan.taskmanager.domain.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EventsParser {

    public ArrayList<Event> parseEvents(String buffer) throws JSONException, ParseException {

        ArrayList<Event> eventsList = new ArrayList<>();
        JSONObject jsonobject = new JSONObject(buffer);
        JSONArray jsonArray = jsonobject.getJSONArray("items");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject explrObject = jsonArray.getJSONObject(i);
            eventsList.add(parseEvent(explrObject));
        }
        return eventsList;
    }

    private Event parseEvent(JSONObject jsonObject) throws JSONException, ParseException {

        String description;
        if (!jsonObject.isNull("description")) {
            description = jsonObject.getString("description");
        } else {
            description = "";
        }

        int colorId;
        if (!jsonObject.isNull("colorId")) {
            colorId = jsonObject.getInt("colorId");
        } else {
            colorId = 9;
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        JSONObject startTimeJsonObject = jsonObject.getJSONObject("start");
        JSONObject endTimeJsonObject = jsonObject.getJSONObject("end");

        Date startDate = dateFormat.parse(startTimeJsonObject.getString("dateTime"));

        Date endDate = dateFormat.parse(endTimeJsonObject.getString("dateTime"));

        Boolean isNotify;
        JSONObject reminderJsonObject = jsonObject.getJSONObject("reminders");
        if (!reminderJsonObject.isNull("overrides")) {
            isNotify = true;
        } else {
            isNotify = false;
        }

        Event event = new Event(jsonObject.getString("id"),
                jsonObject.getString("summary"),
                description,
                colorId,
                startDate,
                endDate,
                isNotify);

        return event;
    }
}
