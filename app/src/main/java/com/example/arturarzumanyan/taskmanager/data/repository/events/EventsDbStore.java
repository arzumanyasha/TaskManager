package com.example.arturarzumanyan.taskmanager.data.repository.events;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.data.db.SQLiteDbHelper;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EventsDbStore {
    private SQLiteDbHelper mSqliteDbHelper;
    private Context mContext;

    public EventsDbStore(Context context) {
        this.mContext = context;
    }

    public ArrayList<Event> getEvents() {
        mSqliteDbHelper = new SQLiteDbHelper(mContext);
        return mSqliteDbHelper.getEvents();
    }

    public ArrayList<Event> getDailyEvents() {
        String date = DateUtils.getCurrentTime();
        mSqliteDbHelper = new SQLiteDbHelper(mContext);
        return mSqliteDbHelper.getDailyEvents(date);
    }

    public ArrayList<Event> getWeeklyEvents() {
        return null;
    }

    public ArrayList<Event> getMonthlyEvents() {
        return null;
    }

    public void addEvents(ArrayList<Event> eventList) {
        mSqliteDbHelper = new SQLiteDbHelper(mContext);
        mSqliteDbHelper.insertEvents(eventList);
    }

    public void addEvent(Event event) {

    }

    public void updateEvent(Event event) {

    }

    public void deleteEvent(Event event) {

    }
}
