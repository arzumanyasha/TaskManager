package com.example.arturarzumanyan.taskmanager.ui.activity.intention.mvp;

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

        void processUpdatedEventsList(List<Event> events);

        void processUpdatedTasksList(List<Task> tasks);

        void processCreatedTaskList(TaskList taskList);

        void processUpdatedTaskList(TaskList taskList);

        void processTaskListCreatingDialog();

        void processTaskListUpdatingDialog(String title);

        void deleteTaskList(String title);

        void fetchTaskListsData();

        void processActionBarMenuItems(String title);

        void processLogOut();

        void unsubscribe();
    }

    public interface IntentionView {
        void showEventCreatingDialog();

        void showTaskCreatingDialog(TaskList taskList);

        void showTaskListCreatingDialog();

        void showTaskListUpdatingDialog(TaskList taskList);

        void onTasksReady(List<Task> tasks);

        void onEventsReady(List<Event> events);

        void onTaskListReady(TaskList taskList);

        void displayDefaultUi(List<TaskList> taskLists);

        void displayDefaultTasksUi(TaskList taskList);

        void displayRestoredEventsUi();

        void displayPreviousTaskFragment(List<TaskList> taskLists, TaskList taskList);

        void displaySignInScreen();

        void recreateTaskListsMenu(List<TaskList> taskLists);

        void updateTaskListOnUi(TaskList taskList, int index);

        void setActionBarMenuItemsVisibility(boolean visibility);

        void updateTaskUi(TaskList taskList);

        void onFail(String message);
    }
}
