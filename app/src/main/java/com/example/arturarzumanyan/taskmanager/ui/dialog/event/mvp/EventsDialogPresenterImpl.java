package com.example.arturarzumanyan.taskmanager.ui.dialog.event.mvp;

import android.content.Context;
import android.os.Bundle;
import android.util.SparseIntArray;

import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsRepository;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;
import com.example.arturarzumanyan.taskmanager.ui.util.ColorPalette;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.PATCH;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.POST;
import static com.example.arturarzumanyan.taskmanager.ui.activity.intention.IntentionActivity.EVENTS_KEY;

public class EventsDialogPresenterImpl implements EventsDialogContract.EventsDialogPresenter {
    private static final int DEFAULT_COLOR = 9;
    private SparseIntArray mColorMap;
    private int mCurrentColor;
    private EventsDialogContract.EventsDialogView mEventsDialogView;
    private EventsRepository mEventsRepository;
    private Date mStartTime;
    private Date mEndTime;

    public EventsDialogPresenterImpl(EventsDialogContract.EventsDialogView mEventsDialogView) {
        this.mEventsDialogView = mEventsDialogView;
        mEventsRepository = new EventsRepository();
    }

    @Override
    public void setDefaultCurrentColor(Context context) {
        ColorPalette colorPalette = new ColorPalette(context);
        mColorMap = colorPalette.getColorPalette();

        mCurrentColor = mColorMap.get(DEFAULT_COLOR);
    }

    @Override
    public void setDefaultTimeValues() {
        int mHour = DateUtils.getHour();
        int mMinute = DateUtils.getMinute();

        int mDay = DateUtils.getDay();
        int mMonth = DateUtils.getMonth();
        int mYear = DateUtils.getYear();

        mStartTime = new Date(0, 0, 0, mHour, mMinute);
        mEndTime = new Date(0, 0, 0, mHour + 1, mMinute);

        mEventsDialogView.setDefaultTimeViews(mYear, mMonth, mDay, mHour, mMinute);
        mEventsDialogView.setTimeAndDatePickers(mYear, mMonth, mDay, mHour, mMinute);
    }

    @Override
    public void setEventStartTime(int hour, int minute) {
        mStartTime = new Date(0, 0, 0, hour, minute);
        mEventsDialogView.setStartTimeView(hour, minute);
    }

    @Override
    public void setEventEndTime(int hour, int minute) {
        mEndTime = new Date(0, 0, 0, hour, minute);
        mEventsDialogView.setEndTimeView(hour, minute);
    }

    @Override
    public void setEventStartTime(String date) {
        mStartTime = DateUtils.getTimeWithoutA(DateUtils.formatTimeWithoutA(date));
    }

    @Override
    public void setEventEndTime(String date) {
        mEndTime = DateUtils.getTimeWithoutA(DateUtils.formatTimeWithoutA(date));
    }

    @Override
    public void setEventDate(int year, int month, int day) {
        mEventsDialogView.setEventDateView(year, month, day);
    }

    @Override
    public void processOkButtonClick(Bundle bundle, String name, String description,
                                     String eventDate, int isNotify) {
        Date startDate = DateUtils.getEventDate(DateUtils.formatReversedYearMonthDayDate(eventDate), mStartTime);
        Date endDate = DateUtils.getEventDate(DateUtils.formatReversedYearMonthDayDate(eventDate), mEndTime);
        int colorNumber = mColorMap.keyAt(mColorMap.indexOfValue(mCurrentColor));
        if (endDate != null && endDate.after(startDate) && !name.isEmpty()) {
            if (bundle != null) {
                Event event = bundle.getParcelable(EVENTS_KEY);
                if (event != null) {
                    event = createEventObject(event.getEventId(), name, description, colorNumber,
                            DateUtils.formatTaskDate(startDate), DateUtils.formatTaskDate(endDate), isNotify);
                }
                updateEvent(event);
            } else {
                Event event = createEventObject(UUID.randomUUID().toString(), name, description, colorNumber,
                        DateUtils.formatTaskDate(startDate), DateUtils.formatTaskDate(endDate), isNotify);
                addEvent(event);
            }
        } else {
            mEventsDialogView.onWrongDataSetInViews();
        }
    }

    private void addEvent(Event event) {
        mEventsRepository.addOrUpdateEvent(event, POST,
                new EventsRepository.OnEventsLoadedListener() {
                    @Override
                    public void onSuccess(List<Event> eventsList) {
                        mEventsDialogView.onEventsReady(eventsList);
                    }

                    @Override
                    public void onFail(String message) {
                        mEventsDialogView.onFail(message);
                    }

                    @Override
                    public void onPermissionDenied() {
                        /** To-do: add realization with start signInActivity*/
                    }
                });

    }

    private void updateEvent(Event event) {
        mEventsRepository.addOrUpdateEvent(event, PATCH, new EventsRepository.OnEventsLoadedListener() {
            @Override
            public void onSuccess(List<Event> eventsList) {
                mEventsDialogView.onEventsReady(eventsList);
            }

            @Override
            public void onFail(String message) {
                mEventsDialogView.onFail(message);
            }

            @Override
            public void onPermissionDenied() {
                /** To-do: add realization with start signInActivity*/
            }
        });
    }

    private Event createEventObject(String id, String name, String description, int colorNumber,
                                    String startDate, String endDate, int isNotify) {
        return new Event(id, name, description, colorNumber, startDate, endDate, isNotify);
    }

    @Override
    public void setCurrentColor(int colorId) {
        mCurrentColor = mColorMap.get(colorId);
        mEventsDialogView.setColorFilter(mColorMap.get(colorId));
    }

    @Override
    public void processColorPicker() {
        ArrayList<String> colors = new ArrayList<>();

        for (int i = 0; i < mColorMap.size(); i++) {
            colors.add("#" + Integer.toHexString(mColorMap.valueAt(i)));
        }
        mEventsDialogView.showColorPicker(colors);
    }

    @Override
    public void processReceivedBundle(Bundle bundle) {
        if (bundle != null) {
            Event event = bundle.getParcelable(EVENTS_KEY);
            if (event != null) {
                mEventsDialogView.setEventInfoViews(event);
            }
        }
    }
}
