package com.example.arturarzumanyan.taskmanager.data.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.arturarzumanyan.taskmanager.domain.Event;

import java.util.List;

@Dao
public interface EventDao {
    @Query("SELECT * FROM event_table WHERE id = :id")
    Event getEventById(int id);

    @Query("SELECT * FROM event_table WHERE startTime > :startTime AND startTime < :endTime")
    List<Event> getEvents(String startTime, String endTime);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Event event);

    @Update
    void update(Event event);

    @Delete
    void delete(Event event);
}
