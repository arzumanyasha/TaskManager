package com.example.arturarzumanyan.taskmanager.data.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.arturarzumanyan.taskmanager.domain.Task;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM task_table WHERE id = :id")
    Task getTaskById(int id);

    @Query("SELECT * FROM task_table WHERE listId = :listId")
    List<Task> getTasksFromList(int listId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Task Task);

    @Update
    void update(Task Task);

    @Delete
    void delete(Task Task);
}
