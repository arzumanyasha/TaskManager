package com.example.arturarzumanyan.taskmanager.ui.fragment.event.daily.mvp;

import android.content.Context;
import android.util.SparseIntArray;

import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsRepository;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.EventsFromDateSpecification;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;
import com.example.arturarzumanyan.taskmanager.ui.adapter.event.mvp.EventRowView;
import com.example.arturarzumanyan.taskmanager.ui.util.ColorPalette;

import java.util.List;

public class DailyEventsPresenterImpl implements DailyEventsContract.DailyEventsPresenter {
    private DailyEventsContract.DailyEventsView mDailyEventsView;
    private EventsRepository mEventsRepository;
    private List<Event> mDailyEventsList;
    private SparseIntArray mColorPaletteArray;

    public DailyEventsPresenterImpl(DailyEventsContract.DailyEventsView mDailyEventsView, Context context) {
        this.mDailyEventsView = mDailyEventsView;
        mEventsRepository = new EventsRepository();
        ColorPalette colorPalette = new ColorPalette(context);
        mColorPaletteArray = colorPalette.getColorPalette();
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
                if (mDailyEventsView != null) {
                    mDailyEventsView.setProgressBarInvisible();
                    mDailyEventsView.setEventsAdapter(eventsList);
                    if (eventsList.isEmpty()) {
                        mDailyEventsView.setNoEventsTextViewVisible();
                    }
                }
            }

            @Override
            public void onFail(String message) {
                if (mDailyEventsView != null) {
                    mDailyEventsView.onFail(message);
                }
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
    public void processItemDelete(final int position) {
        Event event = mDailyEventsList.get(position);
        mDailyEventsList.remove(event);
        mEventsRepository.deleteEvent(event, new EventsRepository.OnEventsLoadedListener() {
            @Override
            public void onSuccess(List<Event> eventsList) {
                mDailyEventsList = eventsList;
                if (mDailyEventsView != null) {
                    mDailyEventsView.updateEventsAdapterAfterDelete(position);
                    if (eventsList.isEmpty()) {
                        mDailyEventsView.setNoEventsTextViewVisible();
                    }
                }
            }

            @Override
            public void onFail(String message) {
                if (mDailyEventsView != null) {
                    mDailyEventsView.onFail(message);
                }
            }

            @Override
            public void onPermissionDenied() {
                /** To-do: add realization with start signInActivity*/
            }
        });
    }

    @Override
    public void onBindEventsRowViewAtPosition(int position, EventRowView rowView) {
        Event event = mDailyEventsList.get(position);
        rowView.setItemViewClickListener();
        rowView.setName(event.getName());
        rowView.setDescription(event.getDescription().replaceAll("[\n]", ""));
        rowView.setEventColor(mColorPaletteArray.get(event.getColorId()));
        rowView.setTime(DateUtils.formatTime(event.getStartTime()) + " - " + DateUtils.formatTime(event.getEndTime()));
        rowView.setDelete();
    }

    @Override
    public void updateEventsList(List<Event> updatedList) {
        mDailyEventsList = updatedList;
        mDailyEventsView.setNoEventsTextViewInvisible();
        mDailyEventsView.updateEventsAdapter();
    }

    @Override
    public void processItemClick(int position) {
        Event event = mDailyEventsList.get(position);
        mDailyEventsView.showEventUpdatingDialog(event);
    }

    @Override
    public int getEventsRowsCount() {
        return mDailyEventsList.size();
    }


    @Override
    public void unsubscribe() {
        mDailyEventsView = null;
    }
}
