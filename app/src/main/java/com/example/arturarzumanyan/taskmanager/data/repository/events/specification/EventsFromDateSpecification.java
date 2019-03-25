package com.example.arturarzumanyan.taskmanager.data.repository.events.specification;

import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;

import java.util.Date;

public class EventsFromDateSpecification implements EventsSpecification {
    private String mDate;

    public void setDate(String date) {
        if (DateUtils.isMatchesEventFormat(date)) {
            date = DateUtils.trimEventDate(date);
        }
        this.mDate = date;
    }

    @Override
    public String getStartDate() {
        Date date = DateUtils.getEventDate(mDate, new Date(0));
        return DateUtils.formatEventTime(date);
    }

    @Override
    public String getEndDate() {
        Date time = new Date();
        time.setHours(23);
        time.setMinutes(59);
        time.setSeconds(59);
        Date eventsEndTime = DateUtils.getEventDate(mDate, time);
        return DateUtils.formatEventTime(eventsEndTime);
    }
}
