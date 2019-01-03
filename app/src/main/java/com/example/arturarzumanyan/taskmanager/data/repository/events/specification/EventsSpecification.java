package com.example.arturarzumanyan.taskmanager.data.repository.events.specification;

public interface EventsSpecification {
    String getSqlQuery();
    int getCountOfDays();
    String getStartDate();
    String getEndDate();
}
