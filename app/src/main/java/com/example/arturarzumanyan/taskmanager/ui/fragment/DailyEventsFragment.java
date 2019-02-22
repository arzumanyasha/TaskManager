package com.example.arturarzumanyan.taskmanager.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.arturarzumanyan.taskmanager.BuildConfig;
import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.TaskManagerApp;
import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsRepository;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.EventsFromDateSpecification;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;
import com.example.arturarzumanyan.taskmanager.networking.util.Log;
import com.example.arturarzumanyan.taskmanager.ui.activity.BaseActivity;
import com.example.arturarzumanyan.taskmanager.ui.activity.intention.IntentionActivity;
import com.example.arturarzumanyan.taskmanager.ui.adapter.EventsAdapter;
import com.example.arturarzumanyan.taskmanager.ui.dialog.EventsDialog;
import com.squareup.leakcanary.RefWatcher;

import java.util.List;

import static com.example.arturarzumanyan.taskmanager.ui.activity.intention.IntentionActivity.EVENTS_KEY;

public class DailyEventsFragment extends Fragment {
    private RecyclerView mEventsRecyclerView;
    private TextView mNoEventsTextView;
    private ProgressBar mProgressBar;
    private EventsRepository mEventsRepository;

    private EventsAdapter mEventsAdapter;
    private List<Event> mDailyEventsList;

    public DailyEventsFragment() {

    }

    public static DailyEventsFragment newInstance() {
        return new DailyEventsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v("onCreate");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        initEventsRepository();
    }

    private void initEventsRepository() {
        mEventsRepository = new EventsRepository();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("onCreateView");
        View view = inflater.inflate(R.layout.fragment_daily_events, container, false);
        mEventsRecyclerView = view.findViewById(R.id.recycler_events);
        mProgressBar = view.findViewById(R.id.daily_events_progress_bar);
        mNoEventsTextView = view.findViewById(R.id.text_view_no_events);

        if (mDailyEventsList == null) {
            loadDailyEvents();
        } else {
            setEventsAdapter(mDailyEventsList);
        }

        return view;
    }

    private void loadDailyEvents() {
        final EventsFromDateSpecification eventsFromDateSpecification = new EventsFromDateSpecification();
        eventsFromDateSpecification.setDate(DateUtils.getCurrentTime());
        mEventsRepository.getEvents(eventsFromDateSpecification, new EventsRepository.OnEventsLoadedListener() {
            @Override
            public void onSuccess(List<Event> eventsList) {
                if (isVisible()) {
                    mDailyEventsList = eventsList;
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    setEventsAdapter(eventsList);
                    if (eventsList.isEmpty()) {
                        mNoEventsTextView.setVisibility(View.VISIBLE);
                    }
                }
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

    private void setEventsAdapter(List<Event> events) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mEventsRecyclerView.setLayoutManager(layoutManager);

        mEventsAdapter = new EventsAdapter(getActivity(), events, new EventsAdapter.OnItemClickListener() {
            @Override
            public void onItemDelete(Event event) {
                deleteEvent(mEventsRepository, event);
            }

            @Override
            public void onItemClick(Event event) {
                openEventsDialog(event);
            }
        });

        ((IntentionActivity) requireActivity()).setEventFragmentInteractionListener(new IntentionActivity.EventFragmentInteractionListener() {
            @Override
            public void onEventsReady(List<Event> events) {
                mNoEventsTextView.setVisibility(View.INVISIBLE);
                mEventsAdapter.updateList(events);
            }
        });

        mEventsRecyclerView.setAdapter(mEventsAdapter);
    }

    private void deleteEvent(EventsRepository eventsRepository, Event event) {
        eventsRepository.deleteEvent(event, new EventsRepository.OnEventsLoadedListener() {
            @Override
            public void onSuccess(List<Event> eventsList) {
                mDailyEventsList = eventsList;
                if (eventsList.isEmpty()) {
                    mNoEventsTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFail(String message) {
                ((BaseActivity) requireActivity()).onError(message);
            }

            @Override
            public void onPermissionDenied() {
                /** To-do: add realization with start signInActivity*/
            }
        });
    }

    private void openEventsDialog(Event event) {
        EventsDialog eventsDialog = EventsDialog.newInstance(event);
        eventsDialog.setEventsReadyListener(new EventsDialog.EventsReadyListener() {
            @Override
            public void onEventsReady(List<Event> events) {
                mNoEventsTextView.setVisibility(View.INVISIBLE);
                mEventsAdapter.updateList(events);
            }
        });
        eventsDialog.show(requireFragmentManager(), EVENTS_KEY);
    }

    @Override
    public void onAttach(Context context) {
        Log.v("onAttach");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        ((IntentionActivity) requireActivity()).unsubscribeEventListeners();
        if (mEventsAdapter != null) {
            mEventsAdapter.unsubscribe();
        }
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
