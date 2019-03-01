package com.example.arturarzumanyan.taskmanager.ui.adapter.event;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.ui.fragment.event.daily.mvp.contract.DailyEventsContract;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsViewHolder> {
    private final DailyEventsContract.DailyEventsPresenter mEventsListPresenter;

    public EventsAdapter(DailyEventsContract.DailyEventsPresenter eventsListPresenter) {
        this.mEventsListPresenter = eventsListPresenter;
    }

    @NonNull
    @Override
    public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventsViewHolder(view, mEventsListPresenter);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsViewHolder holder, int position) {
        mEventsListPresenter.onBindEventsRowViewAtPosition(position, holder);
    }

    @Override
    public int getItemCount() {
        return mEventsListPresenter.getEventsRowsCount();
    }

    public void updateList(List<Event> updatedList) {
        mEventsListPresenter.updateEventsList(updatedList);
        notifyDataSetChanged();
    }

    public void updateListAfterDeleting(int position){
        notifyItemRemoved(position);
    }

    public void unsubscribe() {
        mEventsListPresenter.unsubscribe();
    }
}