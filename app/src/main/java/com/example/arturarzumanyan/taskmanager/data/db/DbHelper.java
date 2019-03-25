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

    private void insertOrUpdateEvent(Event newEvent) {
        if (isEventExistsInDb(newEvent.getEventId())) {
            Event event = AppDatabase.getAppDatabase().eventDao().getEventById(newEvent.getEventId());
            newEvent.setId(event.getId());
            AppDatabase.getAppDatabase().eventDao().update(newEvent);
        } else {
            AppDatabase.getAppDatabase().eventDao().insert(newEvent);
        }
    }

    private boolean isEventExistsInDb(String id) {
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

    public Boolean addOrUpdateTaskLists(List<TaskList> taskLists) {
        try {
            for (TaskList taskList : taskLists) {
                insertOrUpdateTaskList(taskList);
            }
            return Boolean.TRUE;
        } catch (Exception ex) {
            Log.e(ex.getMessage());
            return Boolean.FALSE;
        }
    }

    private void insertOrUpdateTaskList(TaskList newTaskList) {
        if (isTaskListExistsInDb(newTaskList.getTaskListId())) {
            TaskList taskList = AppDatabase.getAppDatabase().taskListDao()
                    .getTaskListById(newTaskList.getTaskListId());
            newTaskList.setId(taskList.getId());
            AppDatabase.getAppDatabase().taskListDao().update(newTaskList);
        } else {
            AppDatabase.getAppDatabase().taskListDao().insert(newTaskList);
        }
    }

    private boolean isTaskListExistsInDb(String taskListId) {
        return AppDatabase.getAppDatabase().taskListDao().getTaskListById(taskListId) != null;
    }

    public List<TaskList> getTaskLists(TaskListsSpecification specification) {
        if (specification.getSelectionArgs() == null) {
            return AppDatabase.getAppDatabase().taskListDao().getTaskLists();
        } else {
            return Collections.singletonList(AppDatabase.getAppDatabase().taskListDao().getTaskListById(specification.getSelectionArgs()));
        }
    }

    public Boolean deleteTaskList(TaskList taskList) {
        try {
            AppDatabase.getAppDatabase().taskListDao().delete(taskList);
            return Boolean.TRUE;
        } catch (Exception ex) {
            Log.e(ex.getMessage());
            return Boolean.FALSE;
        }
    }

    public Boolean addOrUpdateTasks(List<Task> tasks) {
        try {
            for (Task task : tasks) {
                insertOrUpdateTask(task);
            }
            return Boolean.TRUE;
        } catch (Exception ex) {
            Log.e(ex.getMessage());
            return Boolean.FALSE;
        }
    }

    private void insertOrUpdateTask(Task newTask) {
        if (isTaskExistsInDb(newTask.getTaskId())) {
            Task task = AppDatabase.getAppDatabase().taskDao().getTaskById(newTask.getTaskId());
            newTask.setId(task.getId());
            AppDatabase.getAppDatabase().taskDao().update(newTask);
        } else {
            AppDatabase.getAppDatabase().taskDao().insert(newTask);
        }
    }

    private boolean isTaskExistsInDb(String taskId) {
        return AppDatabase.getAppDatabase().taskDao().getTaskById(taskId) != null;
    }

    public Boolean deleteTask(Task task) {
        try {
            AppDatabase.getAppDatabase().taskDao().delete(task);
            return Boolean.TRUE;
        } catch (Exception ex) {
            Log.e(ex.getMessage());
            return Boolean.FALSE;
        }
    }

    public List<Task> getTasksFromList(int tasksListId) {
        return AppDatabase.getAppDatabase().taskDao().getTasksFromList(tasksListId);
    }
}
