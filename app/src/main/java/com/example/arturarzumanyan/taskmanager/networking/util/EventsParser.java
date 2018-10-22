package com.example.arturarzumanyan.taskmanager.networking.util;

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
    private static final String ITEMS_KEY = "items";
    private static final String DESCRIPTION_KEY = "description";
    private static final String COLOR_ID_KEY = "colorId";
    private static final String START_KEY = "start";
    private static final String END_KEY = "end";
    private static final String DATETIME_KEY = "dateTime";
    private static final String REMINDERS_KEY = "reminders";
    private static final String OVERRIDES_KEY = "overrides";
    private static final String ID_KEY = "id";
    private static final String SUMMARY_KEY = "summary";

    public ArrayList<Event> parseEvents(String buffer) {

        ArrayList<Event> eventsList = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(buffer);
            JSONArray jsonArray = jsonobject.getJSONArray(ITEMS_KEY);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject explrObject = jsonArray.getJSONObject(i);
                eventsList.add(parseEvent(explrObject));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return eventsList;
    }

    private Event parseEvent(JSONObject jsonObject) throws JSONException, ParseException {

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

        Boolean isNotify;
        JSONObject reminderJsonObject = jsonObject.getJSONObject(REMINDERS_KEY);
        if (!reminderJsonObject.isNull(OVERRIDES_KEY)) {
            isNotify = true;
        } else {
            isNotify = false;
        }

        Event event = new Event(jsonObject.getString(ID_KEY),
                jsonObject.getString(SUMMARY_KEY),
                description,
                colorId,
                startDate,
                endDate,
                isNotify);

        return event;
    }
}
