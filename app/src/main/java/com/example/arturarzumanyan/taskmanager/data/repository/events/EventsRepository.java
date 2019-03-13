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
                    .flatMap(responseBody -> updateDbQuery(responseBody, eventsSpecification));
        } else {
            eventsSingle = mEventsDbStore.getEvents(eventsSpecification);
        }

        return eventsSingle;
    }

    private Single<List<Event>> updateDbQuery(ResponseBody responseBody, EventsSpecification eventsSpecification) throws IOException {
        EventsParser eventsParser = new EventsParser();
        List<Event> events = eventsParser.parseEvents(responseBody.string());
        return mEventsDbStore.addOrUpdateEvents(events, eventsSpecification);
    }

    public Single<List<Event>> addEvent(Event event) {
        EventsFromDateSpecification eventsFromDateSpecification = new EventsFromDateSpecification();
        eventsFromDateSpecification.setDate(DateUtils.formatEventDate(event.getStartTime()));

        Single<List<Event>> eventsSingle;
        if (RepositoryLoadHelper.isOnline()) {
            eventsSingle = mEventsCloudStore.addEventOnServer(event)
                    .filter(response -> response != null).toSingle()
                    .map(this::parseEvent)
                    .flatMap(responseBody -> mEventsDbStore.addOrUpdateEvents(Collections.singletonList(event), eventsFromDateSpecification));
        } else {
            eventsSingle = mEventsDbStore.addOrUpdateEvents(Collections.singletonList(event), eventsFromDateSpecification);
        }

        return eventsSingle;
    }

    public Single<List<Event>> updateEvent(Event event) {
        EventsFromDateSpecification eventsFromDateSpecification = new EventsFromDateSpecification();
        eventsFromDateSpecification.setDate(DateUtils.formatEventDate(event.getStartTime()));

        Single<List<Event>> eventsSingle;
        if (RepositoryLoadHelper.isOnline()) {
            eventsSingle = mEventsCloudStore.updateEventOnServer(event)
                    .filter(response -> response != null).toSingle()
                    .map(this::parseEvent)
                    .flatMap(responseBody -> mEventsDbStore.addOrUpdateEvents(Collections.singletonList(event), eventsFromDateSpecification));
        } else {
            eventsSingle = mEventsDbStore.addOrUpdateEvents(Collections.singletonList(event), eventsFromDateSpecification);
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
                    .flatMap(responseBody -> mEventsDbStore.deleteEvent(event, eventsFromDateSpecification));
        } else {
            eventsSingle = mEventsDbStore.deleteEvent(event, eventsFromDateSpecification);
        }

        return eventsSingle;
    }

    /*public static class EventsAsyncTask extends BaseDataLoadingAsyncTask<Event> {

        private Event mEvent;
        private RepositoryLoadHelper mRepositoryLoadHelper;
        private EventsDbStore mEventsDbStore;
        private EventsCloudStore mEventsCloudStore;
        private EventsSpecification mEventsSpecification;
        private OnEventsLoadedListener mListener;

        EventsAsyncTask(Event event,
                        RepositoryLoadHelper repositoryLoadHelper,
                        EventsDbStore eventsDbStore,
                        EventsCloudStore eventsCloudStore,
                        EventsSpecification eventsSpecification,
                        OnEventsLoadedListener listener) {
            this.mEvent = event;
            this.mRepositoryLoadHelper = repositoryLoadHelper;
            this.mEventsDbStore = eventsDbStore;
            this.mEventsCloudStore = eventsCloudStore;
            this.mEventsSpecification = eventsSpecification;
            this.mListener = listener;
        }

        @Override
        protected List<Event> doInBackground(FirebaseWebService.RequestMethods... requestMethods) {
            return super.doInBackground(requestMethods[0]);
        }

        @Override
        protected ResponseDto doGetRequest() {
            return mEventsCloudStore.getEventsFromServer(mEventsSpecification);
        }

       @Override
        protected ResponseDto doPostRequest() {
            return mEventsCloudStore.addEventOnServer(mEvent);
        }

        @Override
        protected ResponseDto doPatchRequest() {
            return mEventsCloudStore.updateEventOnServer(mEvent);
        }

        @Override
        protected ResponseDto doDeleteRequest() {
            return mEventsCloudStore.deleteEventOnServer(mEvent);
        }

        @Override
        protected List<Event> doSelectQuery() {
            return mEventsDbStore.getEvents(mEventsSpecification);
        }

        @Override
        protected void refreshDbQuery(ResponseDto responseDto) {
            List<Event> events = parseEventsData(responseDto.getResponseData());
            updateDbQuery(events);
        }

        @Override
        protected void doInsertQuery(ResponseDto responseDto) {
            Event event;
            if (responseDto != null) {
                event = parseEvent(responseDto.getResponseData());
            } else {
                event = mEvent;
            }
            mEventsDbStore.addOrUpdateEvents(Collections.singletonList(event));
        }

        @Override
        protected void doUpdateQuery() {
            mEventsDbStore.addOrUpdateEvents(Collections.singletonList(mEvent));
        }

        @Override
        protected boolean doDeleteQuery() {
            mEventsDbStore.deleteEvent(mEvent);
            return true;
        }

        @Override
        protected void retryGetResultFromServer(final FirebaseWebService.RequestMethods requestMethod) {
            FirebaseWebService.getFirebaseWebServiceInstance().refreshAccessToken(new FirebaseWebService.AccessTokenUpdatedListener() {
                @Override
                public void onAccessTokenUpdated() {
                    EventsAsyncTask eventsAsyncTask = new EventsAsyncTask(null,
                            mRepositoryLoadHelper, mEventsDbStore, mEventsCloudStore,
                            mEventsSpecification, mListener);

                    if (requestMethod != DELETE) {
                        eventsAsyncTask.setDataInfoLoadingListener(new UserDataLoadingListener<Event>() {
                            @Override
                            public void onSuccess(List<Event> list) {
                                mListener.onSuccess(list);
                            }

                            @Override
                            public void onFail(String message) {
                                mListener.onFail(message);
                            }
                        });
                    }

                    eventsAsyncTask.execute(requestMethod);
                }

                @Override
                public void onFail() {
                    mListener.onPermissionDenied();
                }
            });
        }

        private Event parseEvent(String data) {
            EventsParser eventsParser = new EventsParser();
            return eventsParser.parseEvent(data);
        }

        private List<Event> parseEventsData(String data) {
            EventsParser eventsParser = new EventsParser();
            return eventsParser.parseEvents(data);
        }

        private void updateDbQuery(List<Event> events) {
            mEventsDbStore.addOrUpdateEvents(events);
            mEventsDbStore.getEvents(mEventsSpecification);
        }

    }*/
}
