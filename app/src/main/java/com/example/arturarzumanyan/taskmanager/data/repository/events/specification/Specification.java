package com.example.arturarzumanyan.taskmanager.data.repository.events.specification;

public interface Specification {
    String getSqlQuery();
    int getCountOfDays();
    String getStartDate();
    String getEndDate();
}
