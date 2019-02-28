package com.example.arturarzumanyan.taskmanager.ui.dialog.task.mvp;

import android.os.Bundle;

import com.example.arturarzumanyan.taskmanager.data.repository.tasks.TasksRepository;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.PATCH;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.POST;
import static com.example.arturarzumanyan.taskmanager.ui.activity.intention.IntentionActivity.TASKS_KEY;
import static com.example.arturarzumanyan.taskmanager.ui.activity.intention.IntentionActivity.TASK_LISTS_KEY;

public class TasksDialogPresenterImpl implements TasksDialogContract.TasksDialogPresenter {
    private TasksDialogContract.TasksDialogView mTasksDialogView;
    private TasksRepository mTasksRepository;
    private Date mTaskDate;

    public TasksDialogPresenterImpl(TasksDialogContract.TasksDialogView mTasksDialogView) {
        this.mTasksDialogView = mTasksDialogView;
        mTasksRepository = new TasksRepository();
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
                    task.setDate(mTaskDate);

                    updateTask(task, taskList);
                }
            } else {
                String taskId = UUID.randomUUID().toString();
                int isExecuted = 0;
                TaskList taskList = bundle.getParcelable(TASK_LISTS_KEY);
                if (taskList != null) {
                    int taskListId = taskList.getId();
                    Date taskDate = null;

                    if (date != null) {
                        taskDate = DateUtils.getTaskDateFromString(DateUtils.formatReversedYearMonthDayDate(date));
                    }

                    Task task = createTaskObject(taskId, taskName, description, isExecuted, taskListId, taskDate);

                    addTask(task, taskList);
                }

            }
        }
    }

    private void addTask(Task task, TaskList taskList) {
        mTasksRepository.addOrUpdateTask(taskList, task, POST, new TasksRepository.OnTasksLoadedListener() {
            @Override
            public void onSuccess(List<Task> taskArrayList) {
                mTasksDialogView.onTaskReady(taskArrayList);
            }

            @Override
            public void onFail(String message) {
                mTasksDialogView.onFail(message);
            }

            @Override
            public void onPermissionDenied() {

            }
        });
    }

    private void updateTask(Task task, TaskList taskList) {
        mTasksRepository.addOrUpdateTask(taskList, task, PATCH, new TasksRepository.OnTasksLoadedListener() {
            @Override
            public void onSuccess(List<Task> taskArrayList) {
                mTasksDialogView.onTaskReady(taskArrayList);
            }

            @Override
            public void onFail(String message) {
                mTasksDialogView.onFail(message);
            }

            @Override
            public void onPermissionDenied() {
                /** To-do: add realization with start signInActivity*/
            }
        });
    }

    @Override
    public void setTaskDate(Date date) {
        this.mTaskDate = date;
    }

    private Task createTaskObject(String id, String name, String description, int isExecuted, int taskListId, Date date) {
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
