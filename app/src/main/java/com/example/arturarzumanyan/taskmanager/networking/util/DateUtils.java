package com.example.arturarzumanyan.taskmanager.networking.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final String EVENT_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static final String TASK_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String HOUR_MINUTE_TIME_A_PATTERN = "HH:mm a";
    private static final String HOUR_MINUTE_TIME_PATTERN = "HH:mm";
    private static final String YEAR_MONTH_DAY_DATE_PATTERN = "yyyy-MM-dd";
    private static final String DAY_MONTH_YEAR_DATE_PATTERN = "dd-MM-yyyy";
    private static final String YEAR_MONTH_DAY_PATTERN = "yyyyMMdd";
    private static final int HOURS_IN_DAY = 24;
    private static final int MILLIS_IN_SECONDS = 1000;
    private static final int SECONDS_IN_MINUTES = 60;
    public static final int MINUTES_IN_HOUR = 60;
    public static final int DAYS_IN_WEEK = 7;

    public static String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        Date currentDate = c.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YEAR_MONTH_DAY_DATE_PATTERN, Locale.getDefault());
        return simpleDateFormat.format(currentDate);
    }

    public static String getTaskDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YEAR_MONTH_DAY_DATE_PATTERN, Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public static Date getEventDateFromString(String date) {
        DateFormat dateFormat = new SimpleDateFormat(EVENT_TIME_PATTERN, Locale.getDefault());
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String trimEventDate(String date) {
        return DateUtils.formatEventDate(DateUtils.getEventDateFromString(date));
    }

    public static String formatEventDate(Date eventDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YEAR_MONTH_DAY_DATE_PATTERN, Locale.getDefault());
        return simpleDateFormat.format(eventDate);
    }

    public static Date getEventDateWithoutTime(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YEAR_MONTH_DAY_DATE_PATTERN, Locale.getDefault());
        try {
            return simpleDateFormat.parse(simpleDateFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatReversedYearMonthDayDate(String date) {
        return reverseDate(date, DAY_MONTH_YEAR_DATE_PATTERN, YEAR_MONTH_DAY_DATE_PATTERN);
    }

    public static String formatReversedDayMonthYearDate(String date) {
        return reverseDate(date, YEAR_MONTH_DAY_DATE_PATTERN, DAY_MONTH_YEAR_DATE_PATTERN);
    }

    private static String reverseDate(String date, String reverseFromPattern, String reverseToPattern) {
        SimpleDateFormat simpleDateFormatTo = new SimpleDateFormat(reverseToPattern, Locale.getDefault());
        SimpleDateFormat simpleDateFormatFrom = new SimpleDateFormat(reverseFromPattern, Locale.getDefault());
        if(isValidFormat(reverseFromPattern, date)){
            Date reversedDate = null;
            try {
                reversedDate = simpleDateFormatFrom.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return simpleDateFormatTo.format(reversedDate);
        }
        return null;
    }

    public static Date getTaskDateFromString(String date) {
        DateFormat dateFormat = new SimpleDateFormat(TASK_DATE_PATTERN, Locale.ENGLISH);
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

    public static Date getEventDate(String date, Date time) {
        DateFormat dateFormat = new SimpleDateFormat(EVENT_TIME_PATTERN, Locale.ENGLISH);
        String fullDate = date + "T" + time.getHours() + ":" + time.getMinutes() + ":00+0300";
        try {
            return dateFormat.parse(fullDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatHourMinuteTime(int hour, int minute) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(HOUR_MINUTE_TIME_PATTERN, Locale.getDefault());
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        return simpleDateFormat.format(c.getTime());
    }

    public static int getDaysInCurrentMonth() {
        Calendar c = Calendar.getInstance();
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static Date getFirstDateOfMonth() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

    public static Date getLastDateOfMonth() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, getDaysInCurrentMonth());
        return c.getTime();
    }

    public static String formatTime(Date time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(HOUR_MINUTE_TIME_A_PATTERN, Locale.getDefault());
        return simpleDateFormat.format(time);
    }

    public static String formatTimeWithoutA(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(HOUR_MINUTE_TIME_PATTERN, Locale.getDefault());
        return simpleDateFormat.format(time);
    }

    public static Date getTimeWithoutA(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(HOUR_MINUTE_TIME_PATTERN, Locale.getDefault());
        try {
            return simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatEventTime(Date time) {
        DateFormat dateFormat = new SimpleDateFormat(EVENT_TIME_PATTERN, Locale.getDefault());
        return dateFormat.format(time);
    }

    public static String formatTaskDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat(TASK_DATE_PATTERN, Locale.getDefault());
        return dateFormat.format(date);
    }

    public static int getEventWeek(String eventDate) {
        SimpleDateFormat df = new SimpleDateFormat(YEAR_MONTH_DAY_DATE_PATTERN, Locale.getDefault());
        Date date = null;
        try {
            date = df.parse(eventDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public static Date getMondayDate(int currentWeekDay) {
        SimpleDateFormat df = new SimpleDateFormat(YEAR_MONTH_DAY_PATTERN, Locale.getDefault());
        Calendar c = Calendar.getInstance();
        Date currentDate = null;
        try {
            currentDate = df.parse(df.format(c.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (currentDate != null) {
            return new Date(currentDate.getTime() - currentWeekDay * MILLIS_IN_SECONDS
                    * SECONDS_IN_MINUTES * MINUTES_IN_HOUR * HOURS_IN_DAY);
        } else {
            return null;
        }
    }

    public static Date getSundayDate(int currentWeekDay) {
        SimpleDateFormat df = new SimpleDateFormat(YEAR_MONTH_DAY_PATTERN, Locale.getDefault());
        Calendar c = Calendar.getInstance();
        Date currentDate = null;
        try {
            currentDate = df.parse(df.format(c.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (currentDate != null) {
            return new Date(currentDate.getTime() - (currentWeekDay - DAYS_IN_WEEK - 1) * MILLIS_IN_SECONDS
                    * SECONDS_IN_MINUTES * MINUTES_IN_HOUR * HOURS_IN_DAY);
        } else {
            return null;
        }
    }

    public static String getStringDateFromInt(int year, int month, int day) {
        SimpleDateFormat df = new SimpleDateFormat(DAY_MONTH_YEAR_DATE_PATTERN, Locale.ENGLISH);
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        return df.format(c.getTime());
    }

    public static Date getNextDate(Date date) {
        return new Date(date.getTime() + MILLIS_IN_SECONDS
                * SECONDS_IN_MINUTES * MINUTES_IN_HOUR * HOURS_IN_DAY);
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

    public static boolean isMatchesEventFormat(String date) {
        return isValidFormat(EVENT_TIME_PATTERN, date);
    }

    private static boolean isValidFormat(String format, String value) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
            date = sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        } finally {
            return date != null;
        }
    }

    public static String decodeDate(String date) {
        date = date.replaceAll(":", "%3A");
        date = date.replaceAll("\\+", "%2B");
        return date;
    }
}
