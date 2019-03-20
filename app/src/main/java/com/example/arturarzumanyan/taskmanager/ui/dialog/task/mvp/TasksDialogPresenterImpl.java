package com.example.arturarzumanyan.taskmanager.ui.dialog.task.mvp;

import android.os.Bundle;

import com.example.arturarzumanyan.taskmanager.data.repository.tasks.TasksRepository;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;
import com.example.arturarzumanyan.taskmanager.ui.util.ResourceManager;

import java.util.Date;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.arturarzumanyan.taskmanager.ui.activity.intention.IntentionActivity.TASKS_KEY;
import static com.example.arturarzumanyan.taskmanager.ui.activity.intention.IntentionActivity.TASK_LISTS_KEY;
import static com.example.arturarzumanyan.taskmanager.ui.util.ResourceManager.getResourceManager;

public class TasksDialogPresenterImpl implements TasksDialogContract.TasksDialogPresenter {
    private TasksDialogContract.TasksDialogView mTasksDialogView;
    private TasksRepository mTasksRepository;
    private CompositeDisposable mCompositeDisposable;
    private Date mTaskDate;

    public TasksDialogPresenterImpl(TasksDialogContract.TasksDialogView mTasksDialogView) {
        this.mTasksDialogView = mTasksDialogView;
        mTasksRepository = new TasksRepository();
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void processOkButtonClick(Bundle bundle, String taskName, String description, String date) {
        if (bundle != null && !taskName.isEmpty()) {
            if (bundle.getParcelable(TASKS_KEY) != null) {
                Task task = bundle.getParcelable(TASKS_KEY);
                TaskList taskList = bundle.getParcelable(TASK_LISTS_KEY);
                if (task != null) {
                    task.setName(taskName);
                    task.setDescription(description);
                    if (mTaskDate != null) {
                        task.setDate(DateUtils.formatTaskDate(mTaskDate));
                    }
                    updateTask(task, taskList);
                }
            } else {
                String taskId = UUID.randomUUID().toString();
                int isExecuted = 0;
                TaskList taskList = bundle.getParcelable(TASK_LISTS_KEY);
                if (taskList != null) {
                    int taskListId = taskList.getId();
                    String taskDate = null;

                    if (date != null) {
                        taskDate = DateUtils.formatTaskDate(DateUtils.getTaskDateFromString(
                                DateUtils.formatReversedYearMonthDayDate(date)));
                    }

                    Task task = createTaskObject(taskId, taskName, description, isExecuted, taskListId, taskDate);

                    addTask(task, taskList);
                }

            }
        }
    }

    private void addTask(Task task, TaskList taskList) {
        mCompositeDisposable.add(mTasksRepository.addTask(taskList, task)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(tasks -> mTasksDialogView.onTaskReady(tasks))
                .doOnError(throwable -> mTasksDialogView.onFail(getResourceManager().getErrorMessage(ResourceManager.State.FAILED_TO_CREATE_TASK)))
                .subscribe());
    }

    private void updateTask(Task task, TaskList taskList) {
        mCompositeDisposable.add(mTasksRepository.updateTask(taskList, task)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(tasks -> mTasksDialogView.onTaskReady(tasks))
                .doOnError(throwable -> mTasksDialogView.onFail(getResourceManager().getErrorMessage(ResourceManager.State.FAILED_TO_UPDATE_TASK)))
                .subscribe());
    }

    @Override
    public void setTaskDate(Date date) {
        this.mTaskDate = date;
    }

    private Task createTaskObject(String id, String name, String description, int isExecuted, int taskListId, String date) {
        return new Task(id, name, description, isExecuted, taskListId, date);
    }

    @Override
    public void processReceivedBundle(Bundle bundle) {
        if (bundle != null) {
            Task task = bundle.getParcelable(TASKS_KEY);
            if (task != null) {
                mTasksDialogView.setTaskInfoViews(task);
            }
        }
    }
}
