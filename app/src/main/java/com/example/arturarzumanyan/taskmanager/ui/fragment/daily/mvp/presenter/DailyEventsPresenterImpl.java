package com.example.arturarzumanyan.taskmanager.ui.fragment.daily.mvp.presenter;

import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsRepository;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.EventsFromDateSpecification;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;
import com.example.arturarzumanyan.taskmanager.ui.dialog.event.EventsDialog;
import com.example.arturarzumanyan.taskmanager.ui.fragment.daily.mvp.contract.DailyEventsContract;

import java.util.List;

public class DailyEventsPresenterImpl implements DailyEventsContract.DailyEventsPresenter {
    private DailyEventsContract.DailyEventsView mDailyEventsView;
    private EventsRepository mEventsRepository;
    private List<Event> mDailyEventsList;


    public DailyEventsPresenterImpl(DailyEventsContract.DailyEventsView mDailyEventsView) {
        this.mDailyEventsView = mDailyEventsView;
        mEventsRepository = new EventsRepository();
    }

    @Override
    public void attachView(DailyEventsContract.DailyEventsView dailyEventsView) {
        this.mDailyEventsView = dailyEventsView;
    }

    @Override
    public void processDailyEvents() {
        if (mDailyEventsList == null) {
            loadDailyEvents();
        } else {
            mDailyEventsView.setEventsAdapter(mDailyEventsList);
        }
    }

    private void loadDailyEvents() {
        final EventsFromDateSpecification eventsFromDateSpecification = new EventsFromDateSpecification();
        eventsFromDateSpecification.setDate(DateUtils.getCurrentTime());
        mEventsRepository.getEvents(eventsFromDateSpecification, new EventsRepository.OnEventsLoadedListener() {
            @Override
            public void onSuccess(List<Event> eventsList) {
                mDailyEventsList = eventsList;
                mDailyEventsView.setProgressBarInvisible();
                mDailyEventsView.setEventsAdapter(eventsList);
                if (eventsList.isEmpty()) {
                    mDailyEventsView.setNoEventsTextViewVisible();
                }
            }

            @Override
            public void onFail(String message) {
                mDailyEventsView.onFail(message);
            }

            @Override
            public void onPermissionDenied() {
                /** To-do: add realization with start signInActivity*/
            }
        });
    }

    @Override
    public void processRetainedState() {
        mDailyEventsView.setProgressBarInvisible();
    }

    @Override
    public void deleteEvent(Event event) {
        mEventsRepository.deleteEvent(event, new EventsRepository.OnEventsLoadedListener() {
            @Override
            public void onSuccess(List<Event> eventsList) {
                mDailyEventsList = eventsList;
                mDailyEventsView.updateEventsAdapter(eventsList);
                if (eventsList.isEmpty()) {
                    mDailyEventsView.setNoEventsTextViewVisible();
                }
            }

            @Override
            public void onFail(String message) {
                mDailyEventsView.onFail(message);
            }

            @Override
            public void onPermissionDenied() {
                /** To-do: add realization with start signInActivity*/
            }
        });
    }

    @Override
    public void processUpdatedEvents(List<Event> events) {
        mDailyEventsView.setNoEventsTextViewInvisible();
        mDailyEventsView.updateEventsAdapter(events);
    }

    @Override
    public void processEventDialog(Event event) {
        EventsDialog eventsDialog = EventsDialog.newInstance(event);
        eventsDialog.setEventsReadyListener(new EventsDialog.EventsReadyListener() {
            @Override
            public void onEventsReady(List<Event> events) {
                mDailyEventsView.setNoEventsTextViewInvisible();
                mDailyEventsView.updateEventsAdapter(events);
            }
        });
        mDailyEventsView.showDialog(eventsDialog);
    }

    @Override
    public void unsubscribe() {
        mDailyEventsView = null;
    }
}
