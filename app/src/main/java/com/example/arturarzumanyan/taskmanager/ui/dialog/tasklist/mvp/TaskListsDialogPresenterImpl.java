package com.example.arturarzumanyan.taskmanager.ui.dialog.tasklist.mvp;

import android.os.Bundle;

import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.TaskListsRepository;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.ui.util.ResourceManager;

import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.arturarzumanyan.taskmanager.ui.activity.intention.IntentionActivity.TASK_LISTS_KEY;
import static com.example.arturarzumanyan.taskmanager.ui.util.ResourceManager.getResourceManager;

public class TaskListsDialogPresenterImpl implements TaskListsDialogContract.TaskListsDialogPresenter {
    private TaskListsDialogContract.TaskListsDialogView mTaskListsDialogView;
    private TaskListsRepository mTaskListsRepository;
    private CompositeDisposable mCompositeDisposable;

    public TaskListsDialogPresenterImpl(TaskListsDialogContract.TaskListsDialogView mTaskListsDialogView) {
        this.mTaskListsDialogView = mTaskListsDialogView;
        mTaskListsRepository = new TaskListsRepository();
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void processOkButtonClick(Bundle bundle, String taskListName) {
        if (!taskListName.isEmpty()) {
            if (bundle != null) {
                TaskList taskList = bundle.getParcelable(TASK_LISTS_KEY);
                if (taskList != null) {
                    updateTaskList(taskList, taskListName);
                }
            } else {
                addTaskList(taskListName);
            }
        }
    }

    private void updateTaskList(TaskList taskList, String taskListName) {
        taskList.setTitle(taskListName);
        mCompositeDisposable.add(mTaskListsRepository.updateTaskList(taskList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(parsedTaskList -> mTaskListsDialogView.onTaskListReady(parsedTaskList))
                .doOnError(throwable -> mTaskListsDialogView.onFail(getResourceManager()
                        .getErrorMessage(ResourceManager.State.FAILED_TO_UPDATE_TASK_LIST)))
                .subscribe());
    }

    private void addTaskList(String taskListName) {
        TaskList taskList = new TaskList(UUID.randomUUID().toString(), taskListName);
        mCompositeDisposable.add(mTaskListsRepository.addTaskList(taskList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(parsedTaskList -> mTaskListsDialogView.onTaskListReady(parsedTaskList))
                .doOnError(throwable -> mTaskListsDialogView.onFail(getResourceManager()
                        .getErrorMessage(ResourceManager.State.FAILED_TO_CREATE_TASK_LIST)))
                .subscribe());
    }

    @Override
    public void processReceivedBundle(Bundle bundle) {
        if (bundle != null) {
            TaskList taskList = bundle.getParcelable(TASK_LISTS_KEY);
            if (taskList != null) {
                mTaskListsDialogView.setTaskListInfoViews(taskList);
            }
        }
    }
}
