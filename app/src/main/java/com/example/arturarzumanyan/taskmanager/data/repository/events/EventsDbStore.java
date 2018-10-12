package com.example.arturarzumanyan.taskmanager.data.repository.events;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.data.db.SQLiteDbHelper;
import com.example.arturarzumanyan.taskmanager.domain.Event;

import java.text.ParseException;
import java.util.ArrayList;

public class EventsDbStore {
    private SQLiteDbHelper sqliteDbHelper;
    private Context mContext;

    public EventsDbStore(Context context) {
        this.mContext = context;
    }

    public ArrayList<Event> getEvents() {
        sqliteDbHelper = new SQLiteDbHelper(mContext);
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

    public void addEvents(ArrayList<Event> eventList) {
        sqliteDbHelper = new SQLiteDbHelper(mContext);
        sqliteDbHelper.insertEvents(eventList);
    }

    public void addEvent(Event event) {

    }

    public void updateEvent(Event event) {

    }

    public void deleteEvent(Event event) {

    }
}
