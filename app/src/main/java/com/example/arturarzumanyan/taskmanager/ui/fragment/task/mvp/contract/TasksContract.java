package com.example.arturarzumanyan.taskmanager.ui.fragment.task.mvp.contract;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;

import java.util.List;

public class TasksContract {
    public interface TasksPresenter {
        void attachView(TasksView tasksView);

        void processTasks();

        void loadTasks(TaskList taskList);

        void processReceivedBundle(Bundle bundle);

        void deleteTask(Task task);

        void processItemExecutedStatus(Task task);

        void processTaskDialog(Task task);

        void processRetainedState();

        void unsubscribe();
    }

    public interface TasksView {
        void setTasksAdapter(List<Task> tasks);

        void showDialog(DialogFragment dialogFragment);

        void updateTasksAdapter(List<Task> tasks);

        void updateAppTitle(String title);

        void setProgressBarVisible();

        void setProgressBarInvisible();

        void setScreenNotTouchable();

        void onFail(String message);
    }
}
