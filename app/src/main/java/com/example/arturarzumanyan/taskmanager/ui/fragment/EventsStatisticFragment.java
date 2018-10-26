package com.example.arturarzumanyan.taskmanager.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsRepository;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;
import com.example.arturarzumanyan.taskmanager.ui.adapter.ColorPalette;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.arturarzumanyan.taskmanager.networking.util.DateUtils.MINUTES_IN_HOUR;

public class EventsStatisticFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int MINUTES_IN_DAY = 1440;
    private static final int MINUTES_IN_WEEK = MINUTES_IN_DAY * 7;
    private static final int PERCENTAGE = 100;

    private String mParam1;
    private String mParam2;

    private PieChart pieChart;
    private Spinner spinnerMode;

    private EventsRepository mEventsRepository;

    private OnFragmentInteractionListener mListener;

    public EventsStatisticFragment() {
    }

    public static EventsStatisticFragment newInstance(String param1, String param2) {
        EventsStatisticFragment fragment = new EventsStatisticFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events_statistic, container, false);
        pieChart = view.findViewById(R.id.pieChart);
        spinnerMode = view.findViewById(R.id.spinnerMode);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getActivity(),
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
                if (position == 0) {
                    makePieChart(mEventsRepository.getDailyEvents(), MINUTES_IN_DAY);
                } else if (position == 1) {
                    makePieChart(mEventsRepository.getWeeklyEvents(), MINUTES_IN_WEEK);
                } else {
                    makePieChart(mEventsRepository.getMonthlyEvents(), DateUtils.getDaysInCurrentMonth() * MINUTES_IN_DAY);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void makePieChart(ArrayList<Event> events, int minutes) {
        ArrayList<Integer> mColors = new ArrayList<>();
        HashMap<Integer, Integer> mMinutesOnEvents = new HashMap<>();

        ColorPalette colorPalette = new ColorPalette(getActivity());
        for (Event event : events) {
            int eventTimeSpent = event.getEndTime().getHours() * MINUTES_IN_HOUR + event.getEndTime().getMinutes()
                    - event.getStartTime().getHours() * MINUTES_IN_HOUR - event.getStartTime().getMinutes();
            if (mMinutesOnEvents.containsKey(event.getColorId())) {
                mMinutesOnEvents.put(event.getColorId(), mMinutesOnEvents.get(event.getColorId()) + eventTimeSpent);
            } else {
                mMinutesOnEvents.put(event.getColorId(), eventTimeSpent);
            }
        }

        ArrayList<PieEntry> dailyEventsValues = new ArrayList<>();

        for (HashMap.Entry<Integer, Integer> map : mMinutesOnEvents.entrySet()) {
            dailyEventsValues.add(new PieEntry(map.getValue() % minutes * PERCENTAGE,
                    map.getValue() / MINUTES_IN_HOUR + "h " + map.getValue() % MINUTES_IN_HOUR + "m"));
            mColors.add(colorPalette.getColorPalette().get(map.getKey()));
        }

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
        pieDataSet.setColors(mColors);

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
