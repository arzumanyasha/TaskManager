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

import static com.example.arturarzumanyan.taskmanager.networking.util.DateUtils.DAYS_IN_WEEK;
import static com.example.arturarzumanyan.taskmanager.networking.util.DateUtils.MINUTES_IN_HOUR;

public class WeekDashboardPresenterImpl implements WeekDashboardContract.WeekDashboardPresenter {
    private WeekDashboardContract.WeekDashboardView mWeekDashboardView;
    private SparseIntArray mColorPaletteArray;
    private List<Event> mWeeklyEventsList;
    private Map<Date, List<Event>> mWeeklyEvents = new HashMap<>();

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
        eventsRepository.getEvents(weeklyEventsSpecification, new EventsRepository.OnEventsLoadedListener() {
            @Override
            public void onSuccess(List<Event> eventsList) {
                Log.v("Weekly events loaded");

                mWeeklyEventsList = eventsList;
                fetchWeeklyEventsWithDate();
                if (mWeekDashboardView != null) {
                    mWeekDashboardView.setProgressBarInvisible();
                    processWeekDashboard();
                }
            }

            @Override
            public void onFail(String message) {
                mWeekDashboardView.onFail(message);
            }

            @Override
            public void onPermissionDenied() {
                /** To-do: add realization with start signInActivity*/
            }
        });
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
            Date eventDate = DateUtils.getEventDateWithoutTime(
                    DateUtils.getEventDateFromString(event.getStartTime()));

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
                    Date startTime = DateUtils.getEventDateFromString(event.getStartTime());
                    Date endTime = DateUtils.getEventDateFromString(event.getEndTime());
                    int minutes;
                    if (startTime != null && endTime != null) {
                        minutes = startTime.getHours() * MINUTES_IN_HOUR + startTime.getMinutes();

                        mWeekDashboardView.makeEmptiness(i, minutes - lastMinute);
                        mWeekDashboardView.makeEventPart(mColorPaletteArray.get(event.getColorId()), i,
                                /*event.getEndTime()*/endTime.getHours() * MINUTES_IN_HOUR + /*event.getEndTime()*/endTime.getMinutes()
                                        - /*event.getStartTime()*/startTime.getHours() * MINUTES_IN_HOUR - /*event.getStartTime()*/startTime.getMinutes());
                        lastMinute = /*event.getEndTime()*/endTime.getHours() * MINUTES_IN_HOUR + /*event.getEndTime()*/endTime.getMinutes();
                    }
                }
                lastMinute = 0;
            }
        }
    }

    @Override
    public void unsubscribe() {
        mWeekDashboardView = null;
    }
}
