package com.example.arturarzumanyan.taskmanager.ui.dialog.event.mvp;

import android.os.Bundle;

import com.example.arturarzumanyan.taskmanager.domain.Event;

import java.util.Date;
import java.util.List;

public class EventsDialogContract {
    public interface EventsDialogPresenter {
        void processOkButtonClick(Bundle bundle, String name, String description, int colorNumber,
                                  Date startDate, Date endDate, int isNotify);

        void processReceivedBundle(Bundle bundle);
    }

    public interface EventsDialogView {
        void setEventInfoViews(Event event);

        void onEventsReady(List<Event> events);

        void onWrongDataSetInViews();

        void onFail(String message);
    }
}
