package com.example.arturarzumanyan.taskmanager.ui.adapter.event.mvp;

import android.content.Context;
import android.util.SparseIntArray;

import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;
import com.example.arturarzumanyan.taskmanager.ui.util.ColorPalette;

import java.util.List;

public class EventsListPresenter {
    private List<Event> mEventsList;
    private SparseIntArray mColorPaletteArray;
    private OnItemClickListener mListener;

    public EventsListPresenter(List<Event> eventsList, Context context, OnItemClickListener onItemClickListener) {
        ColorPalette mColorPalette = new ColorPalette(context);
        this.mEventsList = eventsList;
        this.mColorPaletteArray = mColorPalette.getColorPalette();
        this.mListener = onItemClickListener;
    }

    public void onBindEventsRowViewAtPosition(int position, EventRowView rowView) {
        Event event = mEventsList.get(position);
        rowView.setItemViewClickListener(position);
        rowView.setName(event.getName());
        rowView.setDescription(event.getDescription().replaceAll("[\n]", ""));
        rowView.setEventColor(mColorPaletteArray.get(event.getColorId()));
        rowView.setTime(DateUtils.formatTime(event.getStartTime()) + " - " + DateUtils.formatTime(event.getEndTime()));
        rowView.setDelete(position);
    }

    public void updateEventsList(List<Event> updatedList) {
        mEventsList = updatedList;
    }

    public void processItemClick(int position) {
        Event event = mEventsList.get(position);
        if (mListener != null) {
            mListener.onItemClick(event);
        }
    }

    public void processItemDelete(int position) {
        Event event = mEventsList.get(position);
        mEventsList.remove(event);
        if (mListener != null) {
            mListener.onItemDelete(event);
        }
    }

    public int getEventsRowsCount() {
        return mEventsList.size();
    }

    public void unsubscribe() {
        mListener = null;
    }

    public interface OnItemClickListener {
        void onItemDelete(Event event);

        void onItemClick(Event event);
    }
}
