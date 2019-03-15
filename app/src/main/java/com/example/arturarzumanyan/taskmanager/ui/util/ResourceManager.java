package com.example.arturarzumanyan.taskmanager.ui.util;

import android.content.Context;
import android.util.SparseIntArray;

import com.example.arturarzumanyan.taskmanager.R;

public class ResourceManager {
    public enum State {
        AUTHENTICATION_ERROR, FAILED_TO_CREATE_EVENT_ERROR,
        FAILED_TO_UPDATE_EVENT_ERROR, FAILED_TO_LOAD_EVENTS_ERROR,
        FAILED_TO_DELETE_EVENT_ERROR
    }

    private static ResourceManager resourceManager;
    private static ColorPalette colorPalette;
    private static Context context;

    public static void initResourceManager(Context appContext) {
        if (resourceManager == null) {
            colorPalette = new ColorPalette(appContext);
            context = appContext;
            resourceManager = new ResourceManager();
        }
    }

    public synchronized static ResourceManager getResourceManager() {
        return resourceManager;
    }

    public SparseIntArray getColorPalette() {
        return colorPalette.getColorPalette();
    }

    public String getErrorMessage(State state) {
        switch (state) {
            case AUTHENTICATION_ERROR:
                return context.getString(R.string.failed_log_in_message);
            case FAILED_TO_CREATE_EVENT_ERROR:
                return context.getString(R.string.failed_to_create_event_msg);
            case FAILED_TO_UPDATE_EVENT_ERROR:
                return context.getString(R.string.failed_to_update_event_msg);
            case FAILED_TO_LOAD_EVENTS_ERROR:
                return context.getString(R.string.failed_to_load_events_msg);
            case FAILED_TO_DELETE_EVENT_ERROR:
                return context.getString(R.string.failed_to_delete_event_msg);
            default:
                return null;
        }
    }
}
