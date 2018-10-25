package com.example.arturarzumanyan.taskmanager.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.arturarzumanyan.taskmanager.data.db.contract.EventsContract;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.data.db.contract.TasksContract.*;
import com.example.arturarzumanyan.taskmanager.data.db.contract.EventsContract.*;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SQLiteDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Tasks.db";
    private static final int DATABASE_VERSION = 27;

    private SQLiteDatabase mDb;

    public SQLiteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.mDb = db;

        final String SQL_CREATE_EVENTS_TABLE = "CREATE TABLE " +
                EventsContract.EventsTable.TABLE_NAME + " ( " +
                EventsContract.EventsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EventsContract.EventsTable.COLUMN_EVENT_ID + " TEXT, " +
                EventsContract.EventsTable.COLUMN_NAME + " TEXT, " +
                EventsContract.EventsTable.COLUMN_DESCRIPTION + " TEXT, " +
                EventsContract.EventsTable.COLUMN_COLOR_ID + " INTEGER, " +
                EventsContract.EventsTable.COLUMN_START_TIME + " TEXT, " +
                EventsContract.EventsTable.COLUMN_END_TIME + " TEXT, " +
                EventsContract.EventsTable.COLUMN_REMINDER + " INTEGER" +
                ")";

        final String SQL_CREATE_TASK_LISTS_TABLE = "CREATE TABLE " +
                TaskListTable.TABLE_NAME + " ( " +
                TaskListTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TaskListTable.COLUMN_LIST_ID + " TEXT, " +
                TaskListTable.COLUMN_TITLE + " TEXT" +
                ")";

        final String SQL_CREATE_TASKS_TABLE = "CREATE TABLE " +
                TasksTable.TABLE_NAME + " ( " +
                TasksTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TasksTable.COLUMN_TASK_ID + " TEXT, " +
                TasksTable.COLUMN_TITLE + " TEXT, " +
                TasksTable.COLUMN_NOTES + " TEXT, " +
                TasksTable.COLUMN_STATUS + " INTEGER, " +
                TasksTable.COLUMN_DUE + " TEXT, " +
                TasksTable.COLUMN_LIST_ID + " INTEGER" +
                ")";

        db.execSQL(SQL_CREATE_EVENTS_TABLE);
        db.execSQL(SQL_CREATE_TASK_LISTS_TABLE);
        db.execSQL(SQL_CREATE_TASKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + EventsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TasksTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TaskListTable.TABLE_NAME);
        onCreate(db);
    }

    public void insertEvents(ArrayList<Event> eventsList) {
        mDb = getWritableDatabase();
        for (int i = 0; i < eventsList.size(); i++) {
            insertEvent(eventsList.get(i));
        }
    }

    public void insertEvent(Event event){
        mDb = getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(EventsTable.COLUMN_EVENT_ID, event.getId());
        cv.put(EventsTable.COLUMN_NAME, event.getName());
        cv.put(EventsTable.COLUMN_DESCRIPTION, event.getDescription());
        cv.put(EventsTable.COLUMN_COLOR_ID, event.getColorId());

        cv.put(EventsTable.COLUMN_START_TIME, DateUtils.formatEventTime(event.getStartTime()));

        cv.put(EventsTable.COLUMN_END_TIME, DateUtils.formatEventTime(event.getEndTime()));
        cv.put(EventsTable.COLUMN_REMINDER, event.getIsNotify());
        /*
        if (event.isNotify()) {
            cv.put(EventsTable.COLUMN_REMINDER, 1);
        } else {
            cv.put(EventsTable.COLUMN_REMINDER, 0);
        }*/

        mDb.insert(EventsTable.TABLE_NAME, null, cv);
    }

    public void updateEvent(Event event){
        mDb = getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(EventsTable.COLUMN_NAME, event.getName());
        cv.put(EventsTable.COLUMN_DESCRIPTION, event.getDescription());
        cv.put(EventsTable.COLUMN_COLOR_ID, event.getColorId());

        cv.put(EventsTable.COLUMN_START_TIME, DateUtils.formatEventTime(event.getStartTime()));

        cv.put(EventsTable.COLUMN_END_TIME, DateUtils.formatEventTime(event.getEndTime()));

        cv.put(EventsTable.COLUMN_REMINDER, event.getIsNotify());
/*
        if (event.isNotify()) {
            cv.put(EventsTable.COLUMN_REMINDER, 1);
        } else {
            cv.put(EventsTable.COLUMN_REMINDER, 0);
        }
*/
        mDb.update(EventsTable.TABLE_NAME, cv, "id = ?", new String[]{event.getId()});
    }

    public void deleteEvent(Event event){
        mDb = getWritableDatabase();
        mDb.delete(EventsTable.TABLE_NAME, "id = ?", new String[]{event.getId()});
    }

    public void insertTaskLists(ArrayList<TaskList> taskListArrayList) {
        mDb = getWritableDatabase();
        for (int i = 0; i < taskListArrayList.size(); i++) {
            insertTaskList(taskListArrayList.get(i));
        }
    }

    public void insertTaskList(TaskList taskList) {
        mDb = getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(TaskListTable.COLUMN_LIST_ID, taskList.getTaskListId());
        cv.put(TaskListTable.COLUMN_TITLE, taskList.getTitle());

        mDb.insert(TaskListTable.TABLE_NAME, null, cv);
    }


    public void updateTaskList(TaskList taskList) {
        mDb = getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(TaskListTable.COLUMN_TITLE, taskList.getTitle());

        mDb.update(TaskListTable.TABLE_NAME, cv, "id = ?",
                new String[]{taskList.getTaskListId()});
    }

    public void deleteTaskList(TaskList taskList) {
        mDb = getWritableDatabase();
        mDb.delete(TaskListTable.TABLE_NAME, "_id = ?",
                new String[]{Integer.toString(taskList.getId())});
    }

    public void insertTasks(ArrayList<Task> tasksArrayList) {
        mDb = getWritableDatabase();
        for (int i = 0; i < tasksArrayList.size(); i++) {
            insertTask(tasksArrayList.get(i));
        }
    }

    public void insertTask(Task task) {
        mDb = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TasksTable.COLUMN_TASK_ID, task.getId());
        cv.put(TasksTable.COLUMN_TITLE, task.getName());
        cv.put(TasksTable.COLUMN_NOTES, task.getDescription());

        cv.put(TasksTable.COLUMN_STATUS, task.getIsExecuted());
        if (task.getDate() != null) {
            cv.put(TasksTable.COLUMN_DUE, DateUtils.formatTaskDate(task.getDate()));
        }
        cv.put(TasksTable.COLUMN_LIST_ID, task.getListId());

        mDb.insert(TasksTable.TABLE_NAME, null, cv);
    }

    public void updateTask(Task task) {
        mDb = getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(TasksTable.COLUMN_TITLE, task.getName());
        cv.put(TasksTable.COLUMN_NOTES, task.getDescription());

        cv.put(TasksTable.COLUMN_STATUS, task.getIsExecuted());

        if (task.getDate() != null) {
            cv.put(TasksTable.COLUMN_DUE, DateUtils.formatTaskDate(task.getDate()));
        }
        cv.put(TasksTable.COLUMN_LIST_ID, task.getListId());

        mDb.update(TasksTable.TABLE_NAME, cv, "id = ?", new String[]{task.getId()});
    }

    public void deleteTask(Task task) {
        mDb = getWritableDatabase();
        mDb.delete(TasksTable.TABLE_NAME, "id = ?", new String[]{task.getId()});
    }

    public ArrayList<Event> getEvents() {
        mDb = getReadableDatabase();
        Cursor c = mDb.rawQuery("SELECT * FROM " + EventsTable.TABLE_NAME, null);
        return getEventsFromCursor(c);
    }

    public ArrayList<Event> getDailyEvents(String date) {
        mDb = getReadableDatabase();
        Cursor c = mDb.rawQuery("SELECT * FROM " + EventsTable.TABLE_NAME +
                " WHERE " + EventsTable.COLUMN_START_TIME + " LIKE '" + date + "%'", null);
        return getEventsFromCursor(c);
    }

    private ArrayList<Event> getEventsFromCursor(Cursor c) {
        ArrayList<Event> eventsList = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                Date startDate = DateUtils.getEventDateFromString(c.getString(c.getColumnIndex(EventsTable.COLUMN_START_TIME)));
                Date endDate = DateUtils.getEventDateFromString(c.getString(c.getColumnIndex(EventsTable.COLUMN_END_TIME)));

                Event event = new Event(c.getString(c.getColumnIndex(EventsTable.COLUMN_EVENT_ID)),
                        c.getString(c.getColumnIndex(EventsTable.COLUMN_NAME)),
                        c.getString(c.getColumnIndex(EventsTable.COLUMN_DESCRIPTION)),
                        c.getInt(c.getColumnIndex(EventsTable.COLUMN_COLOR_ID)),
                        startDate,
                        endDate,
                        c.getInt(c.getColumnIndex(EventsTable.COLUMN_REMINDER))
                );

                eventsList.add(event);
            } while (c.moveToNext());
        }

        c.close();
        return eventsList;

    }

    public ArrayList<Task> getTasksFromList(int tasksListId) {
        ArrayList<Task> tasksList = new ArrayList<>();
        mDb = getReadableDatabase();

        String[] selectionArgs = new String[]{Integer.toString(tasksListId)};
        Cursor c = mDb.rawQuery("SELECT * FROM " + TasksTable.TABLE_NAME +
                " WHERE " + TasksTable.COLUMN_LIST_ID + " = ?", selectionArgs);

        if (c.moveToFirst()) {
            do {
                Task task;

                if (c.getString(c.getColumnIndex(TasksTable.COLUMN_DUE)) != null) {
                    Date date = DateUtils.getTaskDateFromString(c.getString(c.getColumnIndex(TasksTable.COLUMN_DUE)));

                    task = new Task(c.getString(c.getColumnIndex(TasksTable.COLUMN_TASK_ID)),
                            c.getString(c.getColumnIndex(TasksTable.COLUMN_TITLE)),
                            c.getString(c.getColumnIndex(TasksTable.COLUMN_NOTES)),
                            c.getInt(c.getColumnIndex(TasksTable.COLUMN_STATUS)),
                            tasksListId,
                            date
                    );
                } else task = new Task(c.getString(c.getColumnIndex(TasksTable.COLUMN_TASK_ID)),
                        c.getString(c.getColumnIndex(TasksTable.COLUMN_TITLE)),
                        c.getString(c.getColumnIndex(TasksTable.COLUMN_NOTES)),
                        c.getInt(c.getColumnIndex(TasksTable.COLUMN_STATUS)),
                        tasksListId
                );

                tasksList.add(task);
            } while (c.moveToNext());
        }

        c.close();
        return tasksList;
    }

    public ArrayList<TaskList> getTaskLists() {
        ArrayList<TaskList> taskListArrayList = new ArrayList<>();
        mDb = getReadableDatabase();

        Cursor c = mDb.rawQuery("SELECT * FROM " + TaskListTable.TABLE_NAME, null);

        if (c.moveToFirst()) {
            do {
                TaskList taskList = new TaskList(c.getInt(c.getColumnIndex(TaskListTable._ID)),
                        c.getString(c.getColumnIndex(TaskListTable.COLUMN_LIST_ID)),
                        c.getString(c.getColumnIndex(TaskListTable.COLUMN_TITLE))
                );

                taskListArrayList.add(taskList);
            } while (c.moveToNext());
        }

        c.close();
        return taskListArrayList;
    }

    public TaskList getTaskList(int id) {
        mDb = getReadableDatabase();

        String[] selectionArgs = new String[]{Integer.toString(id)};
        Cursor c = mDb.rawQuery("SELECT * FROM " + TaskListTable.TABLE_NAME +
                " WHERE " + TaskListTable._ID + " = ?", selectionArgs);

        if (c.moveToFirst()) {
            do {
                TaskList taskList = new TaskList(c.getInt(c.getColumnIndex(TaskListTable._ID)),
                        c.getString(c.getColumnIndex(TaskListTable.COLUMN_LIST_ID)),
                        c.getString(c.getColumnIndex(TaskListTable.COLUMN_TITLE))
                );

                return taskList;

            } while (c.moveToNext());
        }

        c.close();
        return null;
    }

    public TaskList getTaskList(String title) {
        mDb = getReadableDatabase();

        String[] selectionArgs = new String[]{title};
        Cursor c = mDb.rawQuery("SELECT * FROM " + TaskListTable.TABLE_NAME +
                " WHERE " + TaskListTable.COLUMN_TITLE + " = ?", selectionArgs);

        if (c.moveToFirst()) {
            do {
                TaskList taskList = new TaskList(c.getInt(c.getColumnIndex(TaskListTable._ID)),
                        c.getString(c.getColumnIndex(TaskListTable.COLUMN_LIST_ID)),
                        c.getString(c.getColumnIndex(TaskListTable.COLUMN_TITLE))
                );

                return taskList;

            } while (c.moveToNext());
        }

        c.close();
        return null;
    }
}
