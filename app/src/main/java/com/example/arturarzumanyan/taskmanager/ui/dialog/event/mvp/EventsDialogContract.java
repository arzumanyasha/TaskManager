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

        void setDefaultTimeValues();

        void setEventStartTime(int hour, int minute);

        void setEventStartTime(String date);

        void setEventEndTime(int hour, int minute);

        void setEventEndTime(String date);

        void setEventDate(int year, int month, int day);

        void processColorPicker();

        void processOkButtonClick(Bundle bundle, String name, String description,
                                  String eventDate, int isNotify);

        void processReceivedBundle(Bundle bundle);
    }

    public interface EventsDialogView {
        void setEventInfoViews(Event event);

        void setColorFilter(int colorId);

        void setDefaultTimeViews(int year, int month, int day, int hour, int minute);

        void setTimeAndDatePickers(int year, int month, int day, int hour, int minute);

        void setStartTimeView(int hour, int minute);

        void setEndTimeView(int hour, int minute);

        void setEventDateView(int year, int month, int day);

        void showColorPicker(ArrayList<String> colors);

        void onEventsReady(List<Event> events);

        void onWrongDataSetInViews();

        void onFail(String message);
    }
}
