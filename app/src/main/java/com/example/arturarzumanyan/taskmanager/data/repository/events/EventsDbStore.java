package com.example.arturarzumanyan.taskmanager.data.repository.events;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.data.db.DbHelper;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.Specification;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.arturarzumanyan.taskmanager.networking.util.DateUtils.DAYS_IN_WEEK;

public class EventsDbStore {
    private Context mContext;

    public EventsDbStore(Context context) {
        this.mContext = context;
    }
/*
    public List<Event> getEvents() {
        return DbHelper.getDbHelper(mContext).getEvents();
    }

    public List<Event> getDailyEvents() {
        String date = DateUtils.getCurrentTime();
        return DbHelper.getDbHelper(mContext).getDailyEvents(date);
    }

    public List<Event> getEventsFromDate(Date eventDate) {
        String date = DateUtils.getEventDate(eventDate);
        return DbHelper.getDbHelper(mContext).getDailyEvents(date);
    }

    public List<Event> getWeeklyEvents() {
        int date = DateUtils.getEventWeek(DateUtils.getCurrentTime()) - 1;
        Date nextDate = DateUtils.getMondayDate(date - 1);

        List<Event> weeklyEvents = new ArrayList<>();
        for (int i = 0; i < DAYS_IN_WEEK; i++) {
            weeklyEvents.addAll(getEventsFromDate(nextDate));
            nextDate = DateUtils.getNextDate(nextDate);
        }

        return weeklyEvents;
    }

    public List<Event> getMonthlyEvents() {
        Date date = DateUtils.getFirstDateOfMonth();
        List<Event> monthlyEvents = new ArrayList<>();
        for (int i = 0; i < DateUtils.getDaysInCurrentMonth(); i++) {
            monthlyEvents.addAll(getEventsFromDate(date));
            date = DateUtils.getNextDate(date);
        }
        return monthlyEvents;
    }
*/
    public List<Event> getEvents(Specification specification) {
        int countOfDays = specification.getCountOfDays();
        Date nextDate = null;
        List<Event> events = new ArrayList<>();

        if (countOfDays == DAYS_IN_WEEK) {
            int date = DateUtils.getEventWeek(DateUtils.getCurrentTime()) - 1;
            nextDate = DateUtils.getMondayDate(date - 1);
        } else if (countOfDays > DAYS_IN_WEEK) {
            nextDate = DateUtils.getFirstDateOfMonth();
        } else if (countOfDays == 1) {
            return DbHelper.getDbHelper(mContext).getDailyEvents(specification.getStartDate());
        } else if (countOfDays == 0) {
            return DbHelper.getDbHelper(mContext).getEvents();
        }

        for (int i = 0; i < DateUtils.getDaysInCurrentMonth(); i++) {
            events.addAll(DbHelper.getDbHelper(mContext).getDailyEvents(DateUtils.getEventDate(nextDate)));
            nextDate = DateUtils.getNextDate(nextDate);
        }

        return events;
    }

    public void updateEvents(List<Event> eventList) {
        DbHelper.getDbHelper(mContext).updateEvents(eventList);
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
