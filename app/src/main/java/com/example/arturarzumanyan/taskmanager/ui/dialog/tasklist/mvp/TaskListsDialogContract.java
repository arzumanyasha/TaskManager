package com.example.arturarzumanyan.taskmanager.ui.dialog.tasklist.mvp;

import android.os.Bundle;

import com.example.arturarzumanyan.taskmanager.domain.TaskList;

public class TaskListsDialogContract {
    public interface TaskListsDialogPresenter {
        void processOkButtonClick(Bundle bundle, String taskListName);

        void processReceivedBundle(Bundle bundle);
    }

    public interface TaskListsDialogView {
        void setTaskListInfoViews(TaskList taskList);

        void onTaskListReady(TaskList taskLists);

        void onFail(String message);
    }
}
