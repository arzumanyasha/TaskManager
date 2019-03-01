package com.example.arturarzumanyan.taskmanager.ui.dialog.tasklist.mvp;

import android.os.Bundle;

import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.TaskListsRepository;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;

import java.util.List;
import java.util.UUID;

import static com.example.arturarzumanyan.taskmanager.ui.activity.intention.IntentionActivity.TASK_LISTS_KEY;

public class TaskListsDialogPresenterImpl implements TaskListsDialogContract.TaskListsDialogPresenter{
    private TaskListsDialogContract.TaskListsDialogView mTaskListsDialogView;
    private TaskListsRepository mTaskListsRepository;

    public TaskListsDialogPresenterImpl(TaskListsDialogContract.TaskListsDialogView mTaskListsDialogView) {
        this.mTaskListsDialogView = mTaskListsDialogView;
        mTaskListsRepository = new TaskListsRepository();
    }

    @Override
    public void processOkButtonClick(Bundle bundle, String taskListName) {
        TaskListsRepository.OnTaskListsLoadedListener onTaskListsLoadedListener = getTaskListLoadedListener();
        if (!taskListName.isEmpty()) {
            if (bundle != null) {
                TaskList taskList = bundle.getParcelable(TASK_LISTS_KEY);
                if (taskList != null) {
                    updateTaskList(taskList, taskListName, onTaskListsLoadedListener);
                }
            } else {
                addTaskList(taskListName, onTaskListsLoadedListener);
            }
        }
    }

    private void updateTaskList(TaskList taskList, String taskListName,
                                TaskListsRepository.OnTaskListsLoadedListener onTaskListsLoadedListener) {
        taskList.setTitle(taskListName);
        mTaskListsRepository.updateTaskList(taskList, onTaskListsLoadedListener);
    }

    private void addTaskList(String taskListName, TaskListsRepository.OnTaskListsLoadedListener onTaskListsLoadedListener) {
        TaskList taskList = new TaskList(UUID.randomUUID().toString(),
                taskListName);
        mTaskListsRepository.addTaskList(taskList, onTaskListsLoadedListener);
    }

    private TaskListsRepository.OnTaskListsLoadedListener getTaskListLoadedListener() {
        return new TaskListsRepository.OnTaskListsLoadedListener() {
            @Override
            public void onSuccess(List<TaskList> taskListArrayList) {

            }

            @Override
            public void onUpdate(List<TaskList> taskLists) {

            }

            @Override
            public void onSuccess(TaskList taskList) {
                mTaskListsDialogView.onTaskListReady(taskList);
            }

            @Override
            public void onFail(String message) {
                mTaskListsDialogView.onFail(message);
            }

            @Override
            public void onPermissionDenied() {
                /** To-do: add realization with start signInActivity*/
            }
        };
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
