package com.example.arturarzumanyan.taskmanager.data.repository.events;

import com.example.arturarzumanyan.taskmanager.data.db.DbHelper;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.EventsSpecification;
import com.example.arturarzumanyan.taskmanager.domain.Event;

import java.util.List;

import io.reactivex.Single;

public class EventsDbStore {

    EventsDbStore() {
    }

    public Single<List<Event>> getEvents(EventsSpecification eventsSpecification) {
        return Single.fromCallable(() -> DbHelper.getDbHelperInstance().getEvents(eventsSpecification));
    }

    public Single<Boolean> addOrUpdateEvents(List<Event> eventList) {
        return Single.fromCallable(() -> DbHelper.getDbHelperInstance().addOrUpdateEvents(eventList));
    }

    public Single<Boolean> deleteEvent(Event event) {
        return Single.fromCallable(() -> DbHelper.getDbHelperInstance().deleteEvent(event));
    }
}
