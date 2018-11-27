package com.example.arturarzumanyan.taskmanager.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.arturarzumanyan.taskmanager.data.db.contract.EventsContract;
import com.example.arturarzumanyan.taskmanager.data.db.contract.TasksContract;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DbHelper {
    private static DbHelper mDbInstance;
    private SQLiteDatabase mDbSqlite;
    private static SQLiteDbHelper mSqLiteDbHelper;

    public synchronized static DbHelper getDbHelper(Context context) {
        if (mDbInstance == null) {
            mDbInstance = new DbHelper(context);
        }
        return mDbInstance;
    }

    private DbHelper(Context context) {
        mSqLiteDbHelper = new SQLiteDbHelper(context);
    }

    public void updateEvents(List<Event> eventsList) {
        mDbSqlite = mSqLiteDbHelper.getWritableDatabase();

        List<Event> oldEventsList = getEvents();
        for (int i = 0; i < eventsList.size(); i++) {
            for (int j = 0; j < oldEventsList.size(); j++) {
                if (eventsList.get(i).getId().equals(oldEventsList.get(j).getId())) {
                    updateEvent(eventsList.get(i));
                    break;
                }

                if (j == oldEventsList.size() - 1){
                    insertEvent(eventsList.get(i));
                }
            }
        }
    }

    public void insertEvent(Event event) {
        mDbSqlite = mSqLiteDbHelper.getWritableDatabase();

        mDbSqlite.insert(EventsContract.EventsTable.TABLE_NAME, null,
                createEventContentValues(event));
    }

    public void updateEvent(Event event) {
        mDbSqlite = mSqLiteDbHelper.getWritableDatabase();

        mDbSqlite.update(EventsContract.EventsTable.TABLE_NAME, createEventContentValues(event),
                "id = ?", new String[]{event.getId()});
    }

    private ContentValues createEventContentValues(Event event) {
        ContentValues cv = new ContentValues();

        cv.put(EventsContract.EventsTable.COLUMN_EVENT_ID, event.getId());
        cv.put(EventsContract.EventsTable.COLUMN_NAME, event.getName());
        cv.put(EventsContract.EventsTable.COLUMN_DESCRIPTION, event.getDescription());
        cv.put(EventsContract.EventsTable.COLUMN_COLOR_ID, event.getColorId());

        cv.put(EventsContract.EventsTable.COLUMN_START_TIME,
                DateUtils.formatEventTime(event.getStartTime()));

        cv.put(EventsContract.EventsTable.COLUMN_END_TIME,
                DateUtils.formatEventTime(event.getEndTime()));
        cv.put(EventsContract.EventsTable.COLUMN_REMINDER, event.getIsNotify());

        return cv;
    }

    public void deleteEvent(Event event) {
        mDbSqlite = mSqLiteDbHelper.getWritableDatabase();
        mDbSqlite.delete(EventsContract.EventsTable.TABLE_NAME,
                "id = ?",
                new String[]{event.getId()});
    }

    public void insertTaskLists(List<TaskList> taskListArrayList) {
        mDbSqlite = mSqLiteDbHelper.getWritableDatabase();
        for (int i = 0; i < taskListArrayList.size(); i++) {
            insertTaskList(taskListArrayList.get(i));
        }
    }

    public void insertTaskList(TaskList taskList) {
        mDbSqlite = mSqLiteDbHelper.getWritableDatabase();

        mDbSqlite.insert(TasksContract.TaskListTable.TABLE_NAME,
                null, createTaskListContentValues(taskList));
    }


    public void updateTaskList(TaskList taskList) {
        mDbSqlite = mSqLiteDbHelper.getWritableDatabase();

        mDbSqlite.update(TasksContract.TaskListTable.TABLE_NAME,
                createTaskListContentValues(taskList),
                "id = ?",
                new String[]{taskList.getTaskListId()});
    }

    private ContentValues createTaskListContentValues(TaskList taskList) {
        ContentValues cv = new ContentValues();

        cv.put(TasksContract.TaskListTable.COLUMN_LIST_ID, taskList.getTaskListId());
        cv.put(TasksContract.TaskListTable.COLUMN_TITLE, taskList.getTitle());

        return cv;
    }

    public void deleteTaskList(TaskList taskList) {
        mDbSqlite = mSqLiteDbHelper.getWritableDatabase();
        mDbSqlite.delete(TasksContract.TaskListTable.TABLE_NAME, "_id = ?",
                new String[]{Integer.toString(taskList.getId())});
    }

    public void insertTasks(List<Task> tasksArrayList) {
        mDbSqlite = mSqLiteDbHelper.getWritableDatabase();
        for (int i = 0; i < tasksArrayList.size(); i++) {
            insertTask(tasksArrayList.get(i));
        }
    }

    public void insertTask(Task task) {
        mDbSqlite = mSqLiteDbHelper.getWritableDatabase();

        mDbSqlite.insert(TasksContract.TasksTable.TABLE_NAME, null,
                createTaskContentValues(task));
    }

    public void updateTask(Task task) {
        mDbSqlite = mSqLiteDbHelper.getWritableDatabase();

        mDbSqlite.update(TasksContract.TasksTable.TABLE_NAME, createTaskContentValues(task),
                "id = ?", new String[]{task.getId()});
    }

    private ContentValues createTaskContentValues(Task task) {
        ContentValues cv = new ContentValues();

        cv.put(TasksContract.TasksTable.COLUMN_TASK_ID, task.getId());
        cv.put(TasksContract.TasksTable.COLUMN_TITLE, task.getName());
        cv.put(TasksContract.TasksTable.COLUMN_NOTES, task.getDescription());

        cv.put(TasksContract.TasksTable.COLUMN_STATUS, task.getIsExecuted());
        if (task.getDate() != null) {
            cv.put(TasksContract.TasksTable.COLUMN_DUE,
                    DateUtils.formatTaskDate(task.getDate()));
        }

        cv.put(TasksContract.TasksTable.COLUMN_LIST_ID, task.getListId());

        return cv;
    }

    public void deleteTask(Task task) {
        mDbSqlite = mSqLiteDbHelper.getWritableDatabase();
        mDbSqlite.delete(TasksContract.TasksTable.TABLE_NAME,
                "id = ?", new String[]{task.getId()});
    }

    public List<Event> getEvents() {
        mDbSqlite = mSqLiteDbHelper.getReadableDatabase();
        Cursor c = mDbSqlite.rawQuery("SELECT * FROM " + EventsContract.EventsTable.TABLE_NAME, null);
        return getEventsFromCursor(c);
    }

    public List<Event> getDailyEvents(String date) {
        if(DateUtils.isMatchesEventFormat(date)){
            date = DateUtils.trimEventDate(date);
        }
        mDbSqlite = mSqLiteDbHelper.getReadableDatabase();
        Cursor c = mDbSqlite.rawQuery("SELECT * FROM " + EventsContract.EventsTable.TABLE_NAME +
                " WHERE " + EventsContract.EventsTable.COLUMN_START_TIME + " LIKE '" + date + "%'", null);
        return getEventsFromCursor(c);
    }

    private List<Event> getEventsFromCursor(Cursor c) {
        List<Event> eventsList = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                Date startDate = DateUtils.getEventDateFromString(
                        c.getString(c.getColumnIndex(EventsContract.EventsTable.COLUMN_START_TIME)));
                Date endDate = DateUtils.getEventDateFromString(
                        c.getString(c.getColumnIndex(EventsContract.EventsTable.COLUMN_END_TIME)));

                Event event = new Event(c.getString(c.getColumnIndex(EventsContract.EventsTable.COLUMN_EVENT_ID)),
                        c.getString(c.getColumnIndex(EventsContract.EventsTable.COLUMN_NAME)),
                        c.getString(c.getColumnIndex(EventsContract.EventsTable.COLUMN_DESCRIPTION)),
                        c.getInt(c.getColumnIndex(EventsContract.EventsTable.COLUMN_COLOR_ID)),
                        startDate,
                        endDate,
                        c.getInt(c.getColumnIndex(EventsContract.EventsTable.COLUMN_REMINDER))
                );

                eventsList.add(event);
            } while (c.moveToNext());
        }

        c.close();
        return eventsList;

    }

    public List<Task> getTasksFromList(int tasksListId) {
        List<Task> tasksList = new ArrayList<>();
        mDbSqlite = mSqLiteDbHelper.getReadableDatabase();

        String[] selectionArgs = new String[]{Integer.toString(tasksListId)};
        Cursor c = mDbSqlite.rawQuery("SELECT * FROM " + TasksContract.TasksTable.TABLE_NAME +
                " WHERE " + TasksContract.TasksTable.COLUMN_LIST_ID + " = ?", selectionArgs);

        if (c.moveToFirst()) {
            do {
                Task task;
                Date date = null;

                if (c.getString(c.getColumnIndex(TasksContract.TasksTable.COLUMN_DUE)) != null) {
                    date = DateUtils.getTaskDateFromString(c.getString(c.getColumnIndex(TasksContract.TasksTable.COLUMN_DUE)));
                }

                task = new Task(c.getString(c.getColumnIndex(TasksContract.TasksTable.COLUMN_TASK_ID)),
                        c.getString(c.getColumnIndex(TasksContract.TasksTable.COLUMN_TITLE)),
                        c.getString(c.getColumnIndex(TasksContract.TasksTable.COLUMN_NOTES)),
                        c.getInt(c.getColumnIndex(TasksContract.TasksTable.COLUMN_STATUS)),
                        tasksListId,
                        date
                );

                tasksList.add(task);
            } while (c.moveToNext());
        }

        c.close();
        return tasksList;
    }

    public List<TaskList> getTaskLists() {
        List<TaskList> taskListArrayList = new ArrayList<>();
        mDbSqlite = mSqLiteDbHelper.getReadableDatabase();

        Cursor c = mDbSqlite.rawQuery("SELECT * FROM " + TasksContract.TaskListTable.TABLE_NAME, null);

        if (c.moveToFirst()) {
            do {
                TaskList taskList = new TaskList(c.getInt(c.getColumnIndex(TasksContract.TaskListTable._ID)),
                        c.getString(c.getColumnIndex(TasksContract.TaskListTable.COLUMN_LIST_ID)),
                        c.getString(c.getColumnIndex(TasksContract.TaskListTable.COLUMN_TITLE))
                );

                taskListArrayList.add(taskList);
            } while (c.moveToNext());
        }

        c.close();
        return taskListArrayList;
    }

    public TaskList getTaskList(int id) {
        mDbSqlite = mSqLiteDbHelper.getReadableDatabase();

        return getTaskListFromCursor(Integer.toString(id), TasksContract.TaskListTable._ID);
    }

    public TaskList getTaskList(String title) {
        mDbSqlite = mSqLiteDbHelper.getReadableDatabase();

        return getTaskListFromCursor(title, TasksContract.TaskListTable.COLUMN_TITLE);
    }

    private TaskList getTaskListFromCursor(String selectionArg, String columnName) {
        String[] selectionArgs = new String[]{selectionArg};
        Cursor c = mDbSqlite.rawQuery("SELECT * FROM " + TasksContract.TaskListTable.TABLE_NAME +
                " WHERE " + columnName + " = ?", selectionArgs);

        if (c.moveToFirst()) {
            do {
                TaskList taskList = new TaskList(c.getInt(c.getColumnIndex(TasksContract.TaskListTable._ID)),
                        c.getString(c.getColumnIndex(TasksContract.TaskListTable.COLUMN_LIST_ID)),
                        c.getString(c.getColumnIndex(TasksContract.TaskListTable.COLUMN_TITLE))
                );

                return taskList;

            } while (c.moveToNext());
        }

        c.close();
        return null;
    }
}
