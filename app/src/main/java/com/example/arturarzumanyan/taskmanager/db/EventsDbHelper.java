package com.example.arturarzumanyan.taskmanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.arturarzumanyan.taskmanager.db.EventsContract.*;
import com.example.arturarzumanyan.taskmanager.domain.Event;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EventsDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "EventsDatabase.db";
    private static final int DATABASE_VERSION = 4;

    private SQLiteDatabase db;

    public EventsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        final String SQL_CREATE_EVENTS_TABLE = "CREATE TABLE " +
                EventsTable.TABLE_NAME + " ( " +
                EventsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EventsTable.COLUMN_EVENT_ID + " TEXT, " +
                EventsTable.COLUMN_NAME + " TEXT, " +
                EventsTable.COLUMN_DESCRIPTION + " TEXT, " +
                EventsTable.COLUMN_COLOR_ID + " INTEGER, " +
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

    public void insertEvents(ArrayList<Event> eventsList) {
        db = getWritableDatabase();
        for (int i = 0; i < eventsList.size(); i++) {
            ContentValues cv = new ContentValues();

            cv.put(EventsTable.COLUMN_EVENT_ID, eventsList.get(i).getId());
            cv.put(EventsTable.COLUMN_NAME, eventsList.get(i).getName());
            cv.put(EventsTable.COLUMN_DESCRIPTION, eventsList.get(i).getDescription());
            cv.put(EventsTable.COLUMN_COLOR_ID, eventsList.get(i).getColorId());

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            cv.put(EventsTable.COLUMN_START_TIME, dateFormat.format(eventsList.get(i).getStartTime()));

            cv.put(EventsTable.COLUMN_END_TIME, dateFormat.format(eventsList.get(i).getEndTime()));

            if (eventsList.get(i).isNotify()) {
                cv.put(EventsTable.COLUMN_REMINDER, 1);
            } else {
                cv.put(EventsTable.COLUMN_REMINDER, 0);
            }

            db.insert(EventsTable.TABLE_NAME, null, cv);
        }
    }

    public ArrayList<Event> getEvents() throws ParseException {
        ArrayList<Event> eventsList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + EventsTable.TABLE_NAME, null);

        if (c.moveToFirst()) {
            do {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                Date startDate = dateFormat.parse(c.getString(c.getColumnIndex(EventsTable.COLUMN_START_TIME)));
                Date endDate = dateFormat.parse(c.getString(c.getColumnIndex(EventsTable.COLUMN_END_TIME)));
                Boolean isNotify;
                if (c.getInt(c.getColumnIndex(EventsTable.COLUMN_REMINDER)) == 1) {
                    isNotify = true;
                } else
                    isNotify = false;

                Event event = new Event(c.getString(c.getColumnIndex(EventsTable.COLUMN_EVENT_ID)),
                        c.getString(c.getColumnIndex(EventsTable.COLUMN_NAME)),
                        c.getString(c.getColumnIndex(EventsTable.COLUMN_DESCRIPTION)),
                        c.getInt(c.getColumnIndex(EventsTable.COLUMN_COLOR_ID)),
                        startDate,
                        endDate,
                        isNotify
                );

                eventsList.add(event);
            } while (c.moveToNext());
        }

        c.close();
        return eventsList;
    }

}
