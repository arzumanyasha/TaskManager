package com.example.arturarzumanyan.taskmanager.data.repository.events;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.arturarzumanyan.taskmanager.domain.Event;

import java.text.ParseException;
import java.util.ArrayList;

public class EventsRepository {
    private EventsDbStore eventsDbStore;
    private EventsCloudStore eventsCloudStore;

    public void loadEvents(Context context) {
        final Context tempContext = context;
        eventsCloudStore = new EventsCloudStore();
        eventsDbStore = new EventsDbStore();

        ArrayList<Event> events = new ArrayList<>();
        try {
            events = eventsDbStore.getEvents(tempContext);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if ((isOnline(context)) && (events.size() == 0)) {
            eventsCloudStore.getEvents(tempContext, new EventsCloudStore.OnTaskCompletedListener() {
                @Override
                public void onSuccess(ArrayList<Event> eventsList) {
                    ArrayList<Event> events = eventsList;
                    eventsDbStore.addEvents(tempContext, events);
                }

                @Override
                public void onfail() {

                }
            });
        }
    }

    private boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
