package com.example.arturarzumanyan.taskmanager.ui.activity.intention.mvp.presenter;

import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.TaskListsRepository;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.specification.AllTaskListsSpecification;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.util.Log;
import com.example.arturarzumanyan.taskmanager.ui.activity.intention.mvp.contract.IntentionContract;
import com.example.arturarzumanyan.taskmanager.ui.dialog.EventsDialog;
import com.example.arturarzumanyan.taskmanager.ui.dialog.TaskListsDialog;
import com.example.arturarzumanyan.taskmanager.ui.dialog.TasksDialog;

import java.util.List;

public class IntentionPresenterImpl implements IntentionContract.IntentionPresenter {
    private static final String EVENTS_KEY = "Events";
    private static final String TASKS_KEY = "Tasks";
    private static final String TASK_LISTS_KEY = "TaskLists";

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
        mCurrentTaskList = taskList;
        mIntentionView.updateTaskUi(taskList);
    }

    @Override
    public void processAddButtonClick(String currentTitle) {
        if (currentTitle.equals(EVENTS_KEY)) {
            processEventsDialog();
        } else {
            processTasksDialog();
        }
    }

    @Override
    public void processRestoredInfo(String key) {
        mCurrentTaskList = mTaskLists.get(0);
        mIntentionView.displayDefaultUi(mTaskLists);
        mIntentionView.displayRestoredEventsUi();
    }

    private void processEventsDialog() {
        EventsDialog eventsDialog = EventsDialog.newInstance(null);
        eventsDialog.setEventsReadyListener(new EventsDialog.EventsReadyListener() {
            @Override
            public void onEventsReady(List<Event> events) {
                mIntentionView.onEventsReady(events);
            }
        });
        mIntentionView.showDialog(eventsDialog, EVENTS_KEY);
    }

    private void processTasksDialog() {
        for (TaskList taskList : mTaskLists) {
            if (mCurrentTaskList.getId() == taskList.getId()) {
                TasksDialog tasksDialog = TasksDialog.newInstance(null, taskList);
                tasksDialog.setTasksReadyListener(new TasksDialog.TasksReadyListener() {
                    @Override
                    public void onTasksReady(List<Task> tasks) {
                        mIntentionView.onTasksReady(tasks);
                    }
                });
                mIntentionView.showDialog(tasksDialog, TASKS_KEY);
                break;
            }
        }
    }

    @Override
    public void processTaskListCreatingDialog() {
        TaskListsDialog taskListsDialog = TaskListsDialog.newInstance(null);
        taskListsDialog.setTaskListReadyListener(new TaskListsDialog.TaskListReadyListener() {
            @Override
            public void onTaskListReady(final TaskList taskList) {
                mTaskLists.add(taskList);
                mCurrentTaskList = taskList;
                mIntentionView.onTaskListReady(taskList);
            }
        });
        mIntentionView.showDialog(taskListsDialog, TASK_LISTS_KEY);
    }

    @Override
    public void processTaskListUpdatingDialog(String title) {
        if (!title.equals(EVENTS_KEY)) {
            TaskListsDialog taskListsDialog = TaskListsDialog.newInstance(mCurrentTaskList);
            taskListsDialog.setTaskListReadyListener(new TaskListsDialog.TaskListReadyListener() {
                @Override
                public void onTaskListReady(TaskList taskList) {
                    mIntentionView.updateTaskListOnUi(taskList, getTaskListIndex(taskList));
                }
            });
            mIntentionView.showDialog(taskListsDialog, TASK_LISTS_KEY);
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
                    processPreviousTaskList(mCurrentTaskList);
                    mIntentionView.displayPreviousTaskFragment(mTaskLists, mCurrentTaskList);
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
                mTaskLists = taskLists;
                mCurrentTaskList = taskLists.get(0);
                mIntentionView.displayDefaultUi(taskLists);
                mIntentionView.displayDefaultTasksUi(mTaskLists);
            }

            @Override
            public void onUpdate(List<TaskList> taskLists) {
                mTaskLists = taskLists;
                mIntentionView.recreateTaskListsMenu(taskLists);
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
