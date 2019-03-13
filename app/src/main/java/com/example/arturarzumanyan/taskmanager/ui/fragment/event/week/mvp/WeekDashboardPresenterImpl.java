package com.example.arturarzumanyan.taskmanager.ui.fragment.event.week.mvp;

import android.content.Context;
import android.util.SparseIntArray;

import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsRepository;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.WeeklyEventsSpecification;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;
import com.example.arturarzumanyan.taskmanager.networking.util.Log;
import com.example.arturarzumanyan.taskmanager.ui.util.ColorPalette;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.arturarzumanyan.taskmanager.networking.util.DateUtils.DAYS_IN_WEEK;
import static com.example.arturarzumanyan.taskmanager.networking.util.DateUtils.MINUTES_IN_HOUR;
import static com.example.arturarzumanyan.taskmanager.ui.fragment.event.daily.mvp.DailyEventsPresenterImpl.FAILED_TO_LOAD_EVENTS_MSG;

public class WeekDashboardPresenterImpl implements WeekDashboardContract.WeekDashboardPresenter {
    private WeekDashboardContract.WeekDashboardView mWeekDashboardView;
    private SparseIntArray mColorPaletteArray;
    private List<Event> mWeeklyEventsList;
    private Map<Date, List<Event>> mWeeklyEvents = new HashMap<>();
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public WeekDashboardPresenterImpl(WeekDashboardContract.WeekDashboardView weekDashboardView, Context context) {
        this.mWeekDashboardView = weekDashboardView;
        ColorPalette colorPalette = new ColorPalette(context);
        mColorPaletteArray = colorPalette.getColorPalette();
    }

    @Override
    public void attachView(WeekDashboardContract.WeekDashboardView weekDashboardView, Context context) {
        this.mWeekDashboardView = weekDashboardView;
        ColorPalette colorPalette = new ColorPalette(context);
        mColorPaletteArray = colorPalette.getColorPalette();
    }

    @Override
    public void loadWeeklyEvents() {
        WeeklyEventsSpecification weeklyEventsSpecification = new WeeklyEventsSpecification();
        EventsRepository eventsRepository = new EventsRepository();
        mCompositeDisposable.add(eventsRepository.getEvents(weeklyEventsSpecification)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(events -> {
                    Log.v("Weekly events loaded");

                    mWeeklyEventsList = events;
                    fetchWeeklyEventsWithDate();
                    if (mWeekDashboardView != null) {
                        mWeekDashboardView.setProgressBarInvisible();
                        processWeekDashboard();
                    }
                })
                .doOnError(throwable -> mWeekDashboardView.onFail(FAILED_TO_LOAD_EVENTS_MSG))
                .subscribe());
    }

    private List<Date> getDatesOfWeek() {
        int date = DateUtils.getEventWeek(DateUtils.getCurrentTime()) - 1;

        Date nextDate = DateUtils.getMondayDate(date - 1);
        List<Date> weekDateList = new ArrayList<>();

        for (int i = 0; i < DAYS_IN_WEEK; i++) {
            weekDateList.add(nextDate);
            if (nextDate != null) {
                nextDate = DateUtils.getNextDate(nextDate);
            }
        }
        return weekDateList;
    }

    @Override
    public void fetchWeeklyEventsWithDate() {
        for (Event event : mWeeklyEventsList) {
            Date eventDate = DateUtils.getEventDateWithoutTime(event.getStartTime());

            if (mWeeklyEvents.containsKey(eventDate)) {
                mWeeklyEvents.get(eventDate).add(event);
            } else {
                mWeeklyEvents.put(eventDate, new ArrayList<>(Collections.singletonList(event)));
            }
        }
    }

    @Override
    public void processWeekDashboard() {
        List<Date> weekDateList = getDatesOfWeek();
        List<Event> currentEventList;
        int lastMinute = 0;
        for (int i = 0; i < DAYS_IN_WEEK; i++) {
            currentEventList = mWeeklyEvents.get(weekDateList.get(i));
            if (currentEventList != null) {
                for (Event event : currentEventList) {
                    Date startTime = event.getStartTime();
                    int minutes = startTime.getHours() * MINUTES_IN_HOUR + startTime.getMinutes();
                    mWeekDashboardView.makeEmptiness(i, minutes - lastMinute);
                    mWeekDashboardView.makeEventPart(mColorPaletteArray.get(event.getColorId()), i,
                            event.getEndTime().getHours() * MINUTES_IN_HOUR + event.getEndTime().getMinutes()
                                    - event.getStartTime().getHours() * MINUTES_IN_HOUR - event.getStartTime().getMinutes());
                    lastMinute = event.getEndTime().getHours() * MINUTES_IN_HOUR + event.getEndTime().getMinutes();
                }
                lastMinute = 0;
            }
        }
    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
        mWeekDashboardView = null;
    }
}
