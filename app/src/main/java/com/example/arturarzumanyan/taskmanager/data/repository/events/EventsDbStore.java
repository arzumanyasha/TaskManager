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
        mSqliteDbHelper = new SQLiteDbHelper(mContext);
    }

    public ArrayList<Event> getEvents() {
        return mSqliteDbHelper.getEvents();
    }

    public ArrayList<Event> getDailyEvents() {
        String date = DateUtils.getCurrentTime();
        return mSqliteDbHelper.getDailyEvents(date);
    }

    public ArrayList<Event> getEventsFromDate(Date eventDate) {
        String date = DateUtils.getEventDate(eventDate);
        return mSqliteDbHelper.getDailyEvents(date);
    }

    public void addEvents(ArrayList<Event> eventList) {
        mSqliteDbHelper.insertEvents(eventList);
    }

    public void addEvent(Event event) {
        mSqliteDbHelper.insertEvent(event);
    }

    public void updateEvent(Event event) {
        mSqliteDbHelper.updateEvent(event);
    }

    public void deleteEvent(Event event) {
        mSqliteDbHelper.deleteEvent(event);
    }
}
