package com.example.arturarzumanyan.taskmanager.data.repository.events.specification;

public class AllEventsSpecification implements Specification {
    @Override
    public String getSqlQuery() {
        return null;
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
