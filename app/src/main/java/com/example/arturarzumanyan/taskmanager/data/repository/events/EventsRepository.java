package com.example.arturarzumanyan.taskmanager.data.repository.events;

import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.EventsFromDateSpecification;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.EventsSpecification;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;
import com.example.arturarzumanyan.taskmanager.networking.util.EventsParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;

import io.reactivex.Single;
import okhttp3.ResponseBody;

public class EventsRepository {
    private EventsDbStore mEventsDbStore;
    private EventsCloudStore mEventsCloudStore;

    public EventsRepository() {
        mEventsCloudStore = new EventsCloudStore();
        mEventsDbStore = new EventsDbStore();
    }

    public Single<List<Event>> getEvents(EventsSpecification eventsSpecification) {
        Single<List<Event>> eventsSingle;
        if (RepositoryLoadHelper.isOnline()) {
            eventsSingle = mEventsCloudStore.getEventsFromServer(eventsSpecification)
                    .filter(response -> response != null).toSingle()
                    .flatMap(this::updateDbQuery)
                    .flatMap(aBoolean -> {
                        if (aBoolean) {
                            return mEventsDbStore.getEvents(eventsSpecification);
                        } else {
                            throw new IOException();
                        }
                    });
        } else {
            eventsSingle = mEventsDbStore.getEvents(eventsSpecification);
        }

        return eventsSingle;
    }

    private Single<Boolean> updateDbQuery(ResponseBody responseBody) throws IOException {
        EventsParser eventsParser = new EventsParser();
        List<Event> events = eventsParser.parseEvents(responseBody.string());
        return mEventsDbStore.addOrUpdateEvents(events);
    }

    public Single<List<Event>> addEvent(Event event) {
        EventsFromDateSpecification eventsFromDateSpecification = new EventsFromDateSpecification();
        eventsFromDateSpecification.setDate(/*DateUtils.formatEventDate(event.getStartTime())*/event.getStartTime());

        Single<List<Event>> eventsSingle;
        if (RepositoryLoadHelper.isOnline()) {
            eventsSingle = mEventsCloudStore.addEventOnServer(event)
                    .filter(response -> response != null).toSingle()
                    .map(this::parseEvent)
                    .flatMap(responseBody -> mEventsDbStore.addOrUpdateEvents(Collections.singletonList(event)))
                    .flatMap(aBoolean -> {
                        if (aBoolean) {
                            return mEventsDbStore.getEvents(eventsFromDateSpecification);
                        } else {
                            throw new IOException();
                        }
                    });
        } else {
            eventsSingle = mEventsDbStore.addOrUpdateEvents(Collections.singletonList(event))
                    .flatMap(aBoolean -> {
                        if (aBoolean) {
                            return mEventsDbStore.getEvents(eventsFromDateSpecification);
                        } else {
                            throw new IOException();
                        }
                    });
        }

        return eventsSingle;
    }

    public Single<List<Event>> updateEvent(Event event) {
        EventsFromDateSpecification eventsFromDateSpecification = new EventsFromDateSpecification();
        eventsFromDateSpecification.setDate(/*DateUtils.formatEventDate(event.getStartTime())*/event.getStartTime());

        Single<List<Event>> eventsSingle;
        if (RepositoryLoadHelper.isOnline()) {
            eventsSingle = mEventsCloudStore.updateEventOnServer(event)
                    .filter(response -> response != null).toSingle()
                    .map(this::parseEvent)
                    .flatMap(responseBody -> mEventsDbStore.addOrUpdateEvents(Collections.singletonList(event)))
                    .flatMap(aBoolean -> {
                        if (aBoolean) {
                            return mEventsDbStore.getEvents(eventsFromDateSpecification);
                        } else {
                            throw new IOException();
                        }
                    });
        } else {
            eventsSingle = mEventsDbStore.addOrUpdateEvents(Collections.singletonList(event))
                    .flatMap(aBoolean -> {
                        if (aBoolean) {
                            return mEventsDbStore.getEvents(eventsFromDateSpecification);
                        } else {
                            throw new IOException();
                        }
                    });
        }

        return eventsSingle;
    }

    private Event parseEvent(ResponseBody responseBody) throws IOException {
        Event event = null;
        if (responseBody != null) {
            EventsParser eventsParser = new EventsParser();
            event = eventsParser.parseEvent(responseBody.string());
        }
        return event;
    }

    public Single<List<Event>> deleteEvent(Event event) {
        EventsFromDateSpecification eventsFromDateSpecification = new EventsFromDateSpecification();
        eventsFromDateSpecification.setDate(DateUtils.getCurrentTime());

        Single<List<Event>> eventsSingle;
        if (RepositoryLoadHelper.isOnline()) {
            eventsSingle = mEventsCloudStore.deleteEventOnServer(event)
                    .filter(response -> {
                        if (response.code() == HttpURLConnection.HTTP_NO_CONTENT) {
                            return true;
                        } else {
                            throw new IOException();
                        }
                    }).toSingle()
                    .flatMap(responseBody -> mEventsDbStore.deleteEvent(event))
                    .flatMap(aBoolean -> {
                        if (aBoolean) {
                            return mEventsDbStore.getEvents(eventsFromDateSpecification);
                        } else {
                            throw new IOException();
                        }
                    });
        } else {
            eventsSingle = mEventsDbStore.deleteEvent(event)
                    .flatMap(aBoolean -> {
                        if (aBoolean) {
                            return mEventsDbStore.getEvents(eventsFromDateSpecification);
                        } else {
                            throw new IOException();
                        }
                    });
        }

        return eventsSingle;
    }
}
