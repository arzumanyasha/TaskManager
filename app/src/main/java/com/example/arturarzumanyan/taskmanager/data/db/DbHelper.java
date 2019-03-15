package com.example.arturarzumanyan.taskmanager.data.db;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.EventsSpecification;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.specification.TaskListsSpecification;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.util.Log;

import java.util.Collections;
import java.util.List;

public class DbHelper {
    private static DbHelper mDbInstance;

    public static void initDbHelperInstance(Context context) {
        if (mDbInstance == null) {
            mDbInstance = new DbHelper();
            AppDatabase.initAppDatabase(context);
        }
    }

    public synchronized static DbHelper getDbHelperInstance() {
        return mDbInstance;
    }

    private DbHelper() {
    }

    public Boolean addOrUpdateEvents(List<Event> eventsList) {
        try {
            for (Event event : eventsList) {
                insertOrUpdateEvent(event);
            }
            return Boolean.TRUE;
        } catch (Exception ex) {
            Log.e(ex.getMessage());
            return Boolean.FALSE;
        }
    }

    private void insertOrUpdateEvent(Event event) {
        if (isEventExistsInDb(event.getId())) {
            AppDatabase.getAppDatabase().eventDao().update(event);
        } else {
            AppDatabase.getAppDatabase().eventDao().insert(event);
        }
    }

    private boolean isEventExistsInDb(int id) {
        return AppDatabase.getAppDatabase().eventDao().getEventById(id) != null;
    }

    public Boolean deleteEvent(Event event) {
        try {
            AppDatabase.getAppDatabase().eventDao().delete(event);
            return Boolean.TRUE;
        } catch (Exception ex) {
            Log.e(ex.getMessage());
            return false;
        }
    }

    public List<Event> getEvents(EventsSpecification specification) {
        return AppDatabase.getAppDatabase().eventDao().getEvents(specification.getStartDate(), specification.getEndDate());
    }

    public void addOrUpdateTaskLists(List<TaskList> taskLists) {
        for (TaskList taskList : taskLists) {
            insertOrUpdateTaskList(taskList);
        }
    }

    private void insertOrUpdateTaskList(TaskList taskList) {
        if (isTaskListExistsInDb(taskList.getId())) {
            AppDatabase.getAppDatabase().taskListDao().update(taskList);
        } else {
            AppDatabase.getAppDatabase().taskListDao().insert(taskList);
        }
    }

    private boolean isTaskListExistsInDb(int taskListId) {
        return AppDatabase.getAppDatabase().taskListDao().getTaskListById(taskListId) != null;
    }

    public List<TaskList> getTaskLists(TaskListsSpecification specification) {
        if (specification.getSelectionArgs() == 0) {
            return AppDatabase.getAppDatabase().taskListDao().getTaskLists();
        } else {
            return Collections.singletonList(AppDatabase.getAppDatabase().taskListDao().getTaskListById(specification.getSelectionArgs()));
        }
    }

    public void deleteTaskList(TaskList taskList) {
        AppDatabase.getAppDatabase().taskListDao().delete(taskList);
    }

    public void addOrUpdateTasks(List<Task> tasks) {
        for (Task task : tasks) {
            insertOrUpdateTask(task);
        }
    }

    private void insertOrUpdateTask(Task task) {
        if (isTaskExistsInDb(task.getId())) {
            AppDatabase.getAppDatabase().taskDao().update(task);
        } else {
            AppDatabase.getAppDatabase().taskDao().insert(task);
        }
    }

    private boolean isTaskExistsInDb(int taskId) {
        return AppDatabase.getAppDatabase().taskDao().getTaskById(taskId) != null;
    }

    public void deleteTask(Task task) {
        AppDatabase.getAppDatabase().taskDao().delete(task);
    }

    public List<Task> getTasksFromList(int tasksListId) {
        return AppDatabase.getAppDatabase().taskDao().getTasksFromList(tasksListId);
    }
}
