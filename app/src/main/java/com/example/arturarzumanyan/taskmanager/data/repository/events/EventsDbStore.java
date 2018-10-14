package com.example.arturarzumanyan.taskmanager.data.repository.events;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.data.db.SQLiteDbHelper;
import com.example.arturarzumanyan.taskmanager.domain.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
        Calendar c = Calendar.getInstance();
        Date currentDate = c.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDateFormat.format(currentDate);
        sqliteDbHelper = new SQLiteDbHelper(mContext);
        return sqliteDbHelper.getDailyEvents(date);
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
