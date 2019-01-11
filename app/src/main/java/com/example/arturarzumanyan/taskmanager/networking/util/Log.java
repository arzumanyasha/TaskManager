package com.example.arturarzumanyan.taskmanager.networking.util;

import android.text.TextUtils;

public final class Log {

    private static final String TAG = "TaskManager";
    private static final String ERROR_TAG = "Error";

    public static void v(String msg) {
        android.util.Log.v(TAG, getLocation() + msg);
    }

    public static void e(String msg) {
        android.util.Log.v(ERROR_TAG, getLocation() + msg);
    }

    private static String getLocation() {
        final String className = Log.class.getName();
        final StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        boolean found = false;

        for (StackTraceElement trace : traces) {
            try {
                if (found) {
                    if (!trace.getClassName().startsWith(className)) {
                        Class<?> clazz = Class.forName(trace.getClassName());
                        return "[" + getClassName(clazz) + ":" + trace.getMethodName() + ":" + trace.getLineNumber() + "]: ";
                    }
                } else if (trace.getClassName().startsWith(className)) {
                    found = true;
                }
            } catch (ClassNotFoundException e) {
                Log.e(e.getMessage());
                e.printStackTrace();
            }
        }

        return "[]: ";
    }

    private static String getClassName(Class<?> clazz) {
        if (clazz != null) {
            if (!TextUtils.isEmpty(clazz.getSimpleName())) {
                return clazz.getSimpleName();
            }

            return getClassName(clazz.getEnclosingClass());
        }

        return "";
    }
}