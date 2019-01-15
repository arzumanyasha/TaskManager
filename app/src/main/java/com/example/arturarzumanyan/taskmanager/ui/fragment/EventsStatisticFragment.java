package com.example.arturarzumanyan.taskmanager.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsRepository;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.EventsFromDateSpecification;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.MonthlyEventsSpecification;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.WeeklyEventsSpecification;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;
import com.example.arturarzumanyan.taskmanager.ui.adapter.ColorPalette;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import static com.example.arturarzumanyan.taskmanager.networking.util.DateUtils.MINUTES_IN_HOUR;

public class EventsStatisticFragment extends Fragment {
    private static final int MINUTES_IN_DAY = 1440;
    private static final int MINUTES_IN_WEEK = MINUTES_IN_DAY * 7;
    private static final int PERCENTAGE = 100;
    private static final int VALUE_IF_KEY_NOT_FOUND = -1;

    private PieChart pieChart;
    private Spinner spinnerMode;

    private EventsRepository mEventsRepository;

    private OnFragmentInteractionListener mListener;

    public EventsStatisticFragment() {
    }

    public static EventsStatisticFragment newInstance() {
        return new EventsStatisticFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events_statistic, container, false);
        pieChart = view.findViewById(R.id.pie_chart);
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

        mEventsRepository = new EventsRepository(getActivity());

        spinnerMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: {
                        getDailyEvents();
                        break;
                    }
                    case 1: {
                        getWeeklyEvents();
                        break;
                    }
                    case 2: {
                        getMonthlyEvents();
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void getDailyEvents() {
        EventsFromDateSpecification eventsFromDateSpecification = new EventsFromDateSpecification();
        eventsFromDateSpecification.setDate(DateUtils.getCurrentTime());
        mEventsRepository.getEvents(eventsFromDateSpecification, new EventsRepository.OnEventsLoadedListener() {
            @Override
            public void onSuccess(List<Event> eventsList) {
                createPieChart(eventsList, MINUTES_IN_DAY);
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getWeeklyEvents() {
        WeeklyEventsSpecification weeklyEventsSpecification = new WeeklyEventsSpecification();
        mEventsRepository.getEvents(weeklyEventsSpecification, new EventsRepository.OnEventsLoadedListener() {
            @Override
            public void onSuccess(List<Event> eventsList) {
                createPieChart(eventsList, MINUTES_IN_WEEK);
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getMonthlyEvents() {
        MonthlyEventsSpecification monthlyEventsSpecification = new MonthlyEventsSpecification();
        mEventsRepository.getEvents(monthlyEventsSpecification, new EventsRepository.OnEventsLoadedListener() {
            @Override
            public void onSuccess(List<Event> eventsList) {
                createPieChart(eventsList, DateUtils.getDaysInCurrentMonth() * MINUTES_IN_DAY);
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createPieChart(List<Event> events, int minutes) {
        List<Integer> colors = new ArrayList<>();
        SparseIntArray minutesOnEvents = new SparseIntArray();

        ColorPalette colorPalette = new ColorPalette(getActivity());
        for (Event event : events) {
            int eventTimeSpent = event.getEndTime().getHours() * MINUTES_IN_HOUR + event.getEndTime().getMinutes()
                    - event.getStartTime().getHours() * MINUTES_IN_HOUR - event.getStartTime().getMinutes();
            if (minutesOnEvents.get(event.getColorId(), VALUE_IF_KEY_NOT_FOUND) != VALUE_IF_KEY_NOT_FOUND) {
                minutesOnEvents.put(event.getColorId(), minutesOnEvents.get(event.getColorId()) + eventTimeSpent);
            } else {
                minutesOnEvents.put(event.getColorId(), eventTimeSpent);
            }
        }

        List<PieEntry> dailyEventsValues = new ArrayList<>();

        for (int i = 0; i < minutesOnEvents.size(); i++) {
            dailyEventsValues.add(new PieEntry(minutesOnEvents.valueAt(i) % minutes * PERCENTAGE,
                    minutesOnEvents.valueAt(i) / MINUTES_IN_HOUR + "h " +
                            minutesOnEvents.valueAt(i) % MINUTES_IN_HOUR + "m"));
            colors.add(colorPalette.getColorPalette().get(minutesOnEvents.keyAt(i)));
        }

        setPieChartData(dailyEventsValues, colors);
    }

    private void setPieChartData(List<PieEntry> dailyEventsValues, List<Integer> colors) {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic);

        PieDataSet pieDataSet = new PieDataSet(dailyEventsValues, "Time");

        pieDataSet.setSliceSpace(3f);
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setColors(colors);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.YELLOW);

        pieChart.setData(pieData);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
