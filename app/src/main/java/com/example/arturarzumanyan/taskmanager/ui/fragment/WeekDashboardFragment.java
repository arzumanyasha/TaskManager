package com.example.arturarzumanyan.taskmanager.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsRepository;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.WeeklyEventsSpecification;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;
import com.example.arturarzumanyan.taskmanager.networking.util.Log;
import com.example.arturarzumanyan.taskmanager.ui.adapter.ColorPalette;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.arturarzumanyan.taskmanager.networking.util.DateUtils.DAYS_IN_WEEK;
import static com.example.arturarzumanyan.taskmanager.networking.util.DateUtils.MINUTES_IN_HOUR;

public class WeekDashboardFragment extends Fragment {
    private LinearLayout mLinearLayoutMon;
    private LinearLayout mLinearLayoutTue;
    private LinearLayout mLinearLayoutWed;
    private LinearLayout mLinearLayoutThu;
    private LinearLayout mLinearLayoutFri;
    private LinearLayout mLinearLayoutSat;
    private LinearLayout mLinearLayoutSun;

    private Map<Date, List<Event>> mWeeklyEvents = new HashMap<>();

    private OnFragmentInteractionListener mListener;

    public WeekDashboardFragment() {
    }

    public static WeekDashboardFragment newInstance() {
        return new WeekDashboardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_week_dashboard, container, false);
        mLinearLayoutMon = view.findViewById(R.id.linear_layout_mon);
        mLinearLayoutTue = view.findViewById(R.id.linear_layout_tue);
        mLinearLayoutWed = view.findViewById(R.id.linear_layout_wed);
        mLinearLayoutThu = view.findViewById(R.id.linear_layout_thu);
        mLinearLayoutFri = view.findViewById(R.id.linear_layout_fri);
        mLinearLayoutSat = view.findViewById(R.id.linear_layout_sat);
        mLinearLayoutSun = view.findViewById(R.id.linear_layout_sun);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        int date = DateUtils.getEventWeek(DateUtils.getCurrentTime()) - 1;
        Date mondayDate = DateUtils.getMondayDate(date - 1);

        final List<LinearLayout> linearLayouts = new ArrayList<>();
        linearLayouts.add(mLinearLayoutMon);
        linearLayouts.add(mLinearLayoutTue);
        linearLayouts.add(mLinearLayoutWed);
        linearLayouts.add(mLinearLayoutThu);
        linearLayouts.add(mLinearLayoutFri);
        linearLayouts.add(mLinearLayoutSat);
        linearLayouts.add(mLinearLayoutSun);

        Date nextDate = mondayDate;
        final List<Date> weekDateList = new ArrayList<>();

        WeeklyEventsSpecification weeklyEventsSpecification = new WeeklyEventsSpecification();
        EventsRepository eventsRepository = new EventsRepository(getActivity());
        eventsRepository.getEvents(weeklyEventsSpecification, new EventsRepository.OnEventsLoadedListener() {
            @Override
            public void onSuccess(List<Event> eventsList) {
                Log.v("Weekly events loaded");
                ArrayList<Event> dailyEventsList;
                for (Event event : eventsList) {
                    Date eventDate = DateUtils.getEventDate(DateUtils.getEventDate(event.getStartTime()));

                    if (mWeeklyEvents.containsKey(eventDate)) {
                        dailyEventsList = new ArrayList<>(mWeeklyEvents.get(eventDate));
                        dailyEventsList.add(event);
                        mWeeklyEvents.put(eventDate, dailyEventsList);
                    } else {
                        mWeeklyEvents.put(eventDate, Collections.singletonList(event));
                    }
                }
                displayDashboard(linearLayouts, mWeeklyEvents, weekDateList);
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        });

        for (int i = 0; i < DAYS_IN_WEEK; i++) {
            weekDateList.add(nextDate);
            nextDate = DateUtils.getNextDate(nextDate);
        }
    }

    private void displayDashboard(List<LinearLayout> linearLayouts,
                                  Map<Date, List<Event>> weeklyEvents,
                                  List<Date> weekDateList) {
        List<Event> currentEventList;
        int lastMinute = 0;
        for (int i = 0; i < DAYS_IN_WEEK; i++) {
            currentEventList = weeklyEvents.get(weekDateList.get(i));
            if (currentEventList != null) {
                for (Event event : currentEventList) {
                    Date startTime = event.getStartTime();
                    int minutes = startTime.getHours() * MINUTES_IN_HOUR + startTime.getMinutes();
                    makeEmptiness(lastMinute, minutes, linearLayouts.get(i));
                    makeEventPart(event, linearLayouts.get(i));
                    lastMinute = event.getEndTime().getHours() * MINUTES_IN_HOUR + event.getEndTime().getMinutes();
                }
                lastMinute = 0;
            }
        }
    }

    private void makeEmptiness(int startPosition, int endPosition, LinearLayout linearLayout) {
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        View view = new View(getActivity());
        lParams.weight = endPosition - startPosition;
        linearLayout.addView(view, lParams);
    }

    private void makeEventPart(Event event, LinearLayout linearLayout) {
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        View view = new View(getActivity());
        ColorPalette colorPalette = new ColorPalette(getActivity());
        view.setBackgroundColor(colorPalette.getColorPalette().get(event.getColorId()));
        lParams.weight = event.getEndTime().getHours() * MINUTES_IN_HOUR + event.getEndTime().getMinutes()
                - event.getStartTime().getHours() * MINUTES_IN_HOUR - event.getStartTime().getMinutes();
        linearLayout.addView(view, lParams);
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
