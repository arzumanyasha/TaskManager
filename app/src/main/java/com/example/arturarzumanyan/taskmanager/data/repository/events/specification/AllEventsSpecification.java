package com.example.arturarzumanyan.taskmanager.data.repository.events.specification;

import com.example.arturarzumanyan.taskmanager.data.db.contract.EventsContract;

public class AllEventsSpecification implements Specification {
    @Override
    public String getSqlQuery() {
        return "SELECT * FROM " + EventsContract.EventsTable.TABLE_NAME;
    }

    @Override
    public int getCountOfDays() {
        return 0;
    }

    @Override
    public String getStartDate() {
        return null;
    }

    @Override
    public String getEndDate() {
        return null;
    }
}
