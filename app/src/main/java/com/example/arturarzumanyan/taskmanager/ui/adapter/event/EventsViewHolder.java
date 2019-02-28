package com.example.arturarzumanyan.taskmanager.ui.adapter.event;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.ui.adapter.event.mvp.EventRowView;
import com.example.arturarzumanyan.taskmanager.ui.fragment.event.daily.mvp.contract.DailyEventsContract;

public class EventsViewHolder extends RecyclerView.ViewHolder implements EventRowView {
    private DailyEventsContract.DailyEventsPresenter eventsListPresenter;
    private ConstraintLayout constraintLayout;
    private TextView eventName;
    private TextView eventDescription;
    private TextView eventTime;
    private ImageView eventDelete;

    EventsViewHolder(View view, DailyEventsContract.DailyEventsPresenter eventsListPresenter) {
        super(view);
        this.eventsListPresenter = eventsListPresenter;
        constraintLayout = view.findViewById(R.id.constraint_layout_events_holder);
        eventName = view.findViewById(R.id.text_event_name);
        eventDescription = view.findViewById(R.id.text_event_description);
        eventDelete = view.findViewById(R.id.imageViewDeleteEvent);
        eventTime = view.findViewById(R.id.text_event_time);
    }

    @Override
    public void setItemViewClickListener(int position) {
        this.itemView.setTag(position);
        this.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerItemClick(v);
            }
        });
    }

    private void triggerItemClick(View v) {
        int position = (int) v.getTag();
        if (position != RecyclerView.NO_POSITION) {
            eventsListPresenter.processItemClick(position);
        }
    }

    @Override
    public void setName(String name) {
        this.eventName.setText(name);
    }

    @Override
    public void setDescription(String description) {
        this.eventDescription.setText(description);
    }

    @Override
    public void setTime(String time) {
        this.eventTime.setText(time);
    }

    @Override
    public void setEventColor(int colorId) {
        this.constraintLayout.setBackgroundColor(colorId);
    }

    @Override
    public void setDelete(int position) {
        this.eventDelete.setTag(position);
        this.eventDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerDeleteItem(v);
            }
        });
    }

    private void triggerDeleteItem(View v) {
        int position = (int) v.getTag();
        if (position != RecyclerView.NO_POSITION) {
            eventsListPresenter.processItemDelete(position);
        }
    }
}
