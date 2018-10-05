package com.example.arturarzumanyan.taskmanager.db;

import android.provider.BaseColumns;

public final class TasksContract {
    private TasksContract() {
    }

    public static class TaskListTable implements BaseColumns {
        public static final String TABLE_NAME = "task_lists";
        public static final String COLUMN_LIST_ID = "id";
        public static final String COLUMN_TITLE = "title";
    }

    public static class TasksTable implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_TASK_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_NOTES = "notes";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_DUE = "due";
        public static final String COLUMN_LIST_ID = "list_id";
    }
}
