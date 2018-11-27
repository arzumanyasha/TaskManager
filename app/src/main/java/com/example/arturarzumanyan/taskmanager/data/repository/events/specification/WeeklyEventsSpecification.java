package com.example.arturarzumanyan.taskmanager.data.repository.events.specification;

import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;

import java.util.Date;

public class WeeklyEventsSpecification implements Specification {
    @Override
    public String getSqlQuery() {
        return null;
    }

    @Override
    public int getCountOfDays() {
        return 7;
    }

    @Override
    public String getStartDate() {
        int date = DateUtils.getEventWeek(DateUtils.getCurrentTime()) - 1;
        Date monday = DateUtils.getMondayDate(date - 1);
        Date mondayDate = DateUtils.getEventDate(DateUtils.formatEventTime(monday), new Date(0));
        return DateUtils.formatEventTime(mondayDate);
    }

    @Override
    public String getEndDate() {
        String date = DateUtils.getCurrentTime();
        Date endDate = DateUtils.getEventDateFromString(date);
        Date time = new Date();
        time.setHours(23);
        time.setMinutes(59);
        Date eventsEndTime = DateUtils.getEventDate(DateUtils.formatEventTime(endDate), time);
        return DateUtils.formatEventTime(eventsEndTime);
    }
}
