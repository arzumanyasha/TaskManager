package com.example.arturarzumanyan.taskmanager.data.repository.events.specification;

import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;

import java.util.Date;

public class MonthlyEventsSpecification implements EventsSpecification {
    @Override
    public String getStartDate() {
        Date date = DateUtils.getFirstDateOfMonth();
        return DateUtils.formatEventTime(date);
    }

    @Override
    public String getEndDate() {
        Date date = DateUtils.getLastDateOfMonth();
        return DateUtils.formatEventTime(date);
    }
}
