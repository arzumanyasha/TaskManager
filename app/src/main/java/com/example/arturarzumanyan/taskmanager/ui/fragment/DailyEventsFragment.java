package com.example.arturarzumanyan.taskmanager.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsRepository;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;
import com.example.arturarzumanyan.taskmanager.ui.activity.IntentionActivity;
import com.example.arturarzumanyan.taskmanager.ui.adapter.EventsAdapter;
import com.example.arturarzumanyan.taskmanager.ui.dialog.EventsDialog;

import java.util.ArrayList;

import static com.example.arturarzumanyan.taskmanager.ui.activity.IntentionActivity.EVENTS_KEY;

public class DailyEventsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView mEventsRecyclerView;

    private String mParam1;
    private String mParam2;

    private EventsAdapter mEventsAdapter;

    private OnFragmentInteractionListener mListener;

    public DailyEventsFragment() {

    }

    public static DailyEventsFragment newInstance(String param1, String param2) {
        DailyEventsFragment fragment = new DailyEventsFragment();
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
        View view = inflater.inflate(R.layout.fragment_daily_events, container, false);
        mEventsRecyclerView = view.findViewById(R.id.recycler_events);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final EventsRepository eventsRepository = new EventsRepository(getActivity());

        ArrayList<Event> events = eventsRepository.getDailyEvents();
        setEventsAdapter(events, eventsRepository);
    }

    private void setEventsAdapter(ArrayList<Event> events, final EventsRepository eventsRepository) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mEventsRecyclerView.setLayoutManager(layoutManager);

        mEventsAdapter = new EventsAdapter(getActivity(), events, new EventsAdapter.OnItemClickListener() {
            @Override
            public void onItemDelete(Event event) {
                eventsRepository.deleteEvent(event);
            }

            @Override
            public void onItemClick(Event event) {
                openEventsDialog(event);
            }
        });

        ((IntentionActivity) getActivity()).setEventFragmentInteractionListener(new IntentionActivity.EventFragmentInteractionListener() {
            @Override
            public void onEventsReady(ArrayList<Event> events) {
                mEventsAdapter.updateList(events);
            }
        });
        mEventsRecyclerView.setAdapter(mEventsAdapter);
    }

    private void openEventsDialog(Event event) {
        EventsDialog eventsDialog = new EventsDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EVENTS_KEY, event);
        eventsDialog.setArguments(bundle);
        eventsDialog.setEventsReadyListener(new EventsDialog.EventsReadyListener() {
            @Override
            public void onEventsReady(ArrayList<Event> events) {
                mEventsAdapter.updateList(events);
            }
        });
        eventsDialog.show(getFragmentManager(), EVENTS_KEY);
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
