package com.example.arturarzumanyan.taskmanager.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsRepository;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;
import com.example.arturarzumanyan.taskmanager.ui.adapter.ColorPalette;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.example.arturarzumanyan.taskmanager.networking.util.DateUtils.DAYS_IN_WEEK;
import static com.example.arturarzumanyan.taskmanager.networking.util.DateUtils.MINUTES_IN_HOUR;

public class WeekDashboardFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private LinearLayout mLinearLayoutMon;
    private LinearLayout mLinearLayoutTue;
    private LinearLayout mLinearLayoutWed;
    private LinearLayout mLinearLayoutThu;
    private LinearLayout mLinearLayoutFri;
    private LinearLayout mLinearLayoutSat;
    private LinearLayout mLinearLayoutSun;

    private OnFragmentInteractionListener mListener;

    public WeekDashboardFragment() {
    }

    public static WeekDashboardFragment newInstance(String param1, String param2) {
        WeekDashboardFragment fragment = new WeekDashboardFragment();
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
        View view = inflater.inflate(R.layout.fragment_week_dashboard, container, false);
        mLinearLayoutMon = view.findViewById(R.id.linearLayoutMon);
        mLinearLayoutTue = view.findViewById(R.id.linearLayoutTue);
        mLinearLayoutWed = view.findViewById(R.id.linearLayoutWed);
        mLinearLayoutThu = view.findViewById(R.id.linearLayoutThu);
        mLinearLayoutFri = view.findViewById(R.id.linearLayoutFri);
        mLinearLayoutSat = view.findViewById(R.id.linearLayoutSat);
        mLinearLayoutSun = view.findViewById(R.id.linearLayoutSun);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        int date = DateUtils.getEventWeek(DateUtils.getCurrentTime()) - 1;
        Date mondayDate = DateUtils.getMondayDate(date - 1);

        ArrayList<LinearLayout> linearLayouts = new ArrayList<>();
        linearLayouts.add(mLinearLayoutMon);
        linearLayouts.add(mLinearLayoutTue);
        linearLayouts.add(mLinearLayoutWed);
        linearLayouts.add(mLinearLayoutThu);
        linearLayouts.add(mLinearLayoutFri);
        linearLayouts.add(mLinearLayoutSat);
        linearLayouts.add(mLinearLayoutSun);

        Date nextDate = mondayDate;
        ArrayList<Date> weekDateList = new ArrayList<>();
        HashMap<Date, ArrayList<Event>> weeklyEvents = new HashMap<>();

        EventsRepository eventsRepository = new EventsRepository(getActivity());

        for (int i = 0; i < DAYS_IN_WEEK; i++) {
            weekDateList.add(nextDate);
            weeklyEvents.put(nextDate, eventsRepository.getEventsFromDate(nextDate));
            nextDate = DateUtils.getNextDate(nextDate);
        }

        int lastMinute = 0;
        for (int i = 0; i < DAYS_IN_WEEK; i++) {
            for (Event event : weeklyEvents.get(weekDateList.get(i))) {
                Date startTime = event.getStartTime();
                int minutes = startTime.getHours() * MINUTES_IN_HOUR + startTime.getMinutes();
                makeEmptiness(lastMinute, minutes, linearLayouts.get(i));
                makeEventPart(event, linearLayouts.get(i));
                lastMinute = event.getEndTime().getHours() * MINUTES_IN_HOUR + event.getEndTime().getMinutes();
            }
            lastMinute = 0;
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
