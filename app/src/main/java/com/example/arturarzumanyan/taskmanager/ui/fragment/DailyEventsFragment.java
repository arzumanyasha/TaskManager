package com.example.arturarzumanyan.taskmanager.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsRepository;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.EventsFromDateSpecification;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;
import com.example.arturarzumanyan.taskmanager.ui.activity.IntentionActivity;
import com.example.arturarzumanyan.taskmanager.ui.adapter.EventsAdapter;
import com.example.arturarzumanyan.taskmanager.ui.dialog.EventsDialog;

import java.util.List;

import static com.example.arturarzumanyan.taskmanager.ui.activity.IntentionActivity.EVENTS_KEY;

public class DailyEventsFragment extends Fragment {
    private RecyclerView mEventsRecyclerView;

    private EventsAdapter mEventsAdapter;

    private OnFragmentInteractionListener mListener;

    public DailyEventsFragment() {

    }

    public static DailyEventsFragment newInstance() {
        return new DailyEventsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_events, container, false);
        mEventsRecyclerView = view.findViewById(R.id.recycler_events);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadDailyEvents();
    }

    private void loadDailyEvents() {
        final EventsRepository eventsRepository = new EventsRepository(getActivity());

        final EventsFromDateSpecification eventsFromDateSpecification = new EventsFromDateSpecification();
        eventsFromDateSpecification.setDate(DateUtils.getCurrentTime());
        eventsRepository.getEvents(eventsFromDateSpecification, new EventsRepository.OnEventsLoadedListener() {
            @Override
            public void onSuccess(List<Event> eventsList) {
                setEventsAdapter(eventsList, eventsRepository);
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setEventsAdapter(List<Event> events, final EventsRepository eventsRepository) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mEventsRecyclerView.setLayoutManager(layoutManager);

        mEventsAdapter = new EventsAdapter(getActivity(), events, new EventsAdapter.OnItemClickListener() {
            @Override
            public void onItemDelete(Event event) {
                deleteEvent(eventsRepository, event);
            }

            @Override
            public void onItemClick(Event event) {
                openEventsDialog(event);
            }
        });

        ((IntentionActivity) requireActivity()).setEventFragmentInteractionListener(new IntentionActivity.EventFragmentInteractionListener() {
            @Override
            public void onEventsReady(List<Event> events) {
                mEventsAdapter.updateList(events);
            }
        });

        mEventsRecyclerView.setAdapter(mEventsAdapter);
    }

    private void deleteEvent(EventsRepository eventsRepository, Event event){
        eventsRepository.deleteEvent(event, new EventsRepository.OnEventsLoadedListener() {
            @Override
            public void onSuccess(List<Event> eventsList) {

            }

            @Override
            public void onFail(String message) {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openEventsDialog(Event event) {
        EventsDialog eventsDialog = EventsDialog.newInstance(event);
        eventsDialog.setEventsReadyListener(new EventsDialog.EventsReadyListener() {
            @Override
            public void onEventsReady(List<Event> events) {
                mEventsAdapter.updateList(events);
            }
        });
        if (getFragmentManager() != null) {
            eventsDialog.show(getFragmentManager(), EVENTS_KEY);
        }
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
