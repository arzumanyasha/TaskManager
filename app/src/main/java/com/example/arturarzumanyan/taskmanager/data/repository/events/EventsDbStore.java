package com.example.arturarzumanyan.taskmanager.data.repository.events;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.data.db.DbHelper;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;

import java.util.ArrayList;
import java.util.Date;

import static com.example.arturarzumanyan.taskmanager.networking.util.DateUtils.DAYS_IN_WEEK;

public class EventsDbStore {
    private Context mContext;

    public EventsDbStore(Context context) {
        this.mContext = context;
    }

    public ArrayList<Event> getEvents() {
        return DbHelper.getDbHelper(mContext).getEvents();
    }

    public ArrayList<Event> getDailyEvents() {
        String date = DateUtils.getCurrentTime();
        return DbHelper.getDbHelper(mContext).getDailyEvents(date);
    }

    public ArrayList<Event> getEventsFromDate(Date eventDate) {
        String date = DateUtils.getEventDate(eventDate);
        return DbHelper.getDbHelper(mContext).getDailyEvents(date);
    }

    public ArrayList<Event> getWeeklyEvents() {
        int date = DateUtils.getEventWeek(DateUtils.getCurrentTime()) - 1;
        Date nextDate = DateUtils.getMondayDate(date - 1);

        ArrayList<Event> weeklyEvents = new ArrayList<>();
        for (int i = 0; i < DAYS_IN_WEEK; i++) {
            weeklyEvents.addAll(getEventsFromDate(nextDate));
            nextDate = DateUtils.getNextDate(nextDate);
        }

        return weeklyEvents;
    }

    public ArrayList<Event> getMonthlyEvents() {
        Date date = DateUtils.getFirstDateOfMonth();
        ArrayList<Event> monthlyEvents = new ArrayList<>();
        for (int i = 0; i < DateUtils.getDaysInCurrentMonth(); i++) {
            monthlyEvents.addAll(getEventsFromDate(date));
            date = DateUtils.getNextDate(date);
        }
        return monthlyEvents;
    }

    public void addEvents(ArrayList<Event> eventList) {
        DbHelper.getDbHelper(mContext).insertEvents(eventList);
    }

    public void addEvent(Event event) {
        DbHelper.getDbHelper(mContext).insertEvent(event);
    }

    public void updateEvent(Event event) {
        DbHelper.getDbHelper(mContext).updateEvent(event);
    }

    public void deleteEvent(Event event) {
        DbHelper.getDbHelper(mContext).deleteEvent(event);
    }
}
