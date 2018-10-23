package com.example.arturarzumanyan.taskmanager.data.repository.events;

import android.content.Context;
import android.os.AsyncTask;

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
    private EventsDbStore mEventsDbStore;
    private ArrayList<Event> mEventsList;
    private RepositoryLoadHelper mRepositoryLoadHelper;
    private FirebaseWebService mFirebaseWebService;
    private Context mContext;

    public EventsCloudStore(Context context) {
        this.mContext = context;
        mEventsList = new ArrayList<>();
        mUserEventsAsyncTask = new UserDataAsyncTask();
        mRepositoryLoadHelper = new RepositoryLoadHelper(mContext);
        mFirebaseWebService = new FirebaseWebService();
        mEventsDbStore = new EventsDbStore(mContext);
    }

    public void getEvents(final OnTaskCompletedListener listener) {


        final String eventsUrl = BASE_EVENTS_URL + mFirebaseWebService.getCurrentUser().getEmail() + "/events";
        mRepositoryLoadHelper.requestUserData(mUserEventsAsyncTask, eventsUrl);

        mUserEventsAsyncTask.setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) {
                if (response.equals("")) {
                    FirebaseWebService firebaseWebService = new FirebaseWebService();
                    firebaseWebService.refreshAccessToken(mContext, new FirebaseWebService.AccessTokenUpdatedListener() {
                        @Override
                        public void onAccessTokenUpdated() {
                            UserDataAsyncTask updatedUserDataAsyncTask = new UserDataAsyncTask();
                            mRepositoryLoadHelper.requestUserData(updatedUserDataAsyncTask, eventsUrl);
                            updatedUserDataAsyncTask.setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
                                @Override
                                public void onDataLoaded(String response) {
                                    EventsParser eventsParser = new EventsParser();
                                    mEventsList = eventsParser.parseEvents(response);
                                    listener.onSuccess(mEventsList);
                                }
                            });
                        }
                    });
                } else {
                    EventsParser eventsParser = new EventsParser();
                    mEventsList = eventsParser.parseEvents(response);
                    listener.onSuccess(mEventsList);
                }
            }
        });
    }

    public void addEvent(Event event, final OnTaskCompletedListener listener) {
        final String url = BASE_EVENTS_URL +
                mFirebaseWebService.getCurrentUser().getEmail() +
                "/events";

        sendRequest(event, url, FirebaseWebService.RequestMethods.POST, listener);
    }

    public void updateEvent(Event event, final OnTaskCompletedListener listener) {
        final String url = BASE_EVENTS_URL +
                mFirebaseWebService.getCurrentUser().getEmail() +
                "/events/" +
                event.getId();

        sendRequest(event, url, FirebaseWebService.RequestMethods.PATCH, listener);
    }

    private void sendRequest(final Event event,
                             final String url,
                             final FirebaseWebService.RequestMethods requestMethod,
                             final OnTaskCompletedListener listener) {
        UserDataAsyncTask userDataAsyncTask = new UserDataAsyncTask();
        userDataAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
                mRepositoryLoadHelper.getEventCreateOrUpdateParameters(event, url, requestMethod));

        userDataAsyncTask.setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) {
                if (!response.equals("")) {
                    createOrUpdateEventInDb(response, requestMethod);
                    listener.onSuccess(mEventsDbStore.getEvents());
                } else {
                    mFirebaseWebService.refreshAccessToken(mContext, new FirebaseWebService.AccessTokenUpdatedListener() {
                        @Override
                        public void onAccessTokenUpdated() {
                            UserDataAsyncTask updatedUserDataAsyncTask = new UserDataAsyncTask();
                            updatedUserDataAsyncTask.setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
                                @Override
                                public void onDataLoaded(String response) {
                                    createOrUpdateEventInDb(response, requestMethod);
                                    listener.onSuccess(mEventsDbStore.getEvents());
                                }
                            });
                            updatedUserDataAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
                                    mRepositoryLoadHelper.getEventCreateOrUpdateParameters(event, url, requestMethod));
                        }
                    });
                }
            }
        });
    }

    private void createOrUpdateEventInDb(String response,
                                         FirebaseWebService.RequestMethods requestMethod) {
        EventsParser eventsParser = new EventsParser();
        if (requestMethod == FirebaseWebService.RequestMethods.POST) {
            mEventsDbStore.addEvent(eventsParser.parseEvent(response));
        } else if (requestMethod == FirebaseWebService.RequestMethods.PATCH) {
            mEventsDbStore.updateEvent(eventsParser.parseEvent(response));
        }
    }


    public void deleteEvent(final Event event) {
        final String url = BASE_EVENTS_URL +
                mFirebaseWebService.getCurrentUser().getEmail() +
                "/events/" +
                event.getId();

        UserDataAsyncTask userDataAsyncTask = new UserDataAsyncTask();

        userDataAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
                mRepositoryLoadHelper.getDeleteParameters(url));

        userDataAsyncTask.setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) {
                if (response.equals("")) {
                    mFirebaseWebService.refreshAccessToken(mContext, new FirebaseWebService.AccessTokenUpdatedListener() {
                        @Override
                        public void onAccessTokenUpdated() {
                            UserDataAsyncTask updatedUserDataAsyncTask = new UserDataAsyncTask();

                            updatedUserDataAsyncTask.setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
                                @Override
                                public void onDataLoaded(String response) {
                                    mEventsDbStore.deleteEvent(event);
                                }
                            });

                            updatedUserDataAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
                                    mRepositoryLoadHelper.getDeleteParameters(url));
                        }
                    });
                } else if (response.equals("ok")) {
                    mEventsDbStore.deleteEvent(event);
                }
            }
        });
    }

    public interface OnTaskCompletedListener {
        void onSuccess(ArrayList<Event> eventsList);

        void onfail();
    }
}
