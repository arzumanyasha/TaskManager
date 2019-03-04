package com.example.arturarzumanyan.taskmanager.ui.fragment.event.statistic.mvp;

import android.content.Context;
import android.util.SparseIntArray;

import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsRepository;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.EventsFromDateSpecification;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.MonthlyEventsSpecification;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.WeeklyEventsSpecification;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;
import com.example.arturarzumanyan.taskmanager.ui.util.ColorPalette;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import static com.example.arturarzumanyan.taskmanager.networking.util.DateUtils.MINUTES_IN_HOUR;

public class EventsStatisticPresenterImpl implements EventsStatisticContract.EventsStatisticPresenter {
    private static final int MINUTES_IN_DAY = 1440;
    private static final int MINUTES_IN_WEEK = MINUTES_IN_DAY * 7;
    private static final int PERCENTAGE = 100;
    private static final int VALUE_IF_KEY_NOT_FOUND = -1;

    private EventsStatisticContract.EventsStatisticView mEventsStatisticView;
    private SparseIntArray mColorPaletteArray;
    private EventsRepository mEventsRepository;
    private Integer mCountOfMinutes;
    private List<Event> mEvents;

    public EventsStatisticPresenterImpl(EventsStatisticContract.EventsStatisticView mEventsStatisticView, Context context) {
        this.mEventsStatisticView = mEventsStatisticView;
        ColorPalette colorPalette = new ColorPalette(context);
        mColorPaletteArray = colorPalette.getColorPalette();
        mEventsRepository = new EventsRepository();
    }

    @Override
    public void attachView(EventsStatisticContract.EventsStatisticView eventsStatisticView, Context context) {
        this.mEventsStatisticView = eventsStatisticView;
        ColorPalette colorPalette = new ColorPalette(context);
        mColorPaletteArray = colorPalette.getColorPalette();
        mEventsRepository = new EventsRepository();
    }

    @Override
    public void loadDailyEvents() {
        mCountOfMinutes = MINUTES_IN_DAY;
        EventsFromDateSpecification eventsFromDateSpecification = new EventsFromDateSpecification();
        eventsFromDateSpecification.setDate(DateUtils.getCurrentTime());
        mEventsRepository.getEvents(eventsFromDateSpecification, new EventsRepository.OnEventsLoadedListener() {
            @Override
            public void onSuccess(List<Event> eventsList) {
                if (mEventsStatisticView != null) {
                    mEvents = eventsList;
                    if (mEvents.size() != 0) {
                        createPieChartData();
                    }
                }
            }

            @Override
            public void onFail(String message) {
                if (mEventsStatisticView != null) {
                    mEventsStatisticView.onFail(message);
                }
            }

            @Override
            public void onPermissionDenied() {
                /** To-do: add realization with start signInActivity*/
            }
        });
    }

    @Override
    public void loadWeeklyEvents() {
        mCountOfMinutes = MINUTES_IN_WEEK;
        WeeklyEventsSpecification weeklyEventsSpecification = new WeeklyEventsSpecification();
        mEventsRepository.getEvents(weeklyEventsSpecification, new EventsRepository.OnEventsLoadedListener() {
            @Override
            public void onSuccess(List<Event> eventsList) {
                if (mEventsStatisticView != null) {
                    mEvents = eventsList;
                    if (mEvents.size() != 0) {
                        createPieChartData();
                    }
                }
            }

            @Override
            public void onFail(String message) {
                if (mEventsStatisticView != null) {
                    mEventsStatisticView.onFail(message);
                }
            }

            @Override
            public void onPermissionDenied() {
                /** To-do: add realization with start signInActivity*/
            }
        });
    }

    @Override
    public void loadMonthlyEvents() {
        mCountOfMinutes = DateUtils.getDaysInCurrentMonth() * MINUTES_IN_DAY;
        MonthlyEventsSpecification monthlyEventsSpecification = new MonthlyEventsSpecification();
        mEventsRepository.getEvents(monthlyEventsSpecification, new EventsRepository.OnEventsLoadedListener() {
            @Override
            public void onSuccess(List<Event> eventsList) {
                if (mEventsStatisticView != null) {
                    mEvents = eventsList;
                    if (mEvents.size() != 0) {
                        createPieChartData();
                    }
                }
            }

            @Override
            public void onFail(String message) {
                if (mEventsStatisticView != null) {
                    mEventsStatisticView.onFail(message);
                }
            }

            @Override
            public void onPermissionDenied() {
                /** To-do: add realization with start signInActivity*/
            }
        });
    }

    @Override
    public void createPieChartData() {
        List<Integer> colors = new ArrayList<>();
        SparseIntArray minutesOnEvents = new SparseIntArray();

        for (Event event : mEvents) {
            int eventTimeSpent = event.getEndTime().getHours() * MINUTES_IN_HOUR + event.getEndTime().getMinutes()
                    - event.getStartTime().getHours() * MINUTES_IN_HOUR - event.getStartTime().getMinutes();
            int colorNumber = event.getColorId();
            if (minutesOnEvents.get(colorNumber, VALUE_IF_KEY_NOT_FOUND) != VALUE_IF_KEY_NOT_FOUND) {
                minutesOnEvents.put(colorNumber, minutesOnEvents.get(colorNumber) + eventTimeSpent);
            } else {
                minutesOnEvents.put(colorNumber, eventTimeSpent);
            }
        }

        List<PieEntry> dailyEventsValues = new ArrayList<>();

        for (int i = 0; i < minutesOnEvents.size(); i++) {
            int minutes = minutesOnEvents.valueAt(i);
            dailyEventsValues.add(new PieEntry(minutes % mCountOfMinutes * PERCENTAGE,
                    minutes / MINUTES_IN_HOUR + "h " +
                            minutes % MINUTES_IN_HOUR + "m"));
            colors.add(mColorPaletteArray.get(minutesOnEvents.keyAt(i)));
        }

        mEventsStatisticView.createPieChart(dailyEventsValues, colors);
    }

    @Override
    public void unsubscribe() {
        mEventsStatisticView = null;
    }
}
