package com.example.arturarzumanyan.taskmanager.ui.activity.intention.mvp.contract;

import android.support.v4.app.DialogFragment;

import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;

import java.util.List;

public class IntentionContract {
    public interface IntentionPresenter {
        void setCurrentTaskList(TaskList taskList);

        void setTaskLists(List<TaskList> taskLists);

        TaskList getCurrentTaskList();

        List<TaskList> getTaskLists();

        void processEventsDialog();

        void processTasksDialog();

        void processTaskListCreatingDialog();

        void processTaskListUpdatingDialog(TaskList taskList);

        void deleteTaskList(TaskList taskList);

        void fetchTaskListsData();

        void unsubscribe();
    }

    public interface IntentionView {
        void showDialog(DialogFragment dialogFragment, String key);

        void onTasksReady(List<Task> tasks);

        void onEventsReady(List<Event> events);

        void onTaskListReady(TaskList taskList);

        void displayDefaultUi(String title);

        void displayPreviousTaskFragment(TaskList taskList);

        void updateTaskListsMenu(List<TaskList> taskLists);

        void updateTaskListOnUi(TaskList taskList);

        void onFail(String message);
    }
}
