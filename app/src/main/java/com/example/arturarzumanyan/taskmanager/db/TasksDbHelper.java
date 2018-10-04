package com.example.arturarzumanyan.taskmanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.db.TasksContract.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TasksDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Tasks.db";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase db;

    public TasksDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

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
                TasksTable.COLUMN_LIST_TITLE + " TEXT" +
                ")";

        db.execSQL(SQL_CREATE_TASK_LISTS_TABLE);
        db.execSQL(SQL_CREATE_TASKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TasksTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TaskListTable.TABLE_NAME);
        onCreate(db);
    }

    public void insertTaskLists(ArrayList<TaskList> taskListArrayList) {
        db = getWritableDatabase();
        for (int i = 0; i < taskListArrayList.size(); i++) {
            ContentValues cv = new ContentValues();

            cv.put(TaskListTable.COLUMN_LIST_ID, taskListArrayList.get(i).getId());
            cv.put(TaskListTable.COLUMN_TITLE, taskListArrayList.get(i).getTitle());

            db.insert(TaskListTable.TABLE_NAME, null, cv);
        }
    }

    public void insertTasks(ArrayList<Task> tasksArrayList) {
        db = getWritableDatabase();
        for (int i = 0; i < tasksArrayList.size(); i++) {
            ContentValues cv = new ContentValues();

            cv.put(TasksTable.COLUMN_TASK_ID, tasksArrayList.get(i).getId());
            cv.put(TasksTable.COLUMN_TITLE, tasksArrayList.get(i).getName());
            cv.put(TasksTable.COLUMN_NOTES, tasksArrayList.get(i).getDescription());

            if (tasksArrayList.get(i).isExecuted()) {
                cv.put(TasksTable.COLUMN_STATUS, 1);
            } else {
                cv.put(TasksTable.COLUMN_STATUS, 0);
            }

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            cv.put(TasksTable.COLUMN_DUE, dateFormat.format(tasksArrayList.get(i).getDate()));

            cv.put(TasksTable.COLUMN_LIST_TITLE, tasksArrayList.get(i).getListName());

            db.insert(TasksTable.TABLE_NAME, null, cv);
        }
    }

    public ArrayList<Task> getTasks(String tasksListTitle) throws ParseException {
        ArrayList<Task> tasksList = new ArrayList<>();
        db = getReadableDatabase();

        String[] selectionArgs = new String[]{tasksListTitle};
        Cursor c = db.rawQuery("SELECT * FROM " + TasksTable.TABLE_NAME +
                " WHERE " + TasksTable.COLUMN_LIST_TITLE + " = ?", selectionArgs);

        if (c.moveToFirst()) {
            do {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                Date date = dateFormat.parse(c.getString(c.getColumnIndex(TasksTable.COLUMN_DUE)));

                Boolean isExecuted;
                if (c.getInt(c.getColumnIndex(TasksContract.TasksTable.COLUMN_STATUS)) == 1) {
                    isExecuted = true;
                } else
                    isExecuted = false;

                Task task = new Task(c.getString(c.getColumnIndex(TasksContract.TasksTable.COLUMN_TASK_ID)),
                        c.getString(c.getColumnIndex(TasksContract.TasksTable.COLUMN_TITLE)),
                        c.getString(c.getColumnIndex(TasksContract.TasksTable.COLUMN_NOTES)),
                        isExecuted,
                        date,
                        tasksListTitle
                );

                tasksList.add(task);
            } while (c.moveToNext());
        }

        c.close();
        return tasksList;
    }
}
