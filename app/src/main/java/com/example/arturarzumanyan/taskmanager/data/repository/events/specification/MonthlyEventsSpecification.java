package com.example.arturarzumanyan.taskmanager.data.repository.events.specification;

import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;

import java.util.Date;

public class MonthlyEventsSpecification implements Specification {
    @Override
    public String getSqlQuery() {
        return null;
    }

    @Override
    public int getCountOfDays() {
        return DateUtils.getDaysInCurrentMonth();
    }

    @Override
    public String getStartDate() {
        Date date = DateUtils.getFirstDateOfMonth();
        return DateUtils.formatEventTime(date);
    }

    @Override
    public String getEndDate() {
        String date = DateUtils.getCurrentTime();
        Date endDate = DateUtils.getEventDateFromString(date);
        return DateUtils.formatEventTime(endDate);
    }
}
