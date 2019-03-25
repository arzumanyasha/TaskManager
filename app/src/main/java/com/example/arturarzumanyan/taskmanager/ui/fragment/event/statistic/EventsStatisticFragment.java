package com.example.arturarzumanyan.taskmanager.ui.fragment.event.statistic;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.arturarzumanyan.taskmanager.BuildConfig;
import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.TaskManagerApp;
import com.example.arturarzumanyan.taskmanager.ui.activity.BaseActivity;
import com.example.arturarzumanyan.taskmanager.ui.fragment.event.statistic.mvp.EventsStatisticContract;
import com.example.arturarzumanyan.taskmanager.ui.fragment.event.statistic.mvp.EventsStatisticPresenterImpl;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.squareup.leakcanary.RefWatcher;

import java.util.List;

public class EventsStatisticFragment extends Fragment implements EventsStatisticContract.EventsStatisticView {
    private PieChart pieChart;
    private Spinner spinnerMode;

    private EventsStatisticContract.EventsStatisticPresenter mEventsStatisticPresenter;

    public EventsStatisticFragment() {
    }

    public static EventsStatisticFragment newInstance() {
        return new EventsStatisticFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events_statistic, container, false);
        pieChart = view.findViewById(R.id.pie_chart);
        pieChart.setNoDataTextColor(Color.BLACK);
        pieChart.setNoDataText(requireActivity().getString(R.string.no_events_to_show_message));

        spinnerMode = view.findViewById(R.id.spinner_mode);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(requireActivity(),
                R.array.modes, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMode.setAdapter(arrayAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mEventsStatisticPresenter == null) {
            mEventsStatisticPresenter = new EventsStatisticPresenterImpl(this);
        } else {
            mEventsStatisticPresenter.attachView(this);
            mEventsStatisticPresenter.createPieChartData();
        }

        spinnerMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: {
                        mEventsStatisticPresenter.loadDailyEvents();
                        break;
                    }
                    case 1: {
                        mEventsStatisticPresenter.loadWeeklyEvents();
                        break;
                    }
                    case 2: {
                        mEventsStatisticPresenter.loadMonthlyEvents();
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void createPieChart(List<PieEntry> dailyEventsValues, List<Integer> colors) {
        if (isAdded()) {
            pieChart.setUsePercentValues(true);
            pieChart.getDescription().setEnabled(false);
            pieChart.setExtraOffsets(5, 10, 5, 5);
            pieChart.setDragDecelerationFrictionCoef(0.95f);
            pieChart.setDrawHoleEnabled(true);
            pieChart.setHoleColor(Color.WHITE);
            pieChart.setTransparentCircleRadius(61f);

            pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic);

            PieDataSet pieDataSet = new PieDataSet(dailyEventsValues, null);

            pieDataSet.setSliceSpace(3f);
            pieDataSet.setSelectionShift(5f);
            pieDataSet.setColors(colors);

            PieData pieData = new PieData(pieDataSet);
            pieData.setValueTextSize(10f);
            pieData.setValueTextColor(Color.YELLOW);

            pieChart.setData(pieData);
        }
    }

    @Override
    public void onFail(String message) {
        if (isAdded()) {
            ((BaseActivity) requireActivity()).onError(message);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        spinnerMode.setOnItemSelectedListener(null);
        mEventsStatisticPresenter.unsubscribe();
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
