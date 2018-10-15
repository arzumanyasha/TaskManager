package com.example.arturarzumanyan.taskmanager.data.repository.events;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.domain.Event;

import java.util.ArrayList;

public class EventsRepository {
    private EventsDbStore mEventsDbStore;
    private EventsCloudStore mEventsCloudStore;
    private Context mContext;
    private RepositoryLoadHelper mRepositoryLoadHelper;

    public EventsRepository(Context context) {
        this.mContext = context;
        mEventsCloudStore = new EventsCloudStore(mContext);
        mEventsDbStore = new EventsDbStore(mContext);
        mRepositoryLoadHelper = new RepositoryLoadHelper(mContext);
    }

    public void loadEvents(final OnEventsLoadedListener listener) {
        ArrayList<Event> events = mEventsDbStore.getEvents();

        if ((mRepositoryLoadHelper.isOnline()) && (events.size() == 0)) {
            mEventsCloudStore.getEvents(new EventsCloudStore.OnTaskCompletedListener() {
                @Override
                public void onSuccess(ArrayList<Event> eventsList) {
                    listener.onSuccess(eventsList);
                    mEventsDbStore.addEvents(eventsList);
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
        return mEventsDbStore.getDailyEvents();
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
