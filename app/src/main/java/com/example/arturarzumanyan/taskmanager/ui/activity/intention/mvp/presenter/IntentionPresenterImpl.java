package com.example.arturarzumanyan.taskmanager.ui.activity.intention.mvp.presenter;

import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.TaskListsRepository;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.specification.AllTaskListsSpecification;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
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
    public void setCurrentTaskList(TaskList taskList) {
        this.mCurrentTaskList = taskList;
    }

    @Override
    public void setTaskLists(List<TaskList> taskLists) {
        this.mTaskLists = taskLists;
    }

    @Override
    public TaskList getCurrentTaskList() {
        return this.mCurrentTaskList;
    }

    @Override
    public List<TaskList> getTaskLists() {
        return this.mTaskLists;
    }

    @Override
    public void processEventsDialog() {
        EventsDialog eventsDialog = EventsDialog.newInstance(null);
        eventsDialog.setEventsReadyListener(new EventsDialog.EventsReadyListener() {
            @Override
            public void onEventsReady(List<Event> events) {
                mIntentionView.onEventsReady(events);
            }
        });
        mIntentionView.showDialog(eventsDialog, EVENTS_KEY);
    }

    @Override
    public void processTasksDialog() {
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
                mIntentionView.onTaskListReady(taskList);
            }
        });
        mIntentionView.showDialog(taskListsDialog, TASK_LISTS_KEY);
    }

    @Override
    public void processTaskListUpdatingDialog(TaskList taskList) {
        TaskListsDialog taskListsDialog = TaskListsDialog.newInstance(taskList);

        taskListsDialog.setTaskListReadyListener(new TaskListsDialog.TaskListReadyListener() {
            @Override
            public void onTaskListReady(TaskList taskList) {
                mIntentionView.updateTaskListOnUi(taskList);
            }
        });
        mIntentionView.showDialog(taskListsDialog, TASK_LISTS_KEY);
    }

    @Override
    public void deleteTaskList(final TaskList taskList) {
        mTaskListsRepository.deleteTaskList(taskList, new TaskListsRepository.OnTaskListsLoadedListener() {
            @Override
            public void onSuccess(List<TaskList> taskListArrayList) {
                mIntentionView.displayPreviousTaskFragment(taskList);
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

    @Override
    public void fetchTaskListsData() {
        AllTaskListsSpecification allTaskListsSpecification = new AllTaskListsSpecification();

        TaskListsRepository.OnTaskListsLoadedListener onTaskListsLoadedListener = new TaskListsRepository.OnTaskListsLoadedListener() {
            @Override
            public void onSuccess(List<TaskList> taskLists) {
                mTaskLists = taskLists;
                mIntentionView.displayDefaultUi(taskLists.get(0).getTitle());
            }

            @Override
            public void onUpdate(List<TaskList> taskLists) {
                mIntentionView.updateTaskListsMenu(taskLists);
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
    public void unsubscribe() {
        mIntentionView = null;
    }
}
