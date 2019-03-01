package com.example.arturarzumanyan.taskmanager.ui.dialog.task.mvp;

import android.os.Bundle;

import com.example.arturarzumanyan.taskmanager.domain.Task;

import java.util.Date;
import java.util.List;

public class TasksDialogContract {
    public interface TasksDialogPresenter {
        void processOkButtonClick(Bundle bundle, String name, String description, String date);

        void setTaskDate(Date date);

        void processReceivedBundle(Bundle bundle);
    }

    public interface TasksDialogView {
        void setTaskInfoViews(Task task);

        void onTaskReady(List<Task> tasks);

        void onFail(String message);
    }
}
