package com.example.arturarzumanyan.taskmanager.data.repository.tasks;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;

import java.util.List;

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
        List<Task> tasks = mTasksDbStore.getTasksFromTaskList(taskList.getId());

        if ((mRepositoryLoadHelper.isOnline()) && (tasks.size() == 0)) {
            mTasksCloudStore.getTasksFromTaskList(taskList, new TasksCloudStore.OnTaskCompletedListener() {
                @Override
                public void onSuccess(List<Task> taskArrayList) {
                    listener.onSuccess(taskArrayList);
                    mTasksDbStore.addTasks(taskArrayList);
                }

                @Override
                public void onFail() {

                }
            });
        } else if ((mRepositoryLoadHelper.isOnline() && (tasks.size() != 0)) ||
                (!mRepositoryLoadHelper.isOnline() && (tasks.size() != 0))) {
            listener.onSuccess(tasks);
        } else {
            listener.onFail();
        }
    }

    public List<Task> getTasksFromTaskList(int taskListId) {
        return mTasksDbStore.getTasksFromTaskList(taskListId);
    }

    public interface OnTasksLoadedListener {
        void onSuccess(List<Task> taskArrayList);

        void onFail();
    }

    public void addTask(Task task, TasksCloudStore.OnTaskCompletedListener listener) {
        if (mRepositoryLoadHelper.isOnline()) {
            mTasksCloudStore.addTask(task, listener);
        } else {
            mTasksDbStore.addTask(task);
        }
    }

    public void updateTask(Task task, TasksCloudStore.OnTaskCompletedListener listener) {
        if (mRepositoryLoadHelper.isOnline()) {
            mTasksCloudStore.updateTask(task, listener);
        } else {
            mTasksDbStore.updateTask(task);
        }
    }

    public void deleteTask(Task task) {
        if (mRepositoryLoadHelper.isOnline()) {
            mTasksCloudStore.deleteTask(task);
        } else {
            mTasksDbStore.deleteTask(task);
        }

    }
}

