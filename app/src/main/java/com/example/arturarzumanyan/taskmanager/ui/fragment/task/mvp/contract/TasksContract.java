package com.example.arturarzumanyan.taskmanager.ui.fragment.task.mvp.contract;

import android.os.Bundle;

import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.ui.adapter.task.mvp.TaskRowView;

import java.util.List;

public class TasksContract {
    public interface TasksPresenter {
        void attachView(TasksView tasksView);

        void processTasks();

        void loadTasks(TaskList taskList);

        void processReceivedBundle(Bundle bundle);

        void onBindEventsRowViewAtPosition(int position, TaskRowView rowView);

        void updateTasksList(List<Task> updatedList);

        void processItemClick(int position);

        void processTaskStatusChanging(int position);

        void processItemDelete(int position);

        int getTasksRowsCount();

        void processRetainedState();

        void unsubscribe();
    }

    public interface TasksView {
        void setTasksAdapter(List<Task> tasks);

        void showTaskUpdatingDialog(Task task,TaskList taskList);

        void updateTasksAdapter();

        void updateTasksAdapterAfterDelete(int position);

        void updateAppTitle(String title);

        void setProgressBarVisible();

        void setProgressBarInvisible();

        void setScreenNotTouchable();

        void onFail(String message);
    }
}
