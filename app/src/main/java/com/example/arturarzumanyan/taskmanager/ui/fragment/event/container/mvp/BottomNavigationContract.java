package com.example.arturarzumanyan.taskmanager.ui.fragment.event.container.mvp;

public class BottomNavigationContract {
    public interface BottomNavigationPresenter {
        void attachView(BottomNavigationView bottomNavigationView);

        void processDefaultBottomNavigationMenu();

        void processRotatedStateOfBottomNavigationMenu();

        void processWeekDashboardClick();

        void setCurrentFragmentId(int id);

        void processDailyEventsClick();

        void processStatisticClick();

        void unsubscribe();

        void processAppTitle();
    }

    public interface BottomNavigationView {
        void updateAppTitle(String title);

        void displayDefaultUi();

        void displaySelectedFragment(int id);

        void displayWeekDashboard();

        void displayDailyEvents();

        void displayStatistic();
    }
}
