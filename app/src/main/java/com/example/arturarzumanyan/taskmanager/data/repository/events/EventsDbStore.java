package com.example.arturarzumanyan.taskmanager.data.repository.events;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.data.db.DbHelper;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.Specification;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;

import java.util.ArrayList;
import java.util.Collections;
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
        List<Event> events = new ArrayList<>();
        events.addAll(DbHelper.getDbHelper(mContext).getEvents(specification));
        return events;
    }

    public void updateEvents(List<Event> eventList) {
        DbHelper.getDbHelper(mContext).updateEvents(eventList);
    }

    public void addOrUpdateEvent(Event event) {
        DbHelper.getDbHelper(mContext).updateEvents(Collections.singletonList(event));
    }

    public void deleteEvent(Event event) {
        DbHelper.getDbHelper(mContext).deleteEvent(event);
    }
}
