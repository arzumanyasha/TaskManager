package com.example.arturarzumanyan.taskmanager.data.repository.events;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.base.BaseHttpUrlConnection;
import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;
import com.example.arturarzumanyan.taskmanager.networking.util.EventsParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.example.arturarzumanyan.taskmanager.data.repository.events.EventsCloudStore.BASE_EVENTS_URL;

public class EventsRepository {
    @SuppressLint("StaticFieldLeak")
    private static EventsDbStore mEventsDbStore;
    private EventsCloudStore mEventsCloudStore;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private RepositoryLoadHelper mRepositoryLoadHelper;

    public EventsRepository(Context context) {
        mContext = context;
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
        } else if ((mRepositoryLoadHelper.isOnline() && (events.size() != 0)) ||
                (!mRepositoryLoadHelper.isOnline() && (events.size() != 0))) {
            listener.onSuccess(events);
        } else {
            listener.onFail();
        }
    }

    public ArrayList<Event> getDailyEvents() {
        return mEventsDbStore.getDailyEvents();
    }

    public ArrayList<Event> getWeeklyEvents() {
        return mEventsDbStore.getWeeklyEvents();
    }

    public ArrayList<Event> getMonthlyEvents() {
        return mEventsDbStore.getMonthlyEvents();
    }

    public ArrayList<Event> getEventsFromDate(Date date) {
        return mEventsDbStore.getEventsFromDate(date);
    }

    public void addEvent(Event event, final EventsCloudStore.OnTaskCompletedListener listener) {
        FirebaseWebService firebaseWebService = new FirebaseWebService();
        String url = BASE_EVENTS_URL + firebaseWebService.getCurrentUser().getEmail() + "/events";
        RepositoryLoadHelper repositoryLoadHelper = new RepositoryLoadHelper(mContext);
        RequestParameters requestParameters = repositoryLoadHelper.getEventCreateOrUpdateParameters(event, url, FirebaseWebService.RequestMethods.POST);
        EventsAsyncTask eventsAsyncTask = new EventsAsyncTask(event);
        eventsAsyncTask.setEventDataLoadingListener(new EventsAsyncTask.EventDataLoadingListener() {
            @Override
            public void onSuccess(ArrayList<Event> events) {
                listener.onSuccess(events);
            }

            @Override
            public void onFail() {

            }
        });
        eventsAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, requestParameters);

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


    public static class EventsAsyncTask extends AsyncTask<RequestParameters, Void, ArrayList<Event>> {

        private String mBuffer;
        private Event mEvent;

        public EventsAsyncTask(Event event) {
            this.mEvent = event;
        }

        @Override
        protected ArrayList<Event> doInBackground(final RequestParameters... requestParameters) {
            RepositoryLoadHelper repositoryLoadHelper = new RepositoryLoadHelper(mContext);
            if (repositoryLoadHelper.isOnline() && requestParameters[0].getRequestMethod()!= FirebaseWebService.RequestMethods.GET) {
                String dataFromServer = getResultFromServer(requestParameters[0]);
                if (dataFromServer.isEmpty()) {
                    FirebaseWebService firebaseWebService = new FirebaseWebService();
                    firebaseWebService.refreshAccessToken(mContext, new FirebaseWebService.AccessTokenUpdatedListener() {
                        @Override
                        public void onAccessTokenUpdated() {
                            String dataFromServer = getResultFromServer(requestParameters[0]);
                            dbQuery(dataFromServer, requestParameters[0]);
                        }
                    });
                }

                dbQuery(dataFromServer, requestParameters[0]);

            } else {
                if (requestParameters[0].getRequestMethod() == FirebaseWebService.RequestMethods.POST) {
                    mEventsDbStore.addEvent(mEvent);
                } else if (requestParameters[0].getRequestMethod() == FirebaseWebService.RequestMethods.PATCH) {
                    mEventsDbStore.updateEvent(mEvent);
                } else if (requestParameters[0].getRequestMethod() == FirebaseWebService.RequestMethods.DELETE) {
                    mEventsDbStore.deleteEvent(mEvent);
                }
            }

            return mEventsDbStore.getEvents();
        }

        private String getResultFromServer(RequestParameters requestParameters) {
            String url = requestParameters.getUrl();
            FirebaseWebService.RequestMethods requestMethod = requestParameters.getRequestMethod();
            HashMap<String, Object> requestBodyParameters = requestParameters.getRequestBodyParameters();
            HashMap<String, String> requestHeaderParameters = requestParameters.getRequestHeaderParameters();

            BaseHttpUrlConnection baseHttpUrlConnection = new BaseHttpUrlConnection();
            mBuffer = baseHttpUrlConnection.getResult(url,
                    requestMethod,
                    requestBodyParameters,
                    requestHeaderParameters);
            return mBuffer;
        }

        private void dbQuery(String data, RequestParameters requestParameters){
            if (requestParameters.getRequestMethod() == FirebaseWebService.RequestMethods.POST) {
                EventsParser eventsParser = new EventsParser();
                mEventsDbStore.addEvent(eventsParser.parseEvent(data));
            } else if (requestParameters.getRequestMethod() == FirebaseWebService.RequestMethods.PATCH) {
                EventsParser eventsParser = new EventsParser();
                mEventsDbStore.updateEvent(eventsParser.parseEvent(data));
            } else if (requestParameters.getRequestMethod() == FirebaseWebService.RequestMethods.DELETE) {
                mEventsDbStore.deleteEvent(mEvent);
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Event> events) {
            super.onPostExecute(events);
            eventDataLoadingListener.onSuccess(events);
        }

        public interface EventDataLoadingListener {
            void onSuccess(ArrayList<Event> events);

            void onFail();
        }

        public void setEventDataLoadingListener(EventDataLoadingListener listener) {
            this.eventDataLoadingListener = listener;
        }

        private EventDataLoadingListener eventDataLoadingListener;
    }
}
