package com.example.arturarzumanyan.taskmanager.data.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.arturarzumanyan.taskmanager.domain.TaskList;

import java.util.List;

@Dao
public interface TaskListDao {
    @Query("SELECT * FROM taskList_table WHERE id = :id")
    TaskList getTaskListById(int id);

    @Query("SELECT * FROM taskList_table")
    List<TaskList> getTaskLists();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TaskList TaskList);

    @Update
    void update(TaskList TaskList);

    @Delete
    void delete(TaskList TaskList);
}
