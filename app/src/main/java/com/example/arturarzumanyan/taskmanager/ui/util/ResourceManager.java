package com.example.arturarzumanyan.taskmanager.ui.util;

import android.content.Context;
import android.util.SparseIntArray;

import com.example.arturarzumanyan.taskmanager.R;

public class ResourceManager {
    public enum State {
        AUTHENTICATION_ERROR, FAILED_TO_CREATE_EVENT_ERROR,
        FAILED_TO_UPDATE_EVENT_ERROR, FAILED_TO_LOAD_EVENTS_ERROR,
        FAILED_TO_DELETE_EVENT_ERROR, FAILED_TO_LOAD_TASKS,
        FAILED_TO_CREATE_TASK, FAILED_TO_UPDATE_TASK,
        FAILED_TO_DELETE_TASK, FAILED_TO_LOAD_TASK_LISTS,
        FAILED_TO_CREATE_TASK_LIST, FAILED_TO_UPDATE_TASK_LIST,
        FAILED_TO_DELETE_TASK_LIST, DEFAULT_TASK_LIST_DELETING_ERROR
    }

    private static ResourceManager resourceManager;
    private ColorPalette colorPalette;
    private Context context;

    public static void initResourceManager(Context appContext) {
        if (resourceManager == null) {
            resourceManager = new ResourceManager();
            resourceManager.colorPalette = new ColorPalette(appContext);
            resourceManager.context = appContext;
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
            case FAILED_TO_LOAD_TASKS:
                return context.getString(R.string.failed_to_load_tasks_msg);
            case FAILED_TO_CREATE_TASK:
                return context.getString(R.string.failed_to_create_task_msg);
            case FAILED_TO_UPDATE_TASK:
                return context.getString(R.string.failed_to_update_task_msg);
            case FAILED_TO_DELETE_TASK:
                return context.getString(R.string.failed_to_delete_task_msg);
            case FAILED_TO_LOAD_TASK_LISTS:
                return context.getString(R.string.failed_to_load_tasklists_msg);
            case FAILED_TO_CREATE_TASK_LIST:
                return context.getString(R.string.failed_to_create_tasklist_msg);
            case FAILED_TO_UPDATE_TASK_LIST:
                return context.getString(R.string.failed_to_update_tasklist_msg);
            case FAILED_TO_DELETE_TASK_LIST:
                return context.getString(R.string.failed_to_delete_tasklist_msg);
            case DEFAULT_TASK_LIST_DELETING_ERROR:
                return context.getString(R.string.default_tasklist_error_msg);
            default:
                return null;
        }
    }
}
