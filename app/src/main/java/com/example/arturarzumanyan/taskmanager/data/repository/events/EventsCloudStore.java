package com.example.arturarzumanyan.taskmanager.data.repository.events;

import android.content.Context;
import android.os.AsyncTask;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.auth.TokenStorage;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.UserDataAsyncTask;
import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;
import com.example.arturarzumanyan.taskmanager.networking.util.EventsParser;

import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

public class EventsCloudStore {
    private static final String BASE_EVENTS_URL = "https://www.googleapis.com/calendar/v3/calendars/";
    private static final String AUTHORIZATION_KEY = "Authorization";

    private UserDataAsyncTask mUserEventsAsyncTask;
    private UserDataAsyncTask mUserRefreshEventsAsyncTask;
    private ArrayList<Event> mEventsList;

    public void getEvents(final Context context, final OnTaskCompletedListener listener) {
        FirebaseWebService firebaseWebService = new FirebaseWebService();
        mEventsList = new ArrayList<>();
        mUserRefreshEventsAsyncTask = new UserDataAsyncTask();
        mUserEventsAsyncTask = new UserDataAsyncTask();

        final String eventsUrl = BASE_EVENTS_URL + firebaseWebService.getCurrentUser().getEmail() + "/events";
        requestUserData(context, mUserEventsAsyncTask, eventsUrl);

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
                    firebaseWebService.refreshAccessToken(context);
                    requestUserData(context, mUserRefreshEventsAsyncTask, eventsUrl);
                } else {
                    EventsParser eventsParser = new EventsParser();
                    mEventsList = eventsParser.parseEvents(response);
                    listener.onSuccess(mEventsList);
                }
            }
        });
    }

    private void requestUserData(Context context, UserDataAsyncTask asyncTask, String url) {
        TokenStorage tokenStorage = new TokenStorage();

        FirebaseWebService.RequestMethods requestMethod = FirebaseWebService.RequestMethods.GET;
        HashMap<String, String> requestBodyParameters = new HashMap<>();
        HashMap<String, String> requestHeaderParameters = new HashMap<>();
        String token = tokenStorage.getAccessToken(context);
        requestHeaderParameters.put(AUTHORIZATION_KEY, "Bearer " + tokenStorage.getAccessToken(context));
        RequestParameters requestParameters = new RequestParameters(url,
                requestMethod,
                requestBodyParameters,
                requestHeaderParameters);
        asyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, requestParameters);
    }

    public interface OnTaskCompletedListener {
        void onSuccess(ArrayList<Event> eventsList);

        void onfail();
    }
}
