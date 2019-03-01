package com.example.arturarzumanyan.taskmanager.ui.fragment.event.daily.mvp.contract;

import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.ui.adapter.event.mvp.EventRowView;

import java.util.List;

public class DailyEventsContract {
    public interface DailyEventsPresenter {
        void attachView(DailyEventsView dailyEventsView);

        void processDailyEvents();

        void processRetainedState();

        void onBindEventsRowViewAtPosition(int position, EventRowView rowView);

        void updateEventsList(List<Event> updatedList);

        void processItemClick(int position);

        void processItemDelete(int position);

        int getEventsRowsCount();

        void unsubscribe();
    }

    public interface DailyEventsView {
        void setEventsAdapter(List<Event> events);

        void showEventUpdatingDialog(Event event);

        void updateEventsAdapter();

        void updateEventsAdapterAfterDelete(int position);

        void setProgressBarInvisible();

        void setNoEventsTextViewVisible();

        void setNoEventsTextViewInvisible();

        void onFail(String message);
    }
}
