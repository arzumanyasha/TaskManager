package com.example.arturarzumanyan.taskmanager.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.arturarzumanyan.taskmanager.data.db.contract.EventsContract;
import com.example.arturarzumanyan.taskmanager.data.db.contract.TasksContract;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.EventsSpecification;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.specification.TaskListsSpecification;
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

    public void addOrUpdateEvents(List<Event> eventsList) {
        mDbSqlite = mSqLiteDbHelper.getWritableDatabase();

        for (Event event : eventsList) {
            insertOrUpdateEvent(event);
        }
    }

    private void insertOrUpdateEvent(Event event) {
        mDbSqlite.beginTransaction();

        try {
            ContentValues cv = createEventContentValues(event);
            if (isEventExistsInDb(event.getId())) {
                mDbSqlite.update(EventsContract.EventsTable.TABLE_NAME, cv,
                        "id = ?", new String[]{event.getId()});
            } else {
                mDbSqlite.insert(EventsContract.EventsTable.TABLE_NAME,
                        null, cv);
            }
            mDbSqlite.setTransactionSuccessful();
        } finally {
            mDbSqlite.endTransaction();
        }
    }

    private boolean isEventExistsInDb(String eventId) {
        String[] selectionArgs = new String[]{eventId};
        Cursor c = mDbSqlite.rawQuery("SELECT * FROM " + EventsContract.EventsTable.TABLE_NAME +
                " WHERE " + EventsContract.EventsTable.COLUMN_EVENT_ID + " = ?", selectionArgs);
        return c.getCount() != 0;
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

        mDbSqlite.beginTransaction();
        try {
            mDbSqlite.delete(EventsContract.EventsTable.TABLE_NAME,
                    "id = ?",
                    new String[]{event.getId()});

            mDbSqlite.setTransactionSuccessful();
        } finally {
            mDbSqlite.endTransaction();
        }
    }

    public List<Event> getEvents(EventsSpecification specification) {
        mDbSqlite = mSqLiteDbHelper.getReadableDatabase();
        Cursor c = mDbSqlite.rawQuery(specification.getSqlQuery(), null);
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

    public void addOrUpdateTaskLists(List<TaskList> taskLists) {
        mDbSqlite = mSqLiteDbHelper.getWritableDatabase();

        for (TaskList taskList : taskLists) {
            insertOrUpdateTaskList(taskList);
        }
    }

    private void insertOrUpdateTaskList(TaskList taskList) {
        mDbSqlite.beginTransaction();

        try {
            ContentValues cv = createTaskListContentValues(taskList);
            if (isTaskListExistsInDb(taskList.getTaskListId())) {
                mDbSqlite.update(TasksContract.TaskListTable.TABLE_NAME, cv,
                        "_id = ?", new String[]{Integer.toString(taskList.getId())});
            } else {
                mDbSqlite.insert(TasksContract.TaskListTable.TABLE_NAME,
                        null, cv);
            }
            mDbSqlite.setTransactionSuccessful();
        } finally {
            mDbSqlite.endTransaction();
        }
    }

    private boolean isTaskListExistsInDb(String taskListId) {
        String[] selectionArgs = new String[]{taskListId};
        Cursor c = mDbSqlite.rawQuery("SELECT * FROM " + TasksContract.TaskListTable.TABLE_NAME +
                " WHERE " + TasksContract.TaskListTable.COLUMN_LIST_ID + " = ?", selectionArgs);
        return c.getCount() != 0;
    }

    private ContentValues createTaskListContentValues(TaskList taskList) {
        ContentValues cv = new ContentValues();

        cv.put(TasksContract.TaskListTable.COLUMN_LIST_ID, taskList.getTaskListId());
        cv.put(TasksContract.TaskListTable.COLUMN_TITLE, taskList.getTitle());

        return cv;
    }

    public List<TaskList> getTaskLists(TaskListsSpecification specification) {
        List<TaskList> taskListArrayList = new ArrayList<>();
        mDbSqlite = mSqLiteDbHelper.getReadableDatabase();

        Cursor c;
        if (specification.getSelectionArgs() == null) {
            c = mDbSqlite.rawQuery(specification.getSqlQuery(), null);
        } else {
            c = mDbSqlite.rawQuery(specification.getSqlQuery(),
                    new String[]{specification.getSelectionArgs()});
        }
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

    public void deleteTaskList(TaskList taskList) {
        mDbSqlite = mSqLiteDbHelper.getWritableDatabase();
        mDbSqlite.delete(TasksContract.TaskListTable.TABLE_NAME, "_id = ?",
                new String[]{Integer.toString(taskList.getId())});
    }

    public void addOrUpdateTasks(List<Task> tasks) {
        mDbSqlite = mSqLiteDbHelper.getWritableDatabase();

        for (Task task : tasks) {
            insertOrUpdateTask(task);
        }
    }

    private void insertOrUpdateTask(Task task) {
        mDbSqlite.beginTransaction();

        try {
            ContentValues cv = createTaskContentValues(task);
            if (isTaskExistsInDb(task.getId())) {
                mDbSqlite.update(TasksContract.TasksTable.TABLE_NAME, cv,
                        "id = ?", new String[]{task.getId()});
            } else {
                mDbSqlite.insert(TasksContract.TasksTable.TABLE_NAME,
                        null, cv);
            }
            mDbSqlite.setTransactionSuccessful();
        } finally {
            mDbSqlite.endTransaction();
        }
    }

    private boolean isTaskExistsInDb(String taskId) {
        String[] selectionArgs = new String[]{taskId};
        Cursor c = mDbSqlite.rawQuery("SELECT * FROM " + TasksContract.TasksTable.TABLE_NAME +
                " WHERE " + TasksContract.TasksTable.COLUMN_TASK_ID + " = ?", selectionArgs);
        return c.getCount() != 0;
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
}
