package com.example.arturarzumanyan.taskmanager.data.repository.events;

import android.content.Context;
import android.os.AsyncTask;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.data.repository.BaseDataLoadingAsyncTask;
import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.AllEventsSpecification;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.EventsFromDateSpecification;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.Specification;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;
import com.example.arturarzumanyan.taskmanager.networking.util.EventsParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.GET;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.PATCH;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.POST;
import static com.example.arturarzumanyan.taskmanager.data.repository.events.EventsCloudStore.BASE_EVENTS_URL;

public class EventsRepository {
    private EventsDbStore mEventsDbStore;
    private EventsCloudStore mEventsCloudStore;
    private Context mContext;
    private RepositoryLoadHelper mRepositoryLoadHelper;
    private FirebaseWebService mFirebaseWebService;

    public EventsRepository(Context context) {
        mContext = context;
        mEventsCloudStore = new EventsCloudStore(mContext);
        mEventsDbStore = new EventsDbStore(mContext);
        mRepositoryLoadHelper = new RepositoryLoadHelper(mContext);
        mFirebaseWebService = new FirebaseWebService(mContext);
    }

    /*
        public void loadEvents(final OnEventsLoadedListener listener) {
            List<Event> events = mEventsDbStore.getEvents();

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
        }*/
/*
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
*/
    public void getEvents(Specification specification, final OnEventsLoadedListener listener) {
        String eventsUrl = "";
        if (specification.getStartDate().isEmpty() && specification.getEndDate().isEmpty()) {
            eventsUrl = BASE_EVENTS_URL + mFirebaseWebService.getCurrentUser().getEmail() + "/events";
        } else {
            eventsUrl = BASE_EVENTS_URL + mFirebaseWebService.getCurrentUser().getEmail() + "/events?" +
                    "timeMax=" + DateUtils.decodeDate(specification.getEndDate()) +
                    "&timeMin=" + DateUtils.decodeDate(specification.getStartDate());
        }

        EventsAsyncTask eventsAsyncTask = new EventsAsyncTask(null, mContext,
                mRepositoryLoadHelper, mFirebaseWebService, mEventsDbStore, specification, listener);

        eventsAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener<Event>() {
            @Override
            public void onSuccess(List<Event> list) {
                listener.onSuccess(list);
            }

            @Override
            public void onFail() {
                listener.onFail();
            }
        });
        mRepositoryLoadHelper.requestUserData(eventsAsyncTask, eventsUrl);
    }

    public void addEvent(Event event, final OnEventsLoadedListener listener) {
        String url = BASE_EVENTS_URL + mFirebaseWebService.getCurrentUser().getEmail() + "/events";
        RequestParameters requestParameters = mRepositoryLoadHelper.getEventCreateOrUpdateParameters(event, url, POST);

        EventsFromDateSpecification eventsFromDateSpecification = new EventsFromDateSpecification();
        eventsFromDateSpecification.setDate(DateUtils.getCurrentTime());

        EventsAsyncTask eventsAsyncTask = new EventsAsyncTask(event, mContext,
                mRepositoryLoadHelper, mFirebaseWebService, mEventsDbStore, eventsFromDateSpecification, null);
        eventsAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener<Event>() {
            @Override
            public void onSuccess(List<Event> list) {
                listener.onSuccess(list);
            }

            @Override
            public void onFail() {
                listener.onFail();
            }
        });
        eventsAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, requestParameters);
    }

    public void updateEvent(Event event, final OnEventsLoadedListener listener) {
        String url = BASE_EVENTS_URL +
                mFirebaseWebService.getCurrentUser().getEmail() +
                "/events/" +
                event.getId();
        RequestParameters requestParameters = mRepositoryLoadHelper.getEventCreateOrUpdateParameters(event, url, PATCH);

        EventsFromDateSpecification eventsFromDateSpecification = new EventsFromDateSpecification();
        eventsFromDateSpecification.setDate(DateUtils.getCurrentTime());

        EventsAsyncTask eventsAsyncTask = new EventsAsyncTask(event, mContext,
                mRepositoryLoadHelper, mFirebaseWebService, mEventsDbStore, eventsFromDateSpecification, null);
        eventsAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener<Event>() {
            @Override
            public void onSuccess(List<Event> list) {
                listener.onSuccess(list);
            }

            @Override
            public void onFail() {
                listener.onFail();
            }
        });
        eventsAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, requestParameters);
    }

    public void deleteEvent(Event event) {
        String url = BASE_EVENTS_URL +
                mFirebaseWebService.getCurrentUser().getEmail() +
                "/events/" +
                event.getId();
        RequestParameters requestParameters = mRepositoryLoadHelper.getDeleteParameters(url);

        EventsFromDateSpecification eventsFromDateSpecification = new EventsFromDateSpecification();
        eventsFromDateSpecification.setDate(DateUtils.getCurrentTime());

        EventsAsyncTask eventsAsyncTask = new EventsAsyncTask(event, mContext,
                mRepositoryLoadHelper, mFirebaseWebService, mEventsDbStore, eventsFromDateSpecification, null);
        eventsAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, requestParameters);
    }

    public interface OnEventsLoadedListener {
        void onSuccess(List<Event> eventsList);

        void onFail();
    }

    public static class EventsAsyncTask extends BaseDataLoadingAsyncTask<Event> {

        private Event mEvent;
        //private WeakReference<Context> mContextWeakReference;
        private RepositoryLoadHelper mRepositoryLoadHelper;
        private FirebaseWebService mFirebaseWebService;
        private EventsDbStore mEventsDbStore;
        private Specification mSpecification;
        private OnEventsLoadedListener mListener;

        public EventsAsyncTask(Event event,
                               Context context,
                               RepositoryLoadHelper repositoryLoadHelper,
                               FirebaseWebService firebaseWebService,
                               EventsDbStore eventsDbStore,
                               Specification specification,
                               OnEventsLoadedListener listener) {
            this.mEvent = event;
            //this.mContextWeakReference = new WeakReference<>(context);
            this.mRepositoryLoadHelper = repositoryLoadHelper;
            this.mFirebaseWebService = firebaseWebService;
            this.mEventsDbStore = eventsDbStore;
            this.mSpecification = specification;
            this.mListener = listener;
        }

        @Override
        protected List<Event> doInBackground(final RequestParameters... requestParameters) {
            if (mRepositoryLoadHelper.isOnline()) {
                String dataFromServer = getResultFromServer(requestParameters[0]);
                if (dataFromServer.isEmpty()) {
                    retryGetResultFromServer(requestParameters[0]);
                }

                if (requestParameters[0].getRequestMethod() == POST ||
                        requestParameters[0].getRequestMethod() == PATCH) {
                    dbQuery(parseEvent(dataFromServer), requestParameters[0]);
                } else if(requestParameters[0].getRequestMethod() == GET){
                    updateDbQuery(parseEventsData(dataFromServer));
                } else {
                    dbQuery(mEvent, requestParameters[0]);
                }

            } else {
                if (requestParameters[0].getRequestMethod() != GET) {
                    dbQuery(mEvent, requestParameters[0]);
                } else {
                    return mEventsDbStore.getEvents(mSpecification);
                }
            }

            return mEventsDbStore.getEvents(mSpecification);
        }

        private void retryGetResultFromServer(final RequestParameters requestParameters) {
            mFirebaseWebService.refreshAccessToken(new FirebaseWebService.AccessTokenUpdatedListener() {
                @Override
                public void onAccessTokenUpdated() {
                    EventsAsyncTask eventsAsyncTask = new EventsAsyncTask(null, null,
                            mRepositoryLoadHelper, mFirebaseWebService, mEventsDbStore, mSpecification, mListener);
                    eventsAsyncTask.setDataInfoLoadingListener(new UserDataLoadingListener<Event>() {
                        @Override
                        public void onSuccess(List<Event> list) {
                            mListener.onSuccess(list);
                        }

                        @Override
                        public void onFail() {
                            mListener.onFail();
                        }
                    });

                    eventsAsyncTask.executeOnExecutor(SERIAL_EXECUTOR, requestParameters);
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

        private void dbQuery(Event event, RequestParameters requestParameters) {
            if (requestParameters.getRequestMethod() == POST ||
                    requestParameters.getRequestMethod() == PATCH) {
                mEventsDbStore.addOrUpdateEvents(Collections.singletonList(event));
            } else if (requestParameters.getRequestMethod() == FirebaseWebService.RequestMethods.DELETE) {
                mEventsDbStore.deleteEvent(event);
            }
        }

        private void updateDbQuery(List<Event> events) {
            mEventsDbStore.addOrUpdateEvents(events);
            mEventsDbStore.getEvents(mSpecification);
        }

    }
}
