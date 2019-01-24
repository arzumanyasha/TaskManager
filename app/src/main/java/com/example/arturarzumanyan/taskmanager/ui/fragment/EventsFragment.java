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

import com.example.arturarzumanyan.taskmanager.R;

import static com.example.arturarzumanyan.taskmanager.ui.activity.IntentionActivity.EVENTS_KEY;

public class EventsFragment extends Fragment {
    private static final String BACK_STACK_ROOT_TAG = "root_fragment";
    private MenuItem mSelectedFragmentItem;

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

        BottomNavigationView bottomNav = view.findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        if(mSelectedFragmentItem == null){
            bottomNav.setSelectedItemId(R.id.nav_today);
        } else {
            bottomNav.setSelectedItemId(mSelectedFragmentItem.getItemId());
        }
        return view;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    requireFragmentManager().popBackStack(BACK_STACK_ROOT_TAG,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    setSelectedFragment(item);

                    return true;

                }
            };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setDailyEventsFragment(savedInstanceState);
    }

    private void setSelectedFragment(MenuItem item) {
        Fragment selectedFragment = null;
        mSelectedFragmentItem = item;

        switch (item.getItemId()) {
            case R.id.nav_week:
                selectedFragment = WeekDashboardFragment.newInstance();
                break;
            case R.id.nav_today:
                selectedFragment = DailyEventsFragment.newInstance();
                break;
            case R.id.nav_stats:
                selectedFragment = EventsStatisticFragment.newInstance();
                break;
        }

        requireFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .addToBackStack(BACK_STACK_ROOT_TAG)
                .commit();

    }

    private void setDailyEventsFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            requireFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new DailyEventsFragment())
                    .commit();
        }

        requireActivity().setTitle(EVENTS_KEY);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
