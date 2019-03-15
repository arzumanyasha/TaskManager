package com.example.arturarzumanyan.taskmanager.ui.fragment.event.statistic.mvp;

import android.util.SparseIntArray;

import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsRepository;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.EventsFromDateSpecification;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.EventsSpecification;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.MonthlyEventsSpecification;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.WeeklyEventsSpecification;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;
import com.example.arturarzumanyan.taskmanager.ui.util.ResourceManager;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.arturarzumanyan.taskmanager.networking.util.DateUtils.MINUTES_IN_HOUR;
import static com.example.arturarzumanyan.taskmanager.ui.util.ResourceManager.getResourceManager;

public class EventsStatisticPresenterImpl implements EventsStatisticContract.EventsStatisticPresenter {
    private static final int MINUTES_IN_DAY = 1440;
    private static final int MINUTES_IN_WEEK = MINUTES_IN_DAY * 7;
    private static final int PERCENTAGE = 100;
    private static final int VALUE_IF_KEY_NOT_FOUND = -1;

    private EventsStatisticContract.EventsStatisticView mEventsStatisticView;
    private SparseIntArray mColorPaletteArray;
    private EventsRepository mEventsRepository;
    private Integer mCountOfMinutes;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private List<Event> mEvents;

    public EventsStatisticPresenterImpl(EventsStatisticContract.EventsStatisticView mEventsStatisticView) {
        this.mEventsStatisticView = mEventsStatisticView;
        mColorPaletteArray = getResourceManager().getColorPalette();
        mEventsRepository = new EventsRepository();
    }

    @Override
    public void attachView(EventsStatisticContract.EventsStatisticView eventsStatisticView) {
        this.mEventsStatisticView = eventsStatisticView;
        mColorPaletteArray = getResourceManager().getColorPalette();
        mEventsRepository = new EventsRepository();
    }

    @Override
    public void loadDailyEvents() {
        mCountOfMinutes = MINUTES_IN_DAY;
        EventsFromDateSpecification eventsFromDateSpecification = new EventsFromDateSpecification();
        eventsFromDateSpecification.setDate(DateUtils.getCurrentTime());
        loadEvents(eventsFromDateSpecification);

    }

    @Override
    public void loadWeeklyEvents() {
        mCountOfMinutes = MINUTES_IN_WEEK;
        WeeklyEventsSpecification weeklyEventsSpecification = new WeeklyEventsSpecification();
        loadEvents(weeklyEventsSpecification);
    }

    @Override
    public void loadMonthlyEvents() {
        mCountOfMinutes = DateUtils.getDaysInCurrentMonth() * MINUTES_IN_DAY;
        MonthlyEventsSpecification monthlyEventsSpecification = new MonthlyEventsSpecification();
        loadEvents(monthlyEventsSpecification);
    }

    private void loadEvents(EventsSpecification eventsSpecification) {
        mCompositeDisposable.add(mEventsRepository.getEvents(eventsSpecification)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(events -> {
                    if (mEventsStatisticView != null) {
                        mEvents = events;
                        if (mEvents.size() != 0) {
                            createPieChartData();
                        }
                    }
                })
                .doOnError(throwable -> {
                    if (mEventsStatisticView != null) {
                        mEventsStatisticView.onFail(getResourceManager().getErrorMessage(ResourceManager.State.FAILED_TO_LOAD_EVENTS_ERROR));
                    }
                })
                .subscribe()
        );
    }

    @Override
    public void createPieChartData() {
        List<Integer> colors = new ArrayList<>();
        SparseIntArray minutesOnEvents = new SparseIntArray();

        for (Event event : mEvents) {
            int eventTimeSpent = DateUtils.getEventDateFromString(event.getEndTime()).getHours() *
                    MINUTES_IN_HOUR + DateUtils.getEventDateFromString(event.getEndTime()).getMinutes()
                    - DateUtils.getEventDateFromString(event.getStartTime()).getHours() *
                    MINUTES_IN_HOUR - DateUtils.getEventDateFromString(event.getStartTime()).getMinutes();
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
        mCompositeDisposable.clear();
        mEventsStatisticView = null;
    }
}
