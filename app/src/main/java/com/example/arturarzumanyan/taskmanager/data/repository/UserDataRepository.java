package com.example.arturarzumanyan.taskmanager.data.repository;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsCloudStore;
import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsDbStore;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.TaskListsCloudStore;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.TaskListsDbStore;
import com.example.arturarzumanyan.taskmanager.data.repository.tasks.TasksCloudStore;
import com.example.arturarzumanyan.taskmanager.data.repository.tasks.TasksDbStore;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;

import java.text.ParseException;
import java.util.ArrayList;

public class UserDataRepository {
    private EventsDbStore eventsDbStore;
    private TaskListsDbStore taskListsDbStore;
    private TasksDbStore tasksDbStore;
    private TasksCloudStore tasksCloudStore;
    private EventsCloudStore eventsCloudStore;
    private TaskListsCloudStore taskListsCloudStore;

    public void loadUserData(Context context) {
        final Context tempContext = context;
        eventsDbStore = new EventsDbStore();
        taskListsDbStore = new TaskListsDbStore();
        tasksCloudStore = new TasksCloudStore();
        tasksDbStore = new TasksDbStore();
        eventsCloudStore = new EventsCloudStore();
        taskListsCloudStore = new TaskListsCloudStore();

        ArrayList<Event> events = new ArrayList<>();
        try {
            events = eventsDbStore.getEvents(tempContext);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if ((isOnline(context)) && (events.size() == 0)) {
            eventsCloudStore.getEvents(tempContext, new EventsCloudStore.OnTaskCompletedListener() {
                @Override
                public void onSuccess(ArrayList<Event> eventsList) {
                    ArrayList<Event> events = eventsList;
                    eventsDbStore.addEvents(tempContext, events);
                }

                @Override
                public void onfail() {

                }
            });

            taskListsCloudStore.getTaskLists(tempContext, new TaskListsCloudStore.OnTaskCompletedListener() {
                @Override
                public void onSuccess(ArrayList<TaskList> taskListArrayList) {
                    taskListsDbStore.addTaskLists(tempContext, taskListArrayList);
                    ArrayList<TaskList> taskLists = taskListsDbStore.getTaskLists(tempContext);
                    for (int i = 0; i < taskLists.size(); i++) {
                        final int position = i;
                        tasksCloudStore.getTasksFromTaskList(tempContext,
                                taskLists.get(i),
                                new TasksCloudStore.OnTaskCompletedListener() {
                                    @Override
                                    public void onSuccess(ArrayList<Task> taskArrayList) {
                                        tasksDbStore.addTasks(tempContext, taskArrayList);
                                        if (position == taskListsDbStore.getTaskLists(tempContext).size() - 1) {
                                            //notifyDataLoaded();
                                        }
                                    }

                                    @Override
                                    public void onfail() {

                                    }
                                });
                    }
                }

                @Override
                public void onfail() {

                }
            });
        }


    }

    private boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}