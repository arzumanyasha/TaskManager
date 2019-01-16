package com.example.arturarzumanyan.taskmanager.data.repository.events;

import com.example.arturarzumanyan.taskmanager.data.db.DbHelper;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.EventsSpecification;
import com.example.arturarzumanyan.taskmanager.domain.Event;

import java.util.List;

public class EventsDbStore {

    EventsDbStore() {
    }

    public List<Event> getEvents(EventsSpecification eventsSpecification) {
        return DbHelper.getDbHelperInstance().getEvents(eventsSpecification);
    }

    public void addOrUpdateEvents(List<Event> eventList) {
        DbHelper.getDbHelperInstance().addOrUpdateEvents(eventList);
    }

    public void deleteEvent(Event event) {
        DbHelper.getDbHelperInstance().deleteEvent(event);
    }
}
