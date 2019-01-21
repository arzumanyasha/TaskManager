package com.example.arturarzumanyan.taskmanager.data.repository.events.specification;

import com.example.arturarzumanyan.taskmanager.data.db.contract.EventsContract;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;

import java.util.Date;

public class WeeklyEventsSpecification implements EventsSpecification {
    @Override
    public String getSqlQuery() {
        return "SELECT * FROM " + EventsContract.EventsTable.TABLE_NAME +
                " WHERE " + EventsContract.EventsTable.COLUMN_START_TIME + " > '" + getStartDate() +
                "' AND " + EventsContract.EventsTable.COLUMN_START_TIME + " < '" + getEndDate() + "'";
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
        int date = DateUtils.getEventWeek(DateUtils.getCurrentTime()) - 1;
        Date sunday = DateUtils.getSundayDate(date - 1);
        Date sundayDate = DateUtils.getEventDate(DateUtils.formatEventTime(sunday), new Date(0));
        return DateUtils.formatEventTime(sundayDate);
    }
}
