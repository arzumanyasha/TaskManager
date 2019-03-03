package com.example.arturarzumanyan.taskmanager.ui.fragment.event.week;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.arturarzumanyan.taskmanager.BuildConfig;
import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.TaskManagerApp;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.ui.activity.BaseActivity;
import com.example.arturarzumanyan.taskmanager.ui.fragment.event.week.mvp.WeekDashboardContract;
import com.example.arturarzumanyan.taskmanager.ui.fragment.event.week.mvp.WeekDashboardPresenterImpl;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;
import java.util.List;

import static com.example.arturarzumanyan.taskmanager.networking.util.DateUtils.MINUTES_IN_HOUR;

public class WeekDashboardFragment extends Fragment implements WeekDashboardContract.WeekDashboardView {
    private ProgressBar progressBar;
    private List<LinearLayout> mLinearLayouts;

    private WeekDashboardContract.WeekDashboardPresenter mWeekDashboardPresenter;

    public WeekDashboardFragment() {
    }

    public static WeekDashboardFragment newInstance() {
        return new WeekDashboardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week_dashboard, container, false);
        progressBar = view.findViewById(R.id.week_dashboard_progress_bar);
        LinearLayout mLinearLayoutMon = view.findViewById(R.id.linear_layout_mon);
        LinearLayout mLinearLayoutTue = view.findViewById(R.id.linear_layout_tue);
        LinearLayout mLinearLayoutWed = view.findViewById(R.id.linear_layout_wed);
        LinearLayout mLinearLayoutThu = view.findViewById(R.id.linear_layout_thu);
        LinearLayout mLinearLayoutFri = view.findViewById(R.id.linear_layout_fri);
        LinearLayout mLinearLayoutSat = view.findViewById(R.id.linear_layout_sat);
        LinearLayout mLinearLayoutSun = view.findViewById(R.id.linear_layout_sun);

        mLinearLayouts = new ArrayList<>();
        mLinearLayouts.add(mLinearLayoutMon);
        mLinearLayouts.add(mLinearLayoutTue);
        mLinearLayouts.add(mLinearLayoutWed);
        mLinearLayouts.add(mLinearLayoutThu);
        mLinearLayouts.add(mLinearLayoutFri);
        mLinearLayouts.add(mLinearLayoutSat);
        mLinearLayouts.add(mLinearLayoutSun);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mWeekDashboardPresenter == null) {
            mWeekDashboardPresenter = new WeekDashboardPresenterImpl(this, requireActivity());
            mWeekDashboardPresenter.loadWeeklyEvents();
        } else {
            mWeekDashboardPresenter.attachView(this, requireActivity());
            mWeekDashboardPresenter.fetchWeeklyEventsWithDate();
            mWeekDashboardPresenter.processWeekDashboard();
        }
    }

    @Override
    public void setProgressBarInvisible() {
        if (isAdded()) {
            progressBar.setVisibility(ProgressBar.INVISIBLE);
        }
    }

    @Override
    public void onFail(String message) {
        if (isAdded()) {
            ((BaseActivity) requireActivity()).onError(message);
        }
    }

    @Override
    public void makeEmptiness(int startPosition, int endPosition, int layoutNumber) {
        if (isAdded()) {
            LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            View view = new View(getActivity());
            lParams.weight = endPosition - startPosition;
            mLinearLayouts.get(layoutNumber).addView(view, lParams);
        }
    }

    @Override
    public void makeEventPart(Event event, int color, int layoutNumber) {
        if (isAdded()) {
            LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            View view = new View(getActivity());
            view.setBackgroundColor(color);
            lParams.weight = event.getEndTime().getHours() * MINUTES_IN_HOUR + event.getEndTime().getMinutes()
                    - event.getStartTime().getHours() * MINUTES_IN_HOUR - event.getStartTime().getMinutes();
            mLinearLayouts.get(layoutNumber).addView(view, lParams);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mWeekDashboardPresenter.unsubscribe();
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
