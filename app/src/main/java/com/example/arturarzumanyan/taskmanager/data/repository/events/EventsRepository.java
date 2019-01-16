package com.example.arturarzumanyan.taskmanager.data.repository.events;

import android.os.AsyncTask;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.data.repository.BaseDataLoadingAsyncTask;
import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.EventsFromDateSpecification;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.EventsSpecification;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.domain.ResponseDto;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;
import com.example.arturarzumanyan.taskmanager.networking.util.EventsParser;
import com.example.arturarzumanyan.taskmanager.networking.util.Log;

import java.util.Collections;
import java.util.List;

import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.DELETE;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.GET;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.POST;

public class EventsRepository {
    private EventsDbStore mEventsDbStore;
    private EventsCloudStore mEventsCloudStore;
    private RepositoryLoadHelper mRepositoryLoadHelper;

    public EventsRepository() {
        mEventsCloudStore = new EventsCloudStore();
        mEventsDbStore = new EventsDbStore();
        mRepositoryLoadHelper = new RepositoryLoadHelper();
    }

    public void getEvents(EventsSpecification eventsSpecification, final OnEventsLoadedListener listener) {
        EventsAsyncTask eventsAsyncTask = new EventsAsyncTask(null,
                mRepositoryLoadHelper, mEventsDbStore, mEventsCloudStore,
                eventsSpecification, listener);

        eventsAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener<Event>() {
            @Override
            public void onSuccess(List<Event> list) {
                listener.onSuccess(list);
            }

            @Override
            public void onFail(String message) {
                listener.onFail(message + '\n' + "Failed to load events");
            }
        });

        eventsAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, GET);
    }

    public void addOrUpdateEvent(Event event, final FirebaseWebService.RequestMethods requestMethod,
                                 final OnEventsLoadedListener listener) {
        EventsFromDateSpecification eventsFromDateSpecification = new EventsFromDateSpecification();
        eventsFromDateSpecification.setDate(DateUtils.getEventDate(event.getStartTime()));

        EventsAsyncTask eventsAsyncTask = new EventsAsyncTask(event,
                mRepositoryLoadHelper, mEventsDbStore, mEventsCloudStore,
                eventsFromDateSpecification, null);
        eventsAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener<Event>() {
            @Override
            public void onSuccess(List<Event> list) {
                listener.onSuccess(list);
            }

            @Override
            public void onFail(String message) {
                if (requestMethod == POST) {
                    listener.onFail(message + '\n' + "Failed to create event");
                } else {
                    listener.onFail(message + '\n' + "Failed to update event");
                }
            }
        });
        eventsAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, requestMethod);
    }

    public void deleteEvent(Event event, final OnEventsLoadedListener listener) {
        EventsFromDateSpecification eventsFromDateSpecification = new EventsFromDateSpecification();
        eventsFromDateSpecification.setDate(DateUtils.getCurrentTime());

        EventsAsyncTask eventsAsyncTask = new EventsAsyncTask(event,
                mRepositoryLoadHelper, mEventsDbStore, mEventsCloudStore,
                eventsFromDateSpecification, null);

        eventsAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener<Event>() {
            @Override
            public void onSuccess(List<Event> list) {
                Log.v("Event successfully deleted");
            }

            @Override
            public void onFail(String message) {
                listener.onFail(message + '\n' + "Failed to delete event");
                Log.v("Failed to delete event");
            }
        });

        eventsAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, DELETE);
    }

    public interface OnEventsLoadedListener {
        void onSuccess(List<Event> eventsList);

        void onFail(String message);
    }

    public static class EventsAsyncTask extends BaseDataLoadingAsyncTask<Event> {

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
        protected void doDeleteQuery() {
            mEventsDbStore.deleteEvent(mEvent);
        }

        @Override
        protected void onServerError() {
            mListener.onFail("Calendar API server error");
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

                    eventsAsyncTask.executeOnExecutor(SERIAL_EXECUTOR, requestMethod);
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

    }
}
