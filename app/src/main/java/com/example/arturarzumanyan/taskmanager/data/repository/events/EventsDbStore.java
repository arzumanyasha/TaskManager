package com.example.arturarzumanyan.taskmanager.data.repository.events;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.data.db.DbHelper;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.EventsSpecification;
import com.example.arturarzumanyan.taskmanager.domain.Event;

import java.util.List;

public class EventsDbStore {
    private Context mContext;

    public EventsDbStore(Context context) {
        this.mContext = context;
    }

    public List<Event> getEvents(EventsSpecification eventsSpecification) {
        return DbHelper.getDbHelper(mContext).getEvents(eventsSpecification);
    }

    public void addOrUpdateEvents(List<Event> eventList) {
        DbHelper.getDbHelper(mContext).addOrUpdateEvents(eventList);
    }

    public void deleteEvent(Event event) {
        DbHelper.getDbHelper(mContext).deleteEvent(event);
    }
}
