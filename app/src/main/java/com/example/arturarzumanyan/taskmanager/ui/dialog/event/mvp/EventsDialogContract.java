package com.example.arturarzumanyan.taskmanager.ui.dialog.event.mvp;

import android.content.Context;
import android.os.Bundle;

import com.example.arturarzumanyan.taskmanager.domain.Event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventsDialogContract {
    public interface EventsDialogPresenter {
        void setDefaultCurrentColor(Context context);

        void setCurrentColor(int colorId);

        void processColorPicker();

        void processOkButtonClick(Bundle bundle, String name, String description,/* int colorNumber,*/
                                  String eventDate, Date startTime, Date endTime, int isNotify);

        void processReceivedBundle(Bundle bundle);
    }

    public interface EventsDialogView {
        void setEventInfoViews(Event event);

        void setColorFilter(int colorId);

        void showColorPicker(ArrayList<String> colors);

        void onEventsReady(List<Event> events);

        void onWrongDataSetInViews();

        void onFail(String message);
    }
}
