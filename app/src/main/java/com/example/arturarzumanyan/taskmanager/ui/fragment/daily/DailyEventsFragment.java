package com.example.arturarzumanyan.taskmanager.ui.fragment.daily;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
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
import com.example.arturarzumanyan.taskmanager.ui.adapter.event.mvp.EventsListPresenter;
import com.example.arturarzumanyan.taskmanager.ui.fragment.daily.mvp.contract.DailyEventsContract;
import com.example.arturarzumanyan.taskmanager.ui.fragment.daily.mvp.presenter.DailyEventsPresenterImpl;
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
            mDailyEventsPresenter = new DailyEventsPresenterImpl(this);
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

            EventsListPresenter eventsListPresenter = new EventsListPresenter(events, getActivity(), new EventsListPresenter.OnItemClickListener() {
                @Override
                public void onItemDelete(Event event) {
                    mDailyEventsPresenter.deleteEvent(event);
                }

                @Override
                public void onItemClick(Event event) {
                    mDailyEventsPresenter.processEventDialog(event);
                }
            });

            /*mEventsAdapter = new EventsAdapter(getActivity(), events, new EventsListPresenter.OnItemClickListener() {
                @Override
                public void onItemDelete(Event event) {
                    mDailyEventsPresenter.deleteEvent(event);
                }

                @Override
                public void onItemClick(Event event) {
                    mDailyEventsPresenter.processEventDialog(event);
                }
            });*/
            mEventsAdapter = new EventsAdapter(eventsListPresenter);

            ((IntentionActivity) requireActivity()).setEventFragmentInteractionListener(new IntentionActivity.EventFragmentInteractionListener() {
                @Override
                public void onEventsReady(List<Event> events) {
                    mDailyEventsPresenter.processUpdatedEvents(events);
                }
            });

            mEventsRecyclerView.setAdapter(mEventsAdapter);
        }
    }

    @Override
    public void showDialog(DialogFragment dialogFragment) {
        dialogFragment.show(requireFragmentManager(), EVENTS_KEY);
    }

    @Override
    public void updateEventsAdapter(List<Event> events) {
        if (isAdded()) {
            mEventsAdapter.updateList(events);
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
