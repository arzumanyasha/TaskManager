package com.example.arturarzumanyan.taskmanager.ui.fragment.event.container.mvp;

public class BottomNavigationContract {
    public interface BottomNavigationPresenter {
        void attachView(BottomNavigationView bottomNavigationView);

        void processWeekDashboardClick();

        void processDailyEventsClick();

        void processStatisticClick();

        void unsubscribe();

        void processAppTitle();
    }

    public interface BottomNavigationView {
        void updateAppTitle(String title);

        void displayWeekDashboard();

        void displayDailyEvents();

        void displayStatistic();
    }
}
