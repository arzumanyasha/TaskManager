package com.example.arturarzumanyan.taskmanager.data.repository.events;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.data.db.SQLiteDbHelper;
import com.example.arturarzumanyan.taskmanager.domain.Event;

import java.text.ParseException;
import java.util.ArrayList;

public class EventsDbRepository {
    private SQLiteDbHelper sqliteDbHelper;

    public ArrayList<Event> getEvents(Context context) throws ParseException {
        sqliteDbHelper = new SQLiteDbHelper(context);
        return sqliteDbHelper.getEvents();
    }

    public ArrayList<Event> getDailyEvents() {
        return null;
    }

    public ArrayList<Event> getWeeklyEvents() {
        return null;
    }

    public ArrayList<Event> getMonthlyEvents() {
        return null;
    }

    public void addEvents(Context context, ArrayList<Event> eventList) {
        sqliteDbHelper = new SQLiteDbHelper(context);
        sqliteDbHelper.insertEvents(eventList);
    }

    public void addEvent(Event event) {

    }

    public void updateEvent(Event event) {

    }

    public void deleteEvent(Event event) {

    }
}
