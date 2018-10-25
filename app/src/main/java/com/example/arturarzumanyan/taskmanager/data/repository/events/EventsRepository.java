package com.example.arturarzumanyan.taskmanager.data.repository.events;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.domain.Event;

import java.util.ArrayList;
import java.util.Date;

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
                public void onFail() {

                }
            });
        } else if ((mRepositoryLoadHelper.isOnline()) && (events.size() != 0)) {
            listener.onSuccess(events);
        }
    }

    public ArrayList<Event> getDailyEvents() {
        return mEventsDbStore.getDailyEvents();
    }

    public ArrayList<Event> getEventsFromDate(Date date){
        return mEventsDbStore.getEventsFromDate(date);
    }

    public void addEvent(Event event, final EventsCloudStore.OnTaskCompletedListener listener) {
        if (mRepositoryLoadHelper.isOnline()) {
            mEventsCloudStore.addEvent(event, listener);
        } else {
            mEventsDbStore.addEvent(event);
        }
    }

    public void updateEvent(Event event, final EventsCloudStore.OnTaskCompletedListener listener) {
        if (mRepositoryLoadHelper.isOnline()) {
            mEventsCloudStore.updateEvent(event, listener);
        } else {
            mEventsDbStore.updateEvent(event);
        }
    }

    public void deleteEvent(Event event) {
        if (mRepositoryLoadHelper.isOnline()) {
            mEventsCloudStore.deleteEvent(event);
        } else {
            mEventsDbStore.deleteEvent(event);
        }
    }

    public interface OnEventsLoadedListener {
        void onSuccess(ArrayList<Event> eventsList);

        void onFail();
    }
}
