package com.example.arturarzumanyan.taskmanager.networking.util;

import com.example.arturarzumanyan.taskmanager.domain.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventsParser {
    private static final String ITEMS_KEY = "items";
    public static final String DESCRIPTION_KEY = "description";
    public static final String COLOR_ID_KEY = "colorId";
    public static final String START_KEY = "start";
    public static final String END_KEY = "end";
    public static final String DATETIME_KEY = "dateTime";
    public static final String REMINDERS_KEY = "reminders";
    public static final String OVERRIDES_KEY = "overrides";
    private static final String ID_KEY = "id";
    public static final String SUMMARY_KEY = "summary";

    public List<Event> parseEvents(String buffer) {

        List<Event> eventsList = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(buffer);
            JSONArray jsonArray = jsonobject.getJSONArray(ITEMS_KEY);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject explrObject = jsonArray.getJSONObject(i);
                eventsList.add(parseEvent(explrObject));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return eventsList;
    }

    private Event parseEvent(JSONObject jsonObject) throws JSONException {

        String description;
        if (!jsonObject.isNull(DESCRIPTION_KEY)) {
            description = jsonObject.getString(DESCRIPTION_KEY);
        } else {
            description = "";
        }

        int colorId;
        if (!jsonObject.isNull(COLOR_ID_KEY)) {
            colorId = jsonObject.getInt(COLOR_ID_KEY);
        } else {
            colorId = 9;
        }

        JSONObject startTimeJsonObject = jsonObject.getJSONObject(START_KEY);
        JSONObject endTimeJsonObject = jsonObject.getJSONObject(END_KEY);

        Date startDate = DateUtils.getEventDateFromString(startTimeJsonObject.getString(DATETIME_KEY));

        Date endDate = DateUtils.getEventDateFromString(endTimeJsonObject.getString(DATETIME_KEY));

        int isNotify;
        JSONObject reminderJsonObject = jsonObject.getJSONObject(REMINDERS_KEY);
        if (!reminderJsonObject.isNull(OVERRIDES_KEY)) {
            isNotify = 1;
        } else {
            isNotify = 0;
        }

        return new Event(jsonObject.getString(ID_KEY),
                jsonObject.getString(SUMMARY_KEY),
                description,
                colorId,
                startDate,
                endDate,
                isNotify);
    }

    public Event parseEvent(String buffer) {
        try {
            JSONObject jsonobject = new JSONObject(buffer);
            return parseEvent(jsonobject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
