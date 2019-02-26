package com.example.arturarzumanyan.taskmanager.ui.fragment.daily.mvp.contract;

import android.support.v4.app.DialogFragment;

import com.example.arturarzumanyan.taskmanager.domain.Event;

import java.util.List;

public class DailyEventsContract {
    public interface DailyEventsPresenter {
        void attachView(DailyEventsView dailyEventsView);

        void processDailyEvents();

        void deleteEvent(Event event);

        void processEventDialog(Event event);

        void processRetainedState();

        void processUpdatedEvents(List<Event> events);

        void unsubscribe();
    }

    public interface DailyEventsView {
        void setEventsAdapter(List<Event> events);

        void showDialog(DialogFragment dialogFragment);

        void updateEventsAdapter(List<Event> events);

        void setProgressBarInvisible();

        void setNoEventsTextViewVisible();

        void setNoEventsTextViewInvisible();

        void onFail(String message);
    }
}
