package com.example.arturarzumanyan.taskmanager.ui.fragment.task.mvp;

import android.os.Bundle;

import com.example.arturarzumanyan.taskmanager.data.repository.tasks.TasksRepository;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.util.Log;
import com.example.arturarzumanyan.taskmanager.ui.adapter.task.mvp.TaskRowView;
import com.example.arturarzumanyan.taskmanager.ui.util.ResourceManager;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.arturarzumanyan.taskmanager.ui.activity.intention.IntentionActivity.TASK_LISTS_KEY;
import static com.example.arturarzumanyan.taskmanager.ui.util.ResourceManager.getResourceManager;

public class TasksPresenterImpl implements TasksContract.TasksPresenter {
    private TasksContract.TasksView mTasksView;
    private TasksRepository mTasksRepository;
    private TaskList mTaskList;
    private List<Task> mTasks;
    private CompositeDisposable mCompositeDisposable;

    public TasksPresenterImpl(TasksContract.TasksView mTasksView) {
        this.mTasksView = mTasksView;
        this.mTasksRepository = new TasksRepository();
        this.mCompositeDisposable = new CompositeDisposable();
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
        mCompositeDisposable.add(mTasksRepository.loadTasks(taskList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(tasks -> {
                    if (mTasksView != null) {
                        mTasks = tasks;
                        mTasksView.setProgressBarInvisible();
                        mTasksView.setTasksAdapter(tasks);
                    }
                })
                .doOnError(throwable -> {
                    if (mTasksView != null) {
                        mTasksView.setProgressBarInvisible();
                        mTasksView.setScreenNotTouchable();
                        mTasksView.onFail(getResourceManager().getErrorMessage(ResourceManager.State.FAILED_TO_LOAD_TASKS));
                    }
                })
                .subscribe());
        mTasksView.updateAppTitle(taskList.getTitle());
    }

    @Override
    public void processReceivedBundle(Bundle bundle) {
        if (bundle != null) {
            mTaskList = bundle.getParcelable(TASK_LISTS_KEY);
        }
    }

    private void updateTask(Task task) {
        mCompositeDisposable.add(mTasksRepository.updateTask(mTaskList, task)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(tasks -> {
                    if (mTasksView != null) {
                        mTasks = tasks;
                        mTasksView.setProgressBarInvisible();
                        mTasksView.setScreenNotTouchable();
                        mTasksView.updateTasksAdapter();
                    }
                })
                .doOnError(throwable -> {
                    if (mTasksView != null) {
                        mTasksView.setProgressBarInvisible();
                        mTasksView.setScreenNotTouchable();
                        mTasksView.onFail(getResourceManager().getErrorMessage(ResourceManager.State.FAILED_TO_UPDATE_TASK));
                    }
                })
                .subscribe());
    }

    @Override
    public void processItemDelete(final int position) {
        Task task = mTasks.get(position);
        mCompositeDisposable.add(mTasksRepository.deleteTask(mTaskList, task)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(tasks -> {
                    if (mTasksView != null) {
                        mTasks = tasks;
                        mTasksView.updateTasksAdapterAfterDelete(position);
                    }
                })
                .doOnError(throwable -> {
                    if (mTasksView != null) {
                        mTasksView.onFail(getResourceManager().getErrorMessage(ResourceManager.State.FAILED_TO_DELETE_TASK));
                    }
                })
                .subscribe());
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
        mCompositeDisposable.clear();
        mTasksView = null;
    }
}
