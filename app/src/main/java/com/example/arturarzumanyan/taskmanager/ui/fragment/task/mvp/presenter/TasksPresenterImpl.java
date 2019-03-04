package com.example.arturarzumanyan.taskmanager.ui.fragment.task.mvp.presenter;

import android.os.Bundle;

import com.example.arturarzumanyan.taskmanager.data.repository.tasks.TasksRepository;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.util.Log;
import com.example.arturarzumanyan.taskmanager.ui.adapter.task.mvp.TaskRowView;
import com.example.arturarzumanyan.taskmanager.ui.fragment.task.mvp.contract.TasksContract;

import java.util.List;

import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.PATCH;
import static com.example.arturarzumanyan.taskmanager.ui.activity.intention.IntentionActivity.TASK_LISTS_KEY;

public class TasksPresenterImpl implements TasksContract.TasksPresenter {
    private TasksContract.TasksView mTasksView;
    private TasksRepository mTasksRepository;
    private TaskList mTaskList;
    private List<Task> mTasks;

    public TasksPresenterImpl(TasksContract.TasksView mTasksView) {
        this.mTasksView = mTasksView;
        this.mTasksRepository = new TasksRepository();
    }

    @Override
    public void attachView(TasksContract.TasksView tasksView) {
        this.mTasksView = tasksView;
    }

    @Override
    public void processTasks() {
        if (mTasks == null) {
            loadTasks(mTaskList);
        } else {
            mTasksView.setTasksAdapter(mTasks);
        }
    }

    @Override
    public void loadTasks(TaskList taskList) {
        Log.v("Loading tasks");
        mTasksRepository.loadTasks(taskList, new TasksRepository.OnTasksLoadedListener() {
            @Override
            public void onSuccess(List<Task> taskArrayList) {
                if (mTasksView != null) {
                    mTasks = taskArrayList;
                    mTasksView.setProgressBarInvisible();
                    mTasksView.setTasksAdapter(taskArrayList);
                }
            }

            @Override
            public void onFail(String message) {
                if (mTasksView != null) {
                    mTasksView.onFail(message);
                }
            }

            @Override
            public void onPermissionDenied() {
                /** To-do: add realization with start signInActivity*/
            }
        });
        mTasksView.updateAppTitle(taskList.getTitle());
    }

    @Override
    public void processReceivedBundle(Bundle bundle) {
        if (bundle != null) {
            mTaskList = bundle.getParcelable(TASK_LISTS_KEY);
        }
    }

    private void updateTask(Task task) {
        mTasksRepository.addOrUpdateTask(mTaskList,
                task, PATCH, new TasksRepository.OnTasksLoadedListener() {
                    @Override
                    public void onSuccess(List<Task> taskArrayList) {
                        if (mTasksView != null) {
                            mTasks = taskArrayList;
                            mTasksView.setProgressBarInvisible();
                            mTasksView.setScreenNotTouchable();
                            mTasksView.updateTasksAdapter();
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        if (mTasksView != null) {
                            mTasksView.setProgressBarInvisible();
                            mTasksView.setScreenNotTouchable();
                            mTasksView.onFail(message);
                        }
                    }

                    @Override
                    public void onPermissionDenied() {
                        /** To-do: add realization with start signInActivity*/
                    }
                });
    }

    @Override
    public void processItemDelete(final int position) {
        Task task = mTasks.get(position);
        mTasks.remove(task);
        mTasksRepository.deleteTask(mTaskList, task, new TasksRepository.OnTasksLoadedListener() {
            @Override
            public void onSuccess(List<Task> taskArrayList) {
                if (mTasksView != null) {
                    mTasks = taskArrayList;
                    mTasksView.updateTasksAdapterAfterDelete(position);
                }
            }

            @Override
            public void onFail(String message) {
                if (mTasksView != null) {
                    mTasksView.onFail(message);
                }
            }

            @Override
            public void onPermissionDenied() {
                /** To-do: add realization with start signInActivity*/
            }
        });
    }

    @Override
    public void onBindEventsRowViewAtPosition(int position, TaskRowView rowView) {
        Task task = mTasks.get(position);
        rowView.setItemViewClickListener();
        rowView.setName(task.getName());
        rowView.setDescription(task.getDescription().replaceAll("[\n]", ""));
        rowView.setChecked(task.getIsExecuted() == 1);
        rowView.setDelete();
    }

    @Override
    public void updateTasksList(List<Task> updatedList) {
        mTasks = updatedList;
        mTasksView.updateTasksAdapter();
    }

    @Override
    public void processItemClick(int position) {
        Task task = mTasks.get(position);
        mTasksView.showTaskUpdatingDialog(task, mTaskList);
    }

    @Override
    public void processTaskStatusChanging(int position) {
        Task task = mTasks.get(position);
        mTasks.get(position).setIsExecuted(task.getIsExecuted() ^ 1);
        mTasksView.setProgressBarVisible();
        mTasksView.setScreenNotTouchable();
        updateTask(task);
    }

    @Override
    public int getTasksRowsCount() {
        return mTasks.size();
    }

    @Override
    public void processRetainedState() {
        mTasksView.setTasksAdapter(mTasks);
        mTasksView.updateAppTitle(mTaskList.getTitle());
    }

    @Override
    public void unsubscribe() {
        mTasksView = null;
    }
}
