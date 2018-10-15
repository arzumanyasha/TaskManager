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

    public String getCurrentTime(){
        Calendar c = Calendar.getInstance();
        Date currentDate = c.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YEAR_MONTH_DAY_DATE_PATTERN);
        return simpleDateFormat.format(currentDate);
    }

    public Date getEventDateFromString(String date) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(EVENT_TIME_PATTERN);
        return dateFormat.parse(date);
    }

    public Date getTaskDateFromString(String date) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(TASK_DATE_PATTERN);
        return dateFormat.parse(date);
    }

    public String formatTime(Date time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(HOUR_MINUTE_TIME_PATTERN);
        return simpleDateFormat.format(time);
    }

    public String formatEventTime(Date time){
        DateFormat dateFormat = new SimpleDateFormat(EVENT_TIME_PATTERN);
        return dateFormat.format(time);
    }

    public String formatTaskDate(Date date){
        DateFormat dateFormat = new SimpleDateFormat(TASK_DATE_PATTERN);
        return dateFormat.format(date);
    }


}
