package com.example.arturarzumanyan.taskmanager.ui.activity.intention.mvp;

import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.TaskListsRepository;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.specification.AllTaskListsSpecification;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.util.Log;

import java.util.List;

public class IntentionPresenterImpl implements IntentionContract.IntentionPresenter {
    private static final String EVENTS_KEY = "Events";

    private TaskListsRepository mTaskListsRepository;

    private List<TaskList> mTaskLists;
    private TaskList mCurrentTaskList;
    private IntentionContract.IntentionView mIntentionView;

    public IntentionPresenterImpl(IntentionContract.IntentionView intentionView) {
        mTaskListsRepository = new TaskListsRepository();
        this.mIntentionView = intentionView;
    }

    @Override
    public void attachView(IntentionContract.IntentionView intentionView) {
        this.mIntentionView = intentionView;
    }

    @Override
    public void processTaskListMenuItemClick(TaskList taskList) {
        if (taskList != null) {
            mCurrentTaskList = taskList;
            mIntentionView.updateTaskUi(taskList);
        }
    }

    @Override
    public void processAddButtonClick(String title) {
        if (title.equals(EVENTS_KEY)) {
            mIntentionView.showEventCreatingDialog();
        } else {
            if (mTaskLists.size() != 0 && mCurrentTaskList != null) {
                processTasksDialog();
            }
        }
    }

    @Override
    public void processRestoredInfo(String key) {
        if (mTaskLists.size() != 0) {
            mCurrentTaskList = mTaskLists.get(0);
            mIntentionView.displayDefaultUi(mTaskLists);
            mIntentionView.displayRestoredEventsUi();
        }
    }

    @Override
    public void processUpdatedEventsList(List<Event> events) {
        mIntentionView.onEventsReady(events);
    }

    @Override
    public void processUpdatedTasksList(List<Task> tasks) {
        mIntentionView.onTasksReady(tasks);
    }

    private void processTasksDialog() {
        for (TaskList taskList : mTaskLists) {
            if (mCurrentTaskList.getId() == taskList.getId()) {
                mIntentionView.showTaskCreatingDialog(taskList);
                break;
            }
        }
    }

    @Override
    public void processTaskListCreatingDialog() {
        mIntentionView.showTaskListCreatingDialog();
    }

    @Override
    public void processCreatedTaskList(TaskList taskList) {
        if (mTaskLists.size() != 0 && taskList != null) {
            mTaskLists.add(taskList);
            mCurrentTaskList = taskList;
            mIntentionView.onTaskListReady(taskList);
        }
    }

    @Override
    public void processTaskListUpdatingDialog(String title) {
        if (!title.equals(EVENTS_KEY)) {
            mIntentionView.showTaskListUpdatingDialog(mCurrentTaskList);
        }
    }

    @Override
    public void processUpdatedTaskList(TaskList taskList) {
        if (taskList != null) {
            mIntentionView.updateTaskListOnUi(taskList, getTaskListIndex(taskList));
        }
    }

    private int getTaskListIndex(TaskList taskList) {
        for (int i = 0; i < mTaskLists.size(); i++) {
            if (mTaskLists.get(i).getId() == taskList.getId()) {
                mTaskLists.get(i).setTitle(taskList.getTitle());
                return i;
            }
        }
        return 0;
    }

    @Override
    public void deleteTaskList(String title) {
        if (!title.equals(EVENTS_KEY)) {
            mTaskListsRepository.deleteTaskList(mCurrentTaskList, new TaskListsRepository.OnTaskListsLoadedListener() {
                @Override
                public void onSuccess(List<TaskList> taskListArrayList) {
                    if (mTaskLists.size() != 0 && mCurrentTaskList != null) {
                        processPreviousTaskList(mCurrentTaskList);
                        mIntentionView.displayPreviousTaskFragment(mTaskLists, mCurrentTaskList);
                    }
                }

                @Override
                public void onUpdate(List<TaskList> taskLists) {

                }

                @Override
                public void onSuccess(TaskList taskList) {

                }

                @Override
                public void onFail(String message) {
                    mIntentionView.onFail(message);
                }

                @Override
                public void onPermissionDenied() {
                    /** To-do: add realization with start signInActivity*/
                }
            });
        }
    }

    private void processPreviousTaskList(TaskList taskList) {
        int menuSize = mTaskLists.size();
        TaskList previousTaskList = null;
        for (int i = 0; i < menuSize; i++) {
            if (mTaskLists.get(i).getId() == taskList.getId()) {
                mTaskLists.remove(i);
                previousTaskList = mTaskLists.get(i - 1);
                break;
            }
        }
        mCurrentTaskList = previousTaskList;
    }

    @Override
    public void fetchTaskListsData() {
        AllTaskListsSpecification allTaskListsSpecification = new AllTaskListsSpecification();

        TaskListsRepository.OnTaskListsLoadedListener onTaskListsLoadedListener = new TaskListsRepository.OnTaskListsLoadedListener() {
            @Override
            public void onSuccess(List<TaskList> taskLists) {
                if (taskLists.size() != 0) {
                    mTaskLists = taskLists;
                    mCurrentTaskList = taskLists.get(0);
                    mIntentionView.displayDefaultUi(taskLists);
                    mIntentionView.displayDefaultTasksUi(mTaskLists.get(0));
                }
            }

            @Override
            public void onUpdate(List<TaskList> taskLists) {
                if (taskLists.size() != 0) {
                    mTaskLists = taskLists;
                    mIntentionView.recreateTaskListsMenu(taskLists);
                }
            }

            @Override
            public void onSuccess(TaskList taskList) {

            }

            @Override
            public void onFail(String message) {
                mIntentionView.onFail(message);
            }

            @Override
            public void onPermissionDenied() {
                /** To-do: add realization with start signInActivity*/
            }
        };

        mTaskListsRepository.loadTaskLists(allTaskListsSpecification, onTaskListsLoadedListener);
    }

    @Override
    public void processActionBarMenuItems(String title) {
        if (!title.equals(EVENTS_KEY)) {
            Log.v("TaskLists key");
            mIntentionView.setActionBarMenuItemsVisibility(true);
        } else {
            Log.v("Events key");
            mIntentionView.setActionBarMenuItemsVisibility(false);
        }
    }

    @Override
    public void unsubscribe() {
        mIntentionView = null;
    }
}
