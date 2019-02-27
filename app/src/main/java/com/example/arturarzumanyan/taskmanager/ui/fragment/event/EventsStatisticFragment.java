package com.example.arturarzumanyan.taskmanager.ui.fragment.event;

import android.content.Context;
import android.graphics.Color;
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

import com.example.arturarzumanyan.taskmanager.BuildConfig;
import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.TaskManagerApp;
import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsRepository;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.EventsFromDateSpecification;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.MonthlyEventsSpecification;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.WeeklyEventsSpecification;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;
import com.example.arturarzumanyan.taskmanager.ui.activity.BaseActivity;
import com.example.arturarzumanyan.taskmanager.ui.util.ColorPalette;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.squareup.leakcanary.RefWatcher;

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

    private Integer mCountOfMinutes;
    private List<Event> mEvents;
    private SparseIntArray mColorPaletteArray;
    private EventsRepository mEventsRepository;

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

        ColorPalette colorPalette = new ColorPalette(getActivity());
        mColorPaletteArray = colorPalette.getColorPalette();

        mEventsRepository = new EventsRepository();

        spinnerMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: {
                        mCountOfMinutes = MINUTES_IN_DAY;
                        getDailyEvents(mCountOfMinutes);
                        break;
                    }
                    case 1: {
                        mCountOfMinutes = MINUTES_IN_WEEK;
                        getWeeklyEvents(mCountOfMinutes);
                        break;
                    }
                    case 2: {
                        mCountOfMinutes = DateUtils.getDaysInCurrentMonth() * MINUTES_IN_DAY;
                        getMonthlyEvents(mCountOfMinutes);
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //if(requireActivity().)

        if (mEvents != null) {
            createPieChart(mEvents, mCountOfMinutes);
        }
    }

    private void getDailyEvents(final int countOfMinutes) {
        EventsFromDateSpecification eventsFromDateSpecification = new EventsFromDateSpecification();
        eventsFromDateSpecification.setDate(DateUtils.getCurrentTime());
        mEventsRepository.getEvents(eventsFromDateSpecification, new EventsRepository.OnEventsLoadedListener() {
            @Override
            public void onSuccess(List<Event> eventsList) {
                updateUi(eventsList, countOfMinutes);
            }

            @Override
            public void onFail(String message) {
                if (isVisible()) {
                    ((BaseActivity) requireActivity()).onError(message);
                }
            }

            @Override
            public void onPermissionDenied() {
                /** To-do: add realization with start signInActivity*/
            }
        });
    }

    private void getWeeklyEvents(final int countOfMinutes) {
        WeeklyEventsSpecification weeklyEventsSpecification = new WeeklyEventsSpecification();
        mEventsRepository.getEvents(weeklyEventsSpecification, new EventsRepository.OnEventsLoadedListener() {
            @Override
            public void onSuccess(List<Event> eventsList) {
                updateUi(eventsList, countOfMinutes);
            }

            @Override
            public void onFail(String message) {
                if (isVisible()) {
                    ((BaseActivity) requireActivity()).onError(message);
                }
            }

            @Override
            public void onPermissionDenied() {
                /** To-do: add realization with start signInActivity*/
            }
        });
    }

    private void getMonthlyEvents(final int countOfMinutes) {
        MonthlyEventsSpecification monthlyEventsSpecification = new MonthlyEventsSpecification();
        mEventsRepository.getEvents(monthlyEventsSpecification, new EventsRepository.OnEventsLoadedListener() {
            @Override
            public void onSuccess(List<Event> eventsList) {
                updateUi(eventsList, countOfMinutes);
            }

            @Override
            public void onFail(String message) {
                if (isVisible()) {
                    ((BaseActivity) requireActivity()).onError(message);
                }
            }

            @Override
            public void onPermissionDenied() {
                /** To-do: add realization with start signInActivity*/
            }
        });
    }

    private void updateUi(List<Event> eventsList, int countOfMinutes) {
        if (isVisible()) {
            mEvents = eventsList;
            if (eventsList.size() != 0) {
                createPieChart(eventsList, countOfMinutes);
            }
        }
    }

    private void createPieChart(List<Event> events, int minutes) {
        List<Integer> colors = new ArrayList<>();
        SparseIntArray minutesOnEvents = new SparseIntArray();

        for (Event event : events) {
            int eventTimeSpent = event.getEndTime().getHours() * MINUTES_IN_HOUR + event.getEndTime().getMinutes()
                    - event.getStartTime().getHours() * MINUTES_IN_HOUR - event.getStartTime().getMinutes();
            int colorNumber = event.getColorId();
            if (minutesOnEvents.get(colorNumber, VALUE_IF_KEY_NOT_FOUND) != VALUE_IF_KEY_NOT_FOUND) {
                minutesOnEvents.put(colorNumber, minutesOnEvents.get(colorNumber) + eventTimeSpent);
            } else {
                minutesOnEvents.put(colorNumber, eventTimeSpent);
            }
        }

        List<PieEntry> dailyEventsValues = new ArrayList<>();

        for (int i = 0; i < minutesOnEvents.size(); i++) {
            int countOfMinutes = minutesOnEvents.valueAt(i);
            dailyEventsValues.add(new PieEntry(countOfMinutes % minutes * PERCENTAGE,
                    countOfMinutes / MINUTES_IN_HOUR + "h " +
                            countOfMinutes % MINUTES_IN_HOUR + "m"));
            colors.add(mColorPaletteArray.get(minutesOnEvents.keyAt(i)));
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

        PieDataSet pieDataSet = new PieDataSet(dailyEventsValues, null);

        pieDataSet.setSliceSpace(3f);
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setColors(colors);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.YELLOW);

        pieChart.setData(pieData);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        spinnerMode.setOnItemSelectedListener(null);
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
