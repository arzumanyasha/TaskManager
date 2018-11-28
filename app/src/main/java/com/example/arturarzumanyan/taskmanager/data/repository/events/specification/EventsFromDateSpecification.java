package com.example.arturarzumanyan.taskmanager.data.repository.events.specification;

import com.example.arturarzumanyan.taskmanager.data.db.contract.EventsContract;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;

import java.util.Date;

public class EventsFromDateSpecification implements Specification {
    private String mDate;

    @Override
    public String getSqlQuery() {
        return "SELECT * FROM " + EventsContract.EventsTable.TABLE_NAME +
                " WHERE " + EventsContract.EventsTable.COLUMN_START_TIME + " LIKE '" + mDate + "%'";
    }

    @Override
    public int getCountOfDays() {
        return 1;
    }

    public void setDate(String date) {
        if(DateUtils.isMatchesEventFormat(date)){
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
        Date eventsEndTime = DateUtils.getEventDate(mDate, time);
        return DateUtils.formatEventTime(eventsEndTime);
    }


}
