package com.example.arturarzumanyan.taskmanager.ui.activity.intention.mvp;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.TaskListsRepository;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.specification.AllTaskListsSpecification;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.specification.TaskListFromIdSpecification;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.util.Log;
import com.example.arturarzumanyan.taskmanager.ui.util.ResourceManager;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.arturarzumanyan.taskmanager.ui.util.ResourceManager.getResourceManager;

public class IntentionPresenterImpl implements IntentionContract.IntentionPresenter {
    private static final String EVENTS_KEY = "Events";

    private TaskListsRepository mTaskListsRepository;

    private List<TaskList> mTaskLists;
    private TaskList mCurrentTaskList;
    private IntentionContract.IntentionView mIntentionView;
    private CompositeDisposable mCompositeDisposable;

    public IntentionPresenterImpl(IntentionContract.IntentionView intentionView) {
        mTaskListsRepository = new TaskListsRepository();
        this.mIntentionView = intentionView;
        mCompositeDisposable = new CompositeDisposable();
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
    public void processLogOut() {
        FirebaseWebService.getFirebaseWebServiceInstance().logOut();
        mIntentionView.displaySignInScreen();
    }

    @Override
    public void deleteTaskList(String title) {
        if (!title.equals(EVENTS_KEY)) {

            TaskList previousTaskList = mTaskLists.get(mTaskLists.indexOf(mCurrentTaskList) - 1);
            TaskListFromIdSpecification taskListFromIdSpecification = new TaskListFromIdSpecification();
            taskListFromIdSpecification.setTaskListId(previousTaskList.getTaskListId());
            if (mCurrentTaskList.getId() != 1) {
                mCompositeDisposable.add(mTaskListsRepository.deleteTaskList(mCurrentTaskList, taskListFromIdSpecification)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSuccess(taskList -> {
                            if (mTaskLists.size() != 0 && mCurrentTaskList != null) {
                                mTaskLists.remove(mCurrentTaskList);
                                mCurrentTaskList = previousTaskList;
                                mIntentionView.displayPreviousTaskFragment(mTaskLists, mCurrentTaskList);
                            }
                        })
                        .doOnError(throwable -> mIntentionView.onFail(getResourceManager().getErrorMessage(ResourceManager.State.FAILED_TO_DELETE_TASK_LIST)))
                        .subscribe());
            } else {
                mIntentionView.onFail(getResourceManager().getErrorMessage(ResourceManager.State.DEFAULT_TASK_LIST_DELETING_ERROR));
            }
        }
    }

    @Override
    public void fetchTaskListsData() {
        AllTaskListsSpecification allTaskListsSpecification = new AllTaskListsSpecification();

        mCompositeDisposable.add(mTaskListsRepository.loadTaskLists(allTaskListsSpecification)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(taskLists -> {
                    if (taskLists.size() != 0) {
                        mTaskLists = taskLists;
                        mCurrentTaskList = taskLists.get(0);
                        mIntentionView.displayDefaultUi(taskLists);
                        mIntentionView.displayDefaultTasksUi(mTaskLists.get(0));
                    }
                })
                .doOnError(throwable -> mIntentionView.onFail(getResourceManager()
                        .getErrorMessage(ResourceManager.State.FAILED_TO_LOAD_TASK_LISTS)))
                .subscribe());
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
        mCompositeDisposable.clear();
        mIntentionView = null;
    }
}
