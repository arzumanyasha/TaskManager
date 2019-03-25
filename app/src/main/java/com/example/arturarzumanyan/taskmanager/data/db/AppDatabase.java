package com.example.arturarzumanyan.taskmanager.data.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;

@Database(entities = {Event.class, Task.class, TaskList.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;
    private static final String DATABASE_NAME = "intention_database";
    public abstract EventDao eventDao();
    public abstract TaskListDao taskListDao();
    public abstract TaskDao taskDao();

    public static void initAppDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class,
                    DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
    }

    public static AppDatabase getAppDatabase() {
        synchronized (AppDatabase.class) {
            return instance;
        }
    }

    public static void destroyInstance() {
        instance = null;
    }
}
