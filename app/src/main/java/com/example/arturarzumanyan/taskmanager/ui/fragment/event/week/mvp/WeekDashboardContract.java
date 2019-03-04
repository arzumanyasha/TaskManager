package com.example.arturarzumanyan.taskmanager.ui.fragment.event.week.mvp;

import android.content.Context;

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

        void makeEmptiness(int layoutNumber, float weight);

        void makeEventPart(int color, int layoutNumber, float weight);

        void onFail(String message);


    }
}
