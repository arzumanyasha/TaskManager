package com.example.arturarzumanyan.taskmanager.data.repository.tasklists;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.data.repository.tasks.TasksCloudStore;
import com.example.arturarzumanyan.taskmanager.data.repository.tasks.TasksDbStore;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;

import java.util.ArrayList;

public class TaskListsRepository {
    private TaskListsCloudStore mTaskListsCloudStore;
    private TaskListsDbStore mTaskListsDbStore;
    private TasksDbStore mTasksDbStore;
    private TasksCloudStore mTasksCloudStore;
    private RepositoryLoadHelper mRepositoryLoadHelper;
    private Context mContext;

    public TaskListsRepository(Context context) {
        this.mContext = context;
        mTaskListsCloudStore = new TaskListsCloudStore(mContext);
        mTaskListsDbStore = new TaskListsDbStore(mContext);
        mTasksCloudStore = new TasksCloudStore(mContext);
        mTasksDbStore = new TasksDbStore(mContext);
        mRepositoryLoadHelper = new RepositoryLoadHelper(mContext);
    }

    public void loadTaskLists(final OnTaskListsLoadedListener listener) {
        ArrayList<TaskList> taskLists = mTaskListsDbStore.getTaskLists();

        if ((mRepositoryLoadHelper.isOnline()) && (taskLists.size() == 0)) {
            mTaskListsCloudStore.getTaskLists(new TaskListsCloudStore.OnTaskCompletedListener() {
                @Override
                public void onSuccess(ArrayList<TaskList> taskListArrayList) {
                    mTaskListsDbStore.addTaskLists(taskListArrayList);
                    ArrayList<TaskList> taskLists = mTaskListsDbStore.getTaskLists();
                    //listener.onSuccess(taskListsDbStore.getTaskLists());
                    for (int i = 0; i < taskLists.size(); i++) {
                        final int position = i;
                        mTasksCloudStore.getTasksFromTaskList(taskLists.get(i), new TasksCloudStore.OnTaskCompletedListener() {
                            @Override
                            public void onSuccess(ArrayList<Task> taskArrayList) {
                                mTasksDbStore.addTasks(taskArrayList);
                                if (position == mTaskListsDbStore.getTaskLists().size() - 1) {
                                    listener.onSuccess(mTaskListsDbStore.getTaskLists());
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
        } else if ((mRepositoryLoadHelper.isOnline()) && (taskLists.size() != 0)) {
            listener.onSuccess(taskLists);
        }
    }

    public void addTaskList() {
    }

    public void updateTaskList() {
    }

    public void deleteTaskList() {
    }

    public interface OnTaskListsLoadedListener {
        void onSuccess(ArrayList<TaskList> taskLists);

        void onfail();
    }
}
