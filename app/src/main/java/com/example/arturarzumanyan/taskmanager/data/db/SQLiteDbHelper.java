package com.example.arturarzumanyan.taskmanager.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.arturarzumanyan.taskmanager.data.db.contract.EventsContract;
import com.example.arturarzumanyan.taskmanager.data.db.contract.EventsContract.EventsTable;
import com.example.arturarzumanyan.taskmanager.data.db.contract.TasksContract.TaskListTable;
import com.example.arturarzumanyan.taskmanager.data.db.contract.TasksContract.TasksTable;

public class SQLiteDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Tasks.db";
    private static final int DATABASE_VERSION = 2;

    public SQLiteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
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
        recreateDatabase(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        recreateDatabase(db);
    }

    private void recreateDatabase(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + EventsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TasksTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TaskListTable.TABLE_NAME);
        onCreate(db);
    }
}
