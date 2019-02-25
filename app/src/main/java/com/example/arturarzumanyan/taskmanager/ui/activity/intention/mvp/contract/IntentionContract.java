package com.example.arturarzumanyan.taskmanager.ui.activity.intention.mvp.contract;

import android.support.v4.app.DialogFragment;

import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;

import java.util.List;

public class IntentionContract {
    public interface IntentionPresenter {
        void attachView(IntentionView intentionView);

        void processTaskListMenuItemClick(TaskList taskList);

        void processAddButtonClick(String title);

        void processRestoredInfo(String key);

        void processTaskListCreatingDialog();

        void processTaskListUpdatingDialog(String title);

        void deleteTaskList(String title);

        void fetchTaskListsData();

        void processActionBarMenuItems(String title);

        void unsubscribe();
    }

    public interface IntentionView {
        void showDialog(DialogFragment dialogFragment, String key);

        void onTasksReady(List<Task> tasks);

        void onEventsReady(List<Event> events);

        void onTaskListReady(TaskList taskList);

        void displayDefaultUi(List<TaskList> taskLists);

        void displayDefaultTasksUi(List<TaskList> taskLists);

        void displayRestoredEventsUi();

        void displayPreviousTaskFragment(List<TaskList> taskLists, TaskList taskList);

        void recreateTaskListsMenu(List<TaskList> taskLists);

        void updateTaskListOnUi(TaskList taskList, int index);

        void setActionBarMenuItemsVisibility(boolean visibility);

        void updateTaskUi(TaskList taskList);

        void onFail(String message);
    }
}
