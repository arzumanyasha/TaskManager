package com.example.arturarzumanyan.taskmanager.data.repository.events;

import android.content.Context;
import android.os.AsyncTask;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.data.repository.BaseDataLoadingAsyncTask;
import com.example.arturarzumanyan.taskmanager.networking.util.EventsParser;

import java.util.ArrayList;

public class EventsCloudStore {
    public static final String BASE_EVENTS_URL = "https://www.googleapis.com/calendar/v3/calendars/";

    private BaseDataLoadingAsyncTask mUserEventsAsyncTask;
    private EventsDbStore mEventsDbStore;
    private ArrayList<Event> mEventsList;
    private EventsParser mEventsParser;
    private RepositoryLoadHelper mRepositoryLoadHelper;
    private FirebaseWebService mFirebaseWebService;
    private Context mContext;

    public EventsCloudStore(Context context) {
        this.mContext = context;
        mEventsList = new ArrayList<>();
        //mUserEventsAsyncTask = new BaseDataLoadingAsyncTask();
        mRepositoryLoadHelper = new RepositoryLoadHelper(mContext);
        //mFirebaseWebService = new FirebaseWebService();
        mEventsParser = new EventsParser();
        mEventsDbStore = new EventsDbStore(mContext);
    }

    public void getEvents(final OnTaskCompletedListener listener) {

/*
        final String eventsUrl = BASE_EVENTS_URL + mFirebaseWebService.getCurrentUser().getEmail() + "/events";
        mRepositoryLoadHelper.requestUserData(mUserEventsAsyncTask, eventsUrl);

        mUserEventsAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) {
                mEventsList = mEventsParser.parseEvents(response);
                listener.onSuccess(mEventsList);
            }

            @Override
            public void onFail() {
                FirebaseWebService firebaseWebService = new FirebaseWebService();
                firebaseWebService.refreshAccessToken(mContext, new FirebaseWebService.AccessTokenUpdatedListener() {
                    @Override
                    public void onAccessTokenUpdated() {
                        BaseDataLoadingAsyncTask updatedBaseDataLoadingAsyncTask = new BaseDataLoadingAsyncTask();
                        mRepositoryLoadHelper.requestUserData(updatedBaseDataLoadingAsyncTask, eventsUrl);
                        updatedBaseDataLoadingAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener() {
                            @Override
                            public void onDataLoaded(String response) {
                                mEventsList = mEventsParser.parseEvents(response);
                                listener.onSuccess(mEventsList);
                            }

                            @Override
                            public void onFail() {
                                mEventsList = mEventsDbStore.getEvents();
                                listener.onSuccess(mEventsList);
                            }
                        });
                    }
                });
            }
        });*/
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
                             final OnTaskCompletedListener listener) {/*
        BaseDataLoadingAsyncTask baseDataLoadingAsyncTask = new BaseDataLoadingAsyncTask();
        baseDataLoadingAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
                mRepositoryLoadHelper.getEventCreateOrUpdateParameters(event, url, requestMethod));

        baseDataLoadingAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) {
                createOrUpdateEventInDb(response, requestMethod);
                listener.onSuccess(mEventsDbStore.getEvents());

            }

            @Override
            public void onFail() {
                mFirebaseWebService.refreshAccessToken(mContext, new FirebaseWebService.AccessTokenUpdatedListener() {
                    @Override
                    public void onAccessTokenUpdated() {
                        BaseDataLoadingAsyncTask updatedBaseDataLoadingAsyncTask = new BaseDataLoadingAsyncTask();
                        updatedBaseDataLoadingAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener() {
                            @Override
                            public void onDataLoaded(String response) {
                                createOrUpdateEventInDb(response, requestMethod);
                                listener.onSuccess(mEventsDbStore.getEvents());
                            }

                            @Override
                            public void onFail() {
                                if (requestMethod == FirebaseWebService.RequestMethods.POST) {
                                    mEventsDbStore.addEvent(event);
                                } else if (requestMethod == FirebaseWebService.RequestMethods.PATCH) {
                                    mEventsDbStore.updateEvent(event);
                                }
                            }
                        });
                        updatedBaseDataLoadingAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
                                mRepositoryLoadHelper.getEventCreateOrUpdateParameters(event, url, requestMethod));
                    }
                });
            }
        });*/
    }

    private void createOrUpdateEventInDb(String response,
                                         FirebaseWebService.RequestMethods requestMethod) {
        if (requestMethod == FirebaseWebService.RequestMethods.POST) {
            //mEventsDbStore.addEvent(mEventsParser.parseEvent(response));
        } else if (requestMethod == FirebaseWebService.RequestMethods.PATCH) {
            //mEventsDbStore.updateEvent(mEventsParser.parseEvent(response));
        }
    }


    public void deleteEvent(final Event event) {
        final String url = BASE_EVENTS_URL +
                mFirebaseWebService.getCurrentUser().getEmail() +
                "/events/" +
                event.getId();
/*
        BaseDataLoadingAsyncTask baseDataLoadingAsyncTask = new BaseDataLoadingAsyncTask();

        baseDataLoadingAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
                mRepositoryLoadHelper.getDeleteParameters(url));

        baseDataLoadingAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) {
                mEventsDbStore.deleteEvent(event);
            }

            @Override
            public void onFail() {
                mFirebaseWebService.refreshAccessToken(mContext, new FirebaseWebService.AccessTokenUpdatedListener() {
                    @Override
                    public void onAccessTokenUpdated() {
                        BaseDataLoadingAsyncTask updatedBaseDataLoadingAsyncTask = new BaseDataLoadingAsyncTask();

                        updatedBaseDataLoadingAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener() {
                            @Override
                            public void onDataLoaded(String response) {
                                mEventsDbStore.deleteEvent(event);
                            }

                            @Override
                            public void onFail() {
                                mEventsDbStore.deleteEvent(event);
                            }
                        });

                        updatedBaseDataLoadingAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
                                mRepositoryLoadHelper.getDeleteParameters(url));
                    }
                });
            }
        });*/
    }

    public interface OnTaskCompletedListener {
        void onSuccess(ArrayList<Event> eventsList);

        void onFail();
    }
}
