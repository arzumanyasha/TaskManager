package com.example.arturarzumanyan.taskmanager.data.repository.events;

import com.example.arturarzumanyan.taskmanager.domain.Event;

import java.util.ArrayList;

public interface EventsRepository {
    ArrayList<Event> getEvents();

    ArrayList<Event> getDailyEvents();

    ArrayList<Event> getWeeklyEvents();

    ArrayList<Event> getMonthlyEvents();

    void addEvent(Event event);

    void updateEvent(Event event);

    void deleteEvent(Event event);
}
