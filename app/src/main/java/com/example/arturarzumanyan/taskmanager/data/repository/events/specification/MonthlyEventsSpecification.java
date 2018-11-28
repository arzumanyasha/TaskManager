package com.example.arturarzumanyan.taskmanager.data.repository.events.specification;

import com.example.arturarzumanyan.taskmanager.data.db.contract.EventsContract;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;

import java.util.Date;

public class MonthlyEventsSpecification implements Specification {
    @Override
    public String getSqlQuery() {
        return "SELECT * FROM " + EventsContract.EventsTable.TABLE_NAME +
                " WHERE " + EventsContract.EventsTable.COLUMN_START_TIME + " > '" + getStartDate() +
                "' AND " + EventsContract.EventsTable.COLUMN_START_TIME + " < '" + getEndDate() + "'";
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
        Date date = DateUtils.getLastDateOfMonth();
        return DateUtils.formatEventTime(date);
    }
}
