package com.example.arturarzumanyan.taskmanager.data.repository.events;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.domain.Event;

import java.util.ArrayList;

public class EventsRepository {
    private EventsDbStore eventsDbStore;
    private EventsCloudStore eventsCloudStore;
    private Context mContext;
    private RepositoryLoadHelper mRepositoryLoadHelper;

    public EventsRepository(Context context) {
        this.mContext = context;
    }

    public void loadEvents(final OnEventsLoadedListener listener) {
        eventsCloudStore = new EventsCloudStore(mContext);
        eventsDbStore = new EventsDbStore(mContext);
        mRepositoryLoadHelper = new RepositoryLoadHelper(mContext);

        ArrayList<Event> events = eventsDbStore.getEvents();

        if ((mRepositoryLoadHelper.isOnline()) && (events.size() == 0)) {
            eventsCloudStore.getEvents(new EventsCloudStore.OnTaskCompletedListener() {
                @Override
                public void onSuccess(ArrayList<Event> eventsList) {
                    listener.onSuccess(eventsList);
                    eventsDbStore.addEvents(eventsList);
                }

                @Override
                public void onfail() {

                }
            });
        } else if ((mRepositoryLoadHelper.isOnline()) && (events.size() != 0)) {
            listener.onSuccess(events);
        }
    }

    public ArrayList<Event> getDailyEvents() {
        eventsDbStore = new EventsDbStore(mContext);
        return eventsDbStore.getDailyEvents();
    }

    public void addEvent() {
    }

    public void updateEvent() {
    }

    public void deleteEvent() {
    }

    public interface OnEventsLoadedListener {
        void onSuccess(ArrayList<Event> eventsList);

        void onfail();
    }
}
