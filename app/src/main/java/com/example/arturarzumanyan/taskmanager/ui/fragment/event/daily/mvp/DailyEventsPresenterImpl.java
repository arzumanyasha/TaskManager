package com.example.arturarzumanyan.taskmanager.ui.fragment.event.daily.mvp;

import android.util.SparseIntArray;

import com.example.arturarzumanyan.taskmanager.ui.util.ResourceManager;
import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsRepository;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.EventsFromDateSpecification;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;
import com.example.arturarzumanyan.taskmanager.ui.adapter.event.mvp.EventRowView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.arturarzumanyan.taskmanager.ui.util.ResourceManager.*;

public class DailyEventsPresenterImpl implements DailyEventsContract.DailyEventsPresenter {
    private DailyEventsContract.DailyEventsView mDailyEventsView;
    private EventsRepository mEventsRepository;
    private List<Event> mDailyEventsList;
    private SparseIntArray mColorPaletteArray;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public DailyEventsPresenterImpl(DailyEventsContract.DailyEventsView mDailyEventsView) {
        this.mDailyEventsView = mDailyEventsView;
        mEventsRepository = new EventsRepository();
        mColorPaletteArray = ResourceManager.getResourceManager().getColorPalette();
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
            if (mDailyEventsList.isEmpty()) {
                mDailyEventsView.setNoEventsTextViewVisible();
            }
        }
    }

    private void loadDailyEvents() {
        EventsFromDateSpecification eventsFromDateSpecification = new EventsFromDateSpecification();
        eventsFromDateSpecification.setDate(DateUtils.getCurrentTime());
        mCompositeDisposable.add(mEventsRepository.getEvents(eventsFromDateSpecification)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(events -> {
                    mDailyEventsList = events;
                    if (mDailyEventsView != null) {
                        mDailyEventsView.setProgressBarInvisible();
                        mDailyEventsView.setEventsAdapter(events);
                        if (events.isEmpty()) {
                            mDailyEventsView.setNoEventsTextViewVisible();
                        }
                    }
                })
                .doOnError(throwable -> onFail(getResourceManager().getErrorMessage(State.FAILED_TO_LOAD_EVENTS_ERROR)))
                .subscribe());
    }

    @Override
    public void processRetainedState() {
        mDailyEventsView.setProgressBarInvisible();
    }

    @Override
    public void processItemDelete(final int position) {
        Event event = mDailyEventsList.get(position);
        mDailyEventsList.remove(event);
        mCompositeDisposable.add(mEventsRepository.deleteEvent(event)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(events -> {
                    mDailyEventsList = events;
                    if (mDailyEventsView != null) {
                        mDailyEventsView.updateEventsAdapterAfterDelete(position);
                        if (events.isEmpty()) {
                            mDailyEventsView.setNoEventsTextViewVisible();
                        }
                    }
                })
                .doOnError(throwable -> onFail(getResourceManager().getErrorMessage(State.FAILED_TO_DELETE_EVENT_ERROR)))
                .subscribe());
    }

    private void onFail(String message) {
        if (mDailyEventsView != null) {
            mDailyEventsView.onFail(message);
        }
    }

    @Override
    public void onBindEventsRowViewAtPosition(int position, EventRowView rowView) {
        Event event = mDailyEventsList.get(position);
        rowView.setItemViewClickListener();
        rowView.setName(event.getName());
        rowView.setDescription(event.getDescription().replaceAll("[\n]", ""));
        rowView.setEventColor(mColorPaletteArray.get(event.getColorId()));
        rowView.setTime(DateUtils.formatTime(DateUtils.getEventDateFromString(event.getStartTime())) +
                " - " + DateUtils.formatTime(DateUtils.getEventDateFromString(event.getEndTime())));
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
        mCompositeDisposable.clear();
        mDailyEventsView = null;
    }
}
