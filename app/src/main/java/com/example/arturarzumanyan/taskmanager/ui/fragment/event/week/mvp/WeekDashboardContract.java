package com.example.arturarzumanyan.taskmanager.ui.fragment.event.week.mvp;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.domain.Event;

public class WeekDashboardContract {
    public interface WeekDashboardPresenter {
        void attachView(WeekDashboardView weekDashboardView, Context context);

        void loadWeeklyEvents();

        void fetchWeeklyEventsWithDate();

        void processWeekDashboard();

        void unsubscribe();
    }

    public interface WeekDashboardView {
        void setProgressBarInvisible();

        void makeEmptiness(int startPosition, int endPosition, int layoutNumber);

        void makeEventPart(Event event, int color, int layoutNumber);

        void onFail(String message);


    }
}
