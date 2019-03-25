package com.example.arturarzumanyan.taskmanager.ui.fragment.event.container.mvp;

import static com.example.arturarzumanyan.taskmanager.ui.activity.intention.IntentionActivity.EVENTS_KEY;

public class BottomNavigationPresenterImpl implements BottomNavigationContract.BottomNavigationPresenter {
    private BottomNavigationContract.BottomNavigationView bottomNavigationView;
    private int mCurrentFragmentId;

    public BottomNavigationPresenterImpl(BottomNavigationContract.BottomNavigationView bottomNavigationView) {
        this.bottomNavigationView = bottomNavigationView;
    }

    @Override
    public void attachView(BottomNavigationContract.BottomNavigationView bottomNavigationView) {
        this.bottomNavigationView = bottomNavigationView;
    }

    @Override
    public void processAppTitle() {
        bottomNavigationView.updateAppTitle(EVENTS_KEY);
    }

    @Override
    public void processDefaultBottomNavigationMenu() {
        bottomNavigationView.displayDefaultUi();
    }

    @Override
    public void processRotatedStateOfBottomNavigationMenu() {
        bottomNavigationView.displaySelectedFragment(mCurrentFragmentId);
    }

    @Override
    public void processWeekDashboardClick() {
        bottomNavigationView.displayWeekDashboard();
    }

    @Override
    public void setCurrentFragmentId(int id) {
        mCurrentFragmentId = id;
    }

    @Override
    public void processDailyEventsClick() {
        bottomNavigationView.displayDailyEvents();
    }

    @Override
    public void processStatisticClick() {
        bottomNavigationView.displayStatistic();
    }

    @Override
    public void unsubscribe() {
        bottomNavigationView = null;
    }
}
