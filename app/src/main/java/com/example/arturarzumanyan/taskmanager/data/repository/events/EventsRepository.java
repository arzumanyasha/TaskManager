package com.example.arturarzumanyan.taskmanager.data.repository.events;

import android.content.Context;
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

import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;

import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.DELETE;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.GET;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.PATCH;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.POST;

public class EventsRepository {
    private EventsDbStore mEventsDbStore;
    private EventsCloudStore mEventsCloudStore;
    private RepositoryLoadHelper mRepositoryLoadHelper;
    private FirebaseWebService mFirebaseWebService;

    public EventsRepository(Context context) {
        mEventsCloudStore = new EventsCloudStore(context);
        mEventsDbStore = new EventsDbStore(context);
        mRepositoryLoadHelper = new RepositoryLoadHelper(context);
        mFirebaseWebService = new FirebaseWebService(context);
    }

    public void getEvents(EventsSpecification eventsSpecification, final OnEventsLoadedListener listener) {
        EventsAsyncTask eventsAsyncTask = new EventsAsyncTask(null,
                mRepositoryLoadHelper, mFirebaseWebService, mEventsDbStore, mEventsCloudStore,
                eventsSpecification, listener);

        eventsAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener<Event>() {
            @Override
            public void onSuccess(List<Event> list) {
                listener.onSuccess(list);
            }
        });

        eventsAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, GET);
    }

    public void addOrUpdateEvent(Event event, FirebaseWebService.RequestMethods requestMethod,
                                 final OnEventsLoadedListener listener){
        EventsFromDateSpecification eventsFromDateSpecification = new EventsFromDateSpecification();
        eventsFromDateSpecification.setDate(DateUtils.getCurrentTime());

        EventsAsyncTask eventsAsyncTask = new EventsAsyncTask(event,
                mRepositoryLoadHelper, mFirebaseWebService, mEventsDbStore, mEventsCloudStore,
                eventsFromDateSpecification, null);
        eventsAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener<Event>() {
            @Override
            public void onSuccess(List<Event> list) {
                listener.onSuccess(list);
            }
        });
        eventsAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, requestMethod);
    }

    public void deleteEvent(Event event) {
        EventsFromDateSpecification eventsFromDateSpecification = new EventsFromDateSpecification();
        eventsFromDateSpecification.setDate(DateUtils.getCurrentTime());

        EventsAsyncTask eventsAsyncTask = new EventsAsyncTask(event,
                mRepositoryLoadHelper, mFirebaseWebService, mEventsDbStore, mEventsCloudStore,
                eventsFromDateSpecification, null);

        eventsAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener<Event>() {
            @Override
            public void onSuccess(List<Event> list) {
                Log.v("Event successfully deleted");
            }
        });

        eventsAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, DELETE);
    }

    public interface OnEventsLoadedListener {
        void onSuccess(List<Event> eventsList);

        void onFail();
    }

    public static class EventsAsyncTask extends BaseDataLoadingAsyncTask<Event> {

        private Event mEvent;
        private RepositoryLoadHelper mRepositoryLoadHelper;
        private FirebaseWebService mFirebaseWebService;
        private EventsDbStore mEventsDbStore;
        private EventsCloudStore mEventsCloudStore;
        private EventsSpecification mEventsSpecification;
        private OnEventsLoadedListener mListener;

        public EventsAsyncTask(Event event,
                               RepositoryLoadHelper repositoryLoadHelper,
                               FirebaseWebService firebaseWebService,
                               EventsDbStore eventsDbStore,
                               EventsCloudStore eventsCloudStore,
                               EventsSpecification eventsSpecification,
                               OnEventsLoadedListener listener) {
            this.mEvent = event;
            this.mRepositoryLoadHelper = repositoryLoadHelper;
            this.mFirebaseWebService = firebaseWebService;
            this.mEventsDbStore = eventsDbStore;
            this.mEventsCloudStore = eventsCloudStore;
            this.mEventsSpecification = eventsSpecification;
            this.mListener = listener;
        }

        @Override
        protected List<Event> doInBackground(FirebaseWebService.RequestMethods... requestMethods) {
            if (mRepositoryLoadHelper.isOnline()) {

                ResponseDto responseDto = getResponseFromServer(requestMethods[0]);

                int responseCode = 0;
                if (responseDto != null) {
                    responseCode = responseDto.getResponseCode();
                }

                if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    retryGetResultFromServer(requestMethods[0]);
                } else {
                    if (responseCode == HttpURLConnection.HTTP_OK ||
                            responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                        if (requestMethods[0] == POST || requestMethods[0] == PATCH) {
                            Event event = parseEvent(responseDto.getResponseData());
                            dbQuery(event, requestMethods[0]);
                        } else if (requestMethods[0] == GET) {
                            List<Event> events = parseEventsData(responseDto.getResponseData());
                            updateDbQuery(events);
                        } else {
                            dbQuery(mEvent, requestMethods[0]);
                        }
                    }
                }

            } else {
                if (requestMethods[0] != GET) {
                    dbQuery(mEvent, requestMethods[0]);
                } else {
                    return mEventsDbStore.getEvents(mEventsSpecification);
                }
            }

            return mEventsDbStore.getEvents(mEventsSpecification);
        }

        private ResponseDto getResponseFromServer(FirebaseWebService.RequestMethods requestMethod) {
            switch (requestMethod) {
                case GET: {
                    return mEventsCloudStore.getEventsFromServer(mEventsSpecification);
                }
                case POST: {
                    return mEventsCloudStore.addEventOnServer(mEvent);
                }
                case PATCH: {
                    return mEventsCloudStore.updateEventOnServer(mEvent);
                }
                case DELETE: {
                    return mEventsCloudStore.deleteEventOnServer(mEvent);
                }
                default: {
                    return null;
                }
            }
        }

        private void retryGetResultFromServer(final FirebaseWebService.RequestMethods requestMethod) {
            mFirebaseWebService.refreshAccessToken(new FirebaseWebService.AccessTokenUpdatedListener() {
                @Override
                public void onAccessTokenUpdated() {
                    EventsAsyncTask eventsAsyncTask = new EventsAsyncTask(null,
                            mRepositoryLoadHelper, mFirebaseWebService, mEventsDbStore, mEventsCloudStore,
                            mEventsSpecification, mListener);

                    if (requestMethod != DELETE) {
                        eventsAsyncTask.setDataInfoLoadingListener(new UserDataLoadingListener<Event>() {
                            @Override
                            public void onSuccess(List<Event> list) {
                                mListener.onSuccess(list);
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

        private void dbQuery(Event event, FirebaseWebService.RequestMethods requestMethod) {
            if (requestMethod == POST || requestMethod == PATCH) {
                mEventsDbStore.addOrUpdateEvents(Collections.singletonList(event));
            } else if (requestMethod == DELETE) {
                mEventsDbStore.deleteEvent(event);
            }
        }

        private void updateDbQuery(List<Event> events) {
            mEventsDbStore.addOrUpdateEvents(events);
            mEventsDbStore.getEvents(mEventsSpecification);
        }

    }
}
