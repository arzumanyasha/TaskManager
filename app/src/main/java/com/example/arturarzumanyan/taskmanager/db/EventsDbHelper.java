package com.example.arturarzumanyan.taskmanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.arturarzumanyan.taskmanager.db.EventsContract.*;

public class EventsDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "EventsDatabase.db";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase db;

    public EventsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        final String SQL_CREATE_EVENTS_TABLE = "CREATE TABLE " +
                EventsTable.TABLE_NAME + " ( " +
                EventsTable._ID + "INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EventsTable.COLUMN_EVENT_ID + " TEXT, " +
                EventsTable.COLUMN_NAME + " TEXT, " +
                EventsTable.COLUMN_DESCRIPTION + " TEXT, " +
                EventsTable.COLUMN_COLOR_ID + " INTEGER, " +
                EventsTable.COLUMN_DATE + " TEXT, " +
                EventsTable.COLUMN_START_TIME + " TEXT, " +
                EventsTable.COLUMN_END_TIME + " TEXT, " +
                EventsTable.COLUMN_REMINDER + " INTEGER" +
                ")";

        db.execSQL(SQL_CREATE_EVENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + EventsTable.TABLE_NAME);
        onCreate(db);
    }
}
