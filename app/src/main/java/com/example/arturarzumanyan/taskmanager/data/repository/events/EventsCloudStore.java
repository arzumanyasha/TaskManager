package com.example.arturarzumanyan.taskmanager.data.repository.events;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.UserDataAsyncTask;
import com.example.arturarzumanyan.taskmanager.networking.util.EventsParser;

import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;

public class EventsCloudStore {
    private static final String BASE_EVENTS_URL = "https://www.googleapis.com/calendar/v3/calendars/";

    private UserDataAsyncTask mUserEventsAsyncTask;
    private UserDataAsyncTask mUserRefreshEventsAsyncTask;
    private ArrayList<Event> mEventsList;
    private RepositoryLoadHelper mRepositoryLoadHelper;
    private Context mContext;

    public EventsCloudStore(Context context) {
        this.mContext = context;
        mEventsList = new ArrayList<>();
        mUserRefreshEventsAsyncTask = new UserDataAsyncTask();
        mUserEventsAsyncTask = new UserDataAsyncTask();
        mRepositoryLoadHelper = new RepositoryLoadHelper(mContext);
    }

    public void getEvents(final OnTaskCompletedListener listener) {
        FirebaseWebService firebaseWebService = new FirebaseWebService();

        final String eventsUrl = BASE_EVENTS_URL + firebaseWebService.getCurrentUser().getEmail() + "/events";
        mRepositoryLoadHelper.requestUserData(mUserEventsAsyncTask, eventsUrl);

        mUserRefreshEventsAsyncTask.setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) throws JSONException, ParseException {
                EventsParser eventsParser = new EventsParser();
                mEventsList = eventsParser.parseEvents(response);
                listener.onSuccess(mEventsList);
            }
        });

        mUserEventsAsyncTask.setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) throws JSONException, ParseException {
                if (response.equals("")) {
                    FirebaseWebService firebaseWebService = new FirebaseWebService();
                    firebaseWebService.refreshAccessToken(mContext);
                    mRepositoryLoadHelper.requestUserData(mUserRefreshEventsAsyncTask, eventsUrl);
                } else {
                    EventsParser eventsParser = new EventsParser();
                    mEventsList = eventsParser.parseEvents(response);
                    listener.onSuccess(mEventsList);
                }
            }
        });
    }

    public interface OnTaskCompletedListener {
        void onSuccess(ArrayList<Event> eventsList);

        void onfail();
    }
}
