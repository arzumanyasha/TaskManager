package com.example.arturarzumanyan.taskmanager.networking.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    private static final String EVENT_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static final String TASK_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String HOUR_MINUTE_TIME_PATTERN = "HH:mm a";
    private static final String YEAR_MONTH_DAY_DATE_PATTERN = "yyyy-MM-dd";

    public static String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        Date currentDate = c.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YEAR_MONTH_DAY_DATE_PATTERN);
        return simpleDateFormat.format(currentDate);
    }

    public static String getTaskDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YEAR_MONTH_DAY_DATE_PATTERN);
        return simpleDateFormat.format(getTaskDateFromString(date));
    }

    public static Date getEventDateFromString(String date) {
        DateFormat dateFormat = new SimpleDateFormat(EVENT_TIME_PATTERN);
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date getTaskDateFromString(String date) {
        DateFormat dateFormat = new SimpleDateFormat(TASK_DATE_PATTERN);
        if (isValidFormat(TASK_DATE_PATTERN, date)) {
            try {
                return dateFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (isValidFormat(YEAR_MONTH_DAY_DATE_PATTERN, date)) {
            try {
                return dateFormat.parse(date + "T00:00:00.000Z");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static String formatTime(Date time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(HOUR_MINUTE_TIME_PATTERN);
        return simpleDateFormat.format(time);
    }

    public static String formatEventTime(Date time) {
        DateFormat dateFormat = new SimpleDateFormat(EVENT_TIME_PATTERN);
        return dateFormat.format(time);
    }

    public static String formatTaskDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat(TASK_DATE_PATTERN);
        return dateFormat.format(date);
    }

    public static int getHour() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinute() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MINUTE);
    }

    public static int getDay() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public static int getMonth() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH);
    }

    public static int getYear() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR);
    }

    private static boolean isValidFormat(String format, String value) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date != null;
    }
}
