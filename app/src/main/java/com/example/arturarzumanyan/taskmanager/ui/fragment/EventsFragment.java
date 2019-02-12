package com.example.arturarzumanyan.taskmanager.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.arturarzumanyan.taskmanager.BuildConfig;
import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.TaskManagerApp;
import com.example.arturarzumanyan.taskmanager.networking.util.Log;
import com.squareup.leakcanary.RefWatcher;

import static com.example.arturarzumanyan.taskmanager.ui.activity.IntentionActivity.EVENTS_KEY;

public class EventsFragment extends Fragment {
    private static final String BACK_STACK_ROOT_TAG = "root_fragment";
    private static final String DAILY_FRAGMENT_TAG = "daily_fragment_tag";
    private static final String WEEK_DASHBOARD_FRAGMENT_TAG = "week_dashboard_fragment_tag";
    private static final String STATISTIC_FRAGMENT_TAG = "statistic_fragment_tag";
    private MenuItem mSelectedFragmentItem;
    private BottomNavigationView mBottomNav;
    private DailyEventsFragment mRetainedDailyEventsFragment;
    private WeekDashboardFragment mRetainedWeekDashboardFragment;
    private EventsStatisticFragment mRetainedEventsStatisticFragment;

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
        mBottomNav.setOnNavigationItemSelectedListener(navListener);

        if (mSelectedFragmentItem == null) {
            mBottomNav.setSelectedItemId(R.id.nav_today);
        } else {
            mBottomNav.setSelectedItemId(mSelectedFragmentItem.getItemId());
        }
        return view;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Log.v("Selected");
                    requireFragmentManager().popBackStack(BACK_STACK_ROOT_TAG,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    setSelectedFragment(item);

                    return true;

                }
            };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        requireActivity().setTitle(EVENTS_KEY);
    }

    private void setSelectedFragment(MenuItem item) {
        mSelectedFragmentItem = item;

        switch (item.getItemId()) {
            case R.id.nav_week:
                mRetainedDailyEventsFragment = null;
                mRetainedEventsStatisticFragment = null;
                displayRetainedWeekDashboardFragment();
                break;
            case R.id.nav_today:
                mRetainedWeekDashboardFragment = null;
                mRetainedEventsStatisticFragment = null;
                displayRetainedDailyEventsFragment();
                break;
            case R.id.nav_stats:
                mRetainedWeekDashboardFragment = null;
                mRetainedDailyEventsFragment = null;
                displayRetainedEventsStatisticFragment();
                break;
        }
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

    private void displayRetainedDailyEventsFragment() {
        mRetainedDailyEventsFragment = getRetainedDailyEventsFragment();
        if (mRetainedDailyEventsFragment == null) {
            mRetainedDailyEventsFragment = DailyEventsFragment.newInstance();
        }
        openRetainedFragment(mRetainedDailyEventsFragment, DAILY_FRAGMENT_TAG);
    }

    private void displayRetainedWeekDashboardFragment() {
        mRetainedWeekDashboardFragment = getRetainedWeekDashboardFragment();
        if (mRetainedWeekDashboardFragment == null) {
            mRetainedWeekDashboardFragment = WeekDashboardFragment.newInstance();
        }
        openRetainedFragment(mRetainedWeekDashboardFragment, WEEK_DASHBOARD_FRAGMENT_TAG);
    }

    private void displayRetainedEventsStatisticFragment() {
        mRetainedEventsStatisticFragment = getRetainedEventsStatisticFragment();
        if (mRetainedEventsStatisticFragment == null) {
            mRetainedEventsStatisticFragment = EventsStatisticFragment.newInstance();
        }
        openRetainedFragment(mRetainedEventsStatisticFragment, STATISTIC_FRAGMENT_TAG);
    }

    private void openRetainedFragment(Fragment retainedFragment, String tag) {
        if (!retainedFragment.isAdded()) {
            Log.v(retainedFragment.toString() + " is not added");
            requireFragmentManager().beginTransaction()
                    .add(retainedFragment, tag)
                    .replace(R.id.fragment_container, retainedFragment)
                    .commit();
        } else {
            Log.v(retainedFragment.toString() + " is added");
            requireFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, retainedFragment)
                    .commit();
        }
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
        navListener = null;
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
