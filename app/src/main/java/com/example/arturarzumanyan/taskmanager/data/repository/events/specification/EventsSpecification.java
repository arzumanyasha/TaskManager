package com.example.arturarzumanyan.taskmanager.data.repository.events.specification;

public interface EventsSpecification {
    String getSqlQuery();
    String getStartDate();
    String getEndDate();
}
