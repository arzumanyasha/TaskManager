package com.example.arturarzumanyan.taskmanager.ui.fragment.event.container;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.arturarzumanyan.taskmanager.BuildConfig;
import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.TaskManagerApp;
import com.example.arturarzumanyan.taskmanager.networking.util.Log;
import com.example.arturarzumanyan.taskmanager.ui.activity.intention.IntentionActivity;
import com.example.arturarzumanyan.taskmanager.ui.fragment.event.container.mvp.BottomNavigationContract;
import com.example.arturarzumanyan.taskmanager.ui.fragment.event.container.mvp.BottomNavigationPresenterImpl;
import com.example.arturarzumanyan.taskmanager.ui.fragment.event.daily.DailyEventsFragment;
import com.example.arturarzumanyan.taskmanager.ui.fragment.event.statistic.EventsStatisticFragment;
import com.example.arturarzumanyan.taskmanager.ui.fragment.event.week.WeekDashboardFragment;
import com.squareup.leakcanary.RefWatcher;

public class EventsFragment extends Fragment implements BottomNavigationContract.BottomNavigationView {
    private static final String DAILY_FRAGMENT_TAG = "daily_fragment_tag";
    private static final String WEEK_DASHBOARD_FRAGMENT_TAG = "week_dashboard_fragment_tag";
    private static final String STATISTIC_FRAGMENT_TAG = "statistic_fragment_tag";
    private BottomNavigationView mBottomNav;
    private DailyEventsFragment mRetainedDailyEventsFragment;
    private WeekDashboardFragment mRetainedWeekDashboardFragment;
    private EventsStatisticFragment mRetainedEventsStatisticFragment;
    private BottomNavigationContract.BottomNavigationPresenter mBottomNavigationPresenter;

    public EventsFragment() {

    }

    public static EventsFragment newInstance() {
        return new EventsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        mBottomNav = view.findViewById(R.id.bottom_navigation);
        mBottomNav.setOnNavigationItemSelectedListener(getNavigationListener());

        if (mBottomNavigationPresenter == null) {
            mBottomNavigationPresenter = new BottomNavigationPresenterImpl(this);
            mBottomNavigationPresenter.processDefaultBottomNavigationMenu();
        } else {
            mBottomNavigationPresenter.attachView(this);
            mBottomNavigationPresenter.processRotatedStateOfBottomNavigationMenu();
        }
        return view;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener getNavigationListener() {
        return item -> {
            Log.v("Selected");
            setSelectedFragment(item);

            return true;
        };
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBottomNavigationPresenter.processAppTitle();
    }

    @Override
    public void updateAppTitle(String title) {
        requireActivity().setTitle(title);
    }

    @Override
    public void displayDefaultUi() {
        mBottomNav.setSelectedItemId(R.id.nav_today);
    }

    @Override
    public void displaySelectedFragment(int id) {
        mBottomNav.setSelectedItemId(id);
    }

    private void setSelectedFragment(MenuItem item) {
        mBottomNavigationPresenter.setCurrentFragmentId(item.getItemId());

        switch (item.getItemId()) {
            case R.id.nav_week:
                mBottomNavigationPresenter.processWeekDashboardClick();
                break;
            case R.id.nav_today:
                mBottomNavigationPresenter.processDailyEventsClick();
                break;
            case R.id.nav_stats:
                mBottomNavigationPresenter.processStatisticClick();
                break;
        }
    }

    @Override
    public void displayWeekDashboard() {
        mRetainedDailyEventsFragment = null;
        mRetainedEventsStatisticFragment = null;
        ((IntentionActivity) requireActivity()).setFloatingActionButtonInvisible();
        mRetainedWeekDashboardFragment = getRetainedWeekDashboardFragment();
        if (mRetainedWeekDashboardFragment == null) {
            mRetainedWeekDashboardFragment = WeekDashboardFragment.newInstance();
        }
        openRetainedFragment(mRetainedWeekDashboardFragment, WEEK_DASHBOARD_FRAGMENT_TAG);
    }

    @Override
    public void displayDailyEvents() {
        mRetainedWeekDashboardFragment = null;
        mRetainedEventsStatisticFragment = null;
        ((IntentionActivity) requireActivity()).setFloatingActionButtonVisible();
        mRetainedDailyEventsFragment = getRetainedDailyEventsFragment();
        if (mRetainedDailyEventsFragment == null) {
            mRetainedDailyEventsFragment = DailyEventsFragment.newInstance();
        }
        openRetainedFragment(mRetainedDailyEventsFragment, DAILY_FRAGMENT_TAG);
    }

    @Override
    public void displayStatistic() {
        mRetainedWeekDashboardFragment = null;
        mRetainedDailyEventsFragment = null;
        ((IntentionActivity) requireActivity()).setFloatingActionButtonInvisible();
        mRetainedEventsStatisticFragment = getRetainedEventsStatisticFragment();
        if (mRetainedEventsStatisticFragment == null) {
            mRetainedEventsStatisticFragment = EventsStatisticFragment.newInstance();
        }
        openRetainedFragment(mRetainedEventsStatisticFragment, STATISTIC_FRAGMENT_TAG);
    }

    private DailyEventsFragment getRetainedDailyEventsFragment() {
        mRetainedDailyEventsFragment = (DailyEventsFragment) requireFragmentManager().findFragmentByTag(DAILY_FRAGMENT_TAG);
        return mRetainedDailyEventsFragment;
    }

    private WeekDashboardFragment getRetainedWeekDashboardFragment() {
        mRetainedWeekDashboardFragment = (WeekDashboardFragment) requireFragmentManager().findFragmentByTag(WEEK_DASHBOARD_FRAGMENT_TAG);
        return mRetainedWeekDashboardFragment;
    }

    private EventsStatisticFragment getRetainedEventsStatisticFragment() {
        mRetainedEventsStatisticFragment = (EventsStatisticFragment) requireFragmentManager().findFragmentByTag(STATISTIC_FRAGMENT_TAG);
        return mRetainedEventsStatisticFragment;
    }

    private void openRetainedFragment(Fragment retainedFragment, String tag) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, retainedFragment, tag)
                .commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        mBottomNav.setOnNavigationItemSelectedListener(null);
        mRetainedDailyEventsFragment = null;
        mRetainedWeekDashboardFragment = null;
        mRetainedEventsStatisticFragment = null;
        mBottomNavigationPresenter.unsubscribe();
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (BuildConfig.DEBUG) {
            RefWatcher refWatcher = TaskManagerApp.getRefWatcher(requireActivity());
            refWatcher.watch(this);
        }
    }
}
