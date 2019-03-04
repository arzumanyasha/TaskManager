package com.example.arturarzumanyan.taskmanager.ui.fragment.event.daily;

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
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.util.Log;
import com.example.arturarzumanyan.taskmanager.ui.activity.BaseActivity;
import com.example.arturarzumanyan.taskmanager.ui.activity.intention.IntentionActivity;
import com.example.arturarzumanyan.taskmanager.ui.adapter.event.EventsAdapter;
import com.example.arturarzumanyan.taskmanager.ui.dialog.event.EventsDialog;
import com.example.arturarzumanyan.taskmanager.ui.fragment.event.daily.mvp.DailyEventsContract;
import com.example.arturarzumanyan.taskmanager.ui.fragment.event.daily.mvp.DailyEventsPresenterImpl;
import com.squareup.leakcanary.RefWatcher;

import java.util.List;

import static com.example.arturarzumanyan.taskmanager.ui.activity.intention.IntentionActivity.EVENTS_KEY;

public class DailyEventsFragment extends Fragment implements DailyEventsContract.DailyEventsView {
    private RecyclerView mEventsRecyclerView;
    private TextView mNoEventsTextView;
    private ProgressBar mProgressBar;
    private DailyEventsContract.DailyEventsPresenter mDailyEventsPresenter;

    private EventsAdapter mEventsAdapter;

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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("onCreateView");
        View view = inflater.inflate(R.layout.fragment_daily_events, container, false);
        mEventsRecyclerView = view.findViewById(R.id.recycler_events);
        mProgressBar = view.findViewById(R.id.daily_events_progress_bar);
        mNoEventsTextView = view.findViewById(R.id.text_view_no_events);

        if (mDailyEventsPresenter == null) {
            mDailyEventsPresenter = new DailyEventsPresenterImpl(this, requireActivity());
        } else {
            mDailyEventsPresenter.attachView(this);
            mDailyEventsPresenter.processRetainedState();
        }
        mDailyEventsPresenter.processDailyEvents();

        return view;
    }

    @Override
    public void setEventsAdapter(List<Event> events) {
        if (isAdded()) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            mEventsRecyclerView.setLayoutManager(layoutManager);

            mEventsAdapter = new EventsAdapter(mDailyEventsPresenter);

            ((IntentionActivity) requireActivity()).setEventFragmentInteractionListener(new IntentionActivity.EventFragmentInteractionListener() {
                @Override
                public void onEventsReady(List<Event> events) {
                    mDailyEventsPresenter.updateEventsList(events);
                }
            });

            mEventsRecyclerView.setAdapter(mEventsAdapter);
        }
    }

    @Override
    public void showEventUpdatingDialog(Event event) {
        EventsDialog eventsDialog = EventsDialog.newInstance(event);
        eventsDialog.setEventsReadyListener(new EventsDialog.EventsReadyListener() {
            @Override
            public void onEventsReady(List<Event> events) {
                mDailyEventsPresenter.updateEventsList(events);
            }
        });
        eventsDialog.show(requireFragmentManager(), EVENTS_KEY);
    }

    @Override
    public void updateEventsAdapter() {
        if (isAdded()) {
            mEventsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateEventsAdapterAfterDelete(int position) {
        if (isAdded()) {
            mEventsAdapter.notifyItemRemoved(position);
        }
    }

    @Override
    public void setProgressBarInvisible() {
        if (isAdded()) {
            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        }
    }

    @Override
    public void setNoEventsTextViewVisible() {
        if (isAdded()) {
            mNoEventsTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setNoEventsTextViewInvisible() {
        if (isAdded()) {
            mNoEventsTextView.setVisibility(View.INVISIBLE);
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
        Log.v("onAttach");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        ((IntentionActivity) requireActivity()).unsubscribeEventListeners();
        if (mEventsAdapter != null) {
            mEventsAdapter.unsubscribe();
        }
        mDailyEventsPresenter.unsubscribe();
        setProgressBarInvisible();
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
