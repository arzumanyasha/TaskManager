package com.example.arturarzumanyan.taskmanager.data.repository.tasks;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsCloudStore;
import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsRepository;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;

import java.util.ArrayList;

public class TasksRepository {
    private TasksDbStore mTasksDbStore;
    private TasksCloudStore mTasksCloudStore;

    private RepositoryLoadHelper mRepositoryLoadHelper;

    private Context mContext;

    public TasksRepository(Context context) {
        this.mContext = context;
        mTasksCloudStore = new TasksCloudStore(mContext);
        mTasksDbStore = new TasksDbStore(mContext);
        mRepositoryLoadHelper = new RepositoryLoadHelper(mContext);
    }

    public void loadTasks(TaskList taskList, final OnTasksLoadedListener listener) {
        ArrayList<Task> tasks = mTasksDbStore.getTasksFromTaskList(taskList.getId());

        if ((mRepositoryLoadHelper.isOnline()) && (tasks.size() == 0)) {
            mTasksCloudStore.getTasksFromTaskList(taskList, new TasksCloudStore.OnTaskCompletedListener() {
                @Override
                public void onSuccess(ArrayList<Task> taskArrayList) {
                    listener.onSuccess(taskArrayList);
                    mTasksDbStore.addTasks(taskArrayList);
                }

                @Override
                public void onfail() {

                }
            });
        } else if ((mRepositoryLoadHelper.isOnline()) && (tasks.size() != 0)) {
            listener.onSuccess(tasks);
        }
    }

    public interface OnTasksLoadedListener {
        void onSuccess(ArrayList<Task> taskArrayList);

        void onfail();
    }
}
