package com.example.arturarzumanyan.taskmanager.db.contract;

import android.provider.BaseColumns;

public final class EventsContract {
    private EventsContract() {
    }

    public static class EventsTable implements BaseColumns {
        public static final String TABLE_NAME = "events";
        public static final String COLUMN_EVENT_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_COLOR_ID = "color_id";
        public static final String COLUMN_START_TIME = "start_time";
        public static final String COLUMN_END_TIME = "end_time";
        public static final String COLUMN_REMINDER = "reminder";
    }
}
