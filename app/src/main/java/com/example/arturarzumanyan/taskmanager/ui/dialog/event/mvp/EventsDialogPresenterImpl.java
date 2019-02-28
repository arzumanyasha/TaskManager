package com.example.arturarzumanyan.taskmanager.ui.dialog.event.mvp;

import android.os.Bundle;

import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsRepository;
import com.example.arturarzumanyan.taskmanager.domain.Event;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.PATCH;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.POST;
import static com.example.arturarzumanyan.taskmanager.ui.activity.intention.IntentionActivity.EVENTS_KEY;

public class EventsDialogPresenterImpl implements EventsDialogContract.EventsDialogPresenter {
    private EventsDialogContract.EventsDialogView mEventsDialogView;
    private EventsRepository mEventsRepository;

    public EventsDialogPresenterImpl(EventsDialogContract.EventsDialogView mEventsDialogView) {
        this.mEventsDialogView = mEventsDialogView;
        mEventsRepository = new EventsRepository();
    }

    @Override
    public void processOkButtonClick(Bundle bundle, String name, String description, int colorNumber,
                                     Date startDate, Date endDate, int isNotify) {
        if (endDate != null && endDate.after(startDate) && !name.isEmpty()) {
            if (bundle != null) {
                Event event = bundle.getParcelable(EVENTS_KEY);
                if (event != null) {
                    event = createEventObject(event.getId(), name, description, colorNumber, startDate, endDate, isNotify);
                }
                updateEvent(event);
            } else {
                Event event = createEventObject(UUID.randomUUID().toString(), name, description,
                        colorNumber, startDate, endDate, isNotify);
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
                                    Date startDate, Date endDate, int isNotify) {
        return new Event(id, name, description, colorNumber, startDate, endDate, isNotify);
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
