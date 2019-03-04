package com.example.arturarzumanyan.taskmanager.ui.fragment.event.statistic.mvp;

import android.content.Context;

import com.github.mikephil.charting.data.PieEntry;

import java.util.List;

public class EventsStatisticContract {
    public interface EventsStatisticPresenter {
        void attachView(EventsStatisticView eventsStatisticView, Context context);

        void loadDailyEvents();

        void loadWeeklyEvents();

        void loadMonthlyEvents();

        void createPieChartData();

        void unsubscribe();
    }

    public interface EventsStatisticView {
        void createPieChart(List<PieEntry> dailyEventsValues, List<Integer> colors);

        void onFail(String message);
    }
}
