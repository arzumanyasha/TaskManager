package com.example.arturarzumanyan.taskmanager.data.repository.tasklists;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.data.repository.tasks.TasksCloudStore;
import com.example.arturarzumanyan.taskmanager.data.repository.tasks.TasksDbStore;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;

import java.util.ArrayList;

public class TaskListsRepository {
    private TaskListsCloudStore taskListsCloudStore;
    private TaskListsDbStore taskListsDbStore;
    private TasksDbStore tasksDbStore;
    private TasksCloudStore tasksCloudStore;
    private RepositoryLoadHelper mRepositoryLoadHelper;
    private Context mContext;

    public TaskListsRepository(Context context) {
        this.mContext = context;
    }

    public void loadTaskLists(final OnTaskListsLoadedListener listener) {
        taskListsCloudStore = new TaskListsCloudStore(mContext);
        taskListsDbStore = new TaskListsDbStore(mContext);
        tasksCloudStore = new TasksCloudStore(mContext);
        tasksDbStore = new TasksDbStore(mContext);

        ArrayList<TaskList> taskLists = taskListsDbStore.getTaskLists();
        mRepositoryLoadHelper = new RepositoryLoadHelper(mContext);

        if ((mRepositoryLoadHelper.isOnline()) && (taskLists.size() == 0)) {
            taskListsCloudStore.getTaskLists(new TaskListsCloudStore.OnTaskCompletedListener() {
                @Override
                public void onSuccess(ArrayList<TaskList> taskListArrayList) {
                    taskListsDbStore.addTaskLists(taskListArrayList);
                    ArrayList<TaskList> taskLists = taskListsDbStore.getTaskLists();
                    listener.onSuccess(taskListsDbStore.getTaskLists());
                    for (int i = 0; i < taskLists.size(); i++){
                        tasksCloudStore.getTasksFromTaskList(taskLists.get(i), new TasksCloudStore.OnTaskCompletedListener() {
                            @Override
                            public void onSuccess(ArrayList<Task> taskArrayList) {
                                tasksDbStore.addTasks(taskArrayList);
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
