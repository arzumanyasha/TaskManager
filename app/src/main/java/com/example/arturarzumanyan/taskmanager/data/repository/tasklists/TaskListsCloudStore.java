package com.example.arturarzumanyan.taskmanager.data.repository.tasklists;

import android.content.Context;
import android.os.AsyncTask;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.data.repository.BaseDataLoadingAsyncTask;
import com.example.arturarzumanyan.taskmanager.networking.util.TaskListsParser;

import java.util.List;

public class TaskListsCloudStore {
    private static final String BASE_TASK_LISTS_URL = "https://www.googleapis.com/tasks/v1/users/@me/lists/";

    private TaskListsDbStore mTaskListsDbStore;
    private BaseDataLoadingAsyncTask mUserTaskListsAsyncTask;
    private RepositoryLoadHelper mRepositoryLoadHelper;
    private Context mContext;

    public TaskListsCloudStore(Context context) {
        this.mContext = context;
        mTaskListsDbStore = new TaskListsDbStore(mContext);
        mRepositoryLoadHelper = new RepositoryLoadHelper(mContext);
        //mUserTaskListsAsyncTask = new BaseDataLoadingAsyncTask();
    }

    public void getTaskLists(final OnTaskCompletedListener listener) {
        RepositoryLoadHelper repositoryLoadHelper = new RepositoryLoadHelper(mContext);
        //repositoryLoadHelper.requestUserData(mUserTaskListsAsyncTask, BASE_TASK_LISTS_URL);
        /*

        mUserTaskListsAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) {
                TaskListsParser taskListsParser = new TaskListsParser();
                listener.onSuccess(taskListsParser.parseTaskLists(response));
            }

            @Override
            public void onFail() {
                listener.onSuccess(mTaskListsDbStore.getTaskLists());
            }
        });*/
    }

    public void addTaskList(final TaskList taskList, OnTaskCompletedListener listener) {
        final String url = BASE_TASK_LISTS_URL;

        sendRequest(taskList, url, FirebaseWebService.RequestMethods.POST, listener);
    }

    public void updateTaskList(TaskList taskList, OnTaskCompletedListener listener) {
        final String url = BASE_TASK_LISTS_URL + taskList.getTaskListId();

        sendRequest(taskList, url, FirebaseWebService.RequestMethods.PATCH, listener);
    }

    private void sendRequest(final TaskList taskList,
                             final String url,
                             final FirebaseWebService.RequestMethods requestMethod,
                             final OnTaskCompletedListener listener) {
/*
        BaseDataLoadingAsyncTask baseDataLoadingAsyncTask = new BaseDataLoadingAsyncTask();
        baseDataLoadingAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
                mRepositoryLoadHelper.getTaskListCreateOrUpdateParameters(taskList, url, requestMethod));

        baseDataLoadingAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) {
                createOrUpdateTaskListInDb(response, requestMethod);
                listener.onSuccess(mTaskListsDbStore.getTaskList(taskList.getTitle()));
            }

            @Override
            public void onFail() {
                FirebaseWebService firebaseWebService = new FirebaseWebService();
                firebaseWebService.refreshAccessToken(mContext, new FirebaseWebService.AccessTokenUpdatedListener() {
                    @Override
                    public void onAccessTokenUpdated() {
                        BaseDataLoadingAsyncTask updatedBaseDataLoadingAsyncTask = new BaseDataLoadingAsyncTask();
                        updatedBaseDataLoadingAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener() {
                            @Override
                            public void onDataLoaded(String response) {
                                createOrUpdateTaskListInDb(response, requestMethod);
                                listener.onSuccess(mTaskListsDbStore.getTaskList(taskList.getTitle()));
                            }

                            @Override
                            public void onFail() {
                                if (requestMethod == FirebaseWebService.RequestMethods.POST) {
                                    mTaskListsDbStore.addTaskList(taskList);
                                    listener.onSuccess(taskList);
                                } else if (requestMethod == FirebaseWebService.RequestMethods.PATCH) {
                                    mTaskListsDbStore.updateTaskList(taskList);
                                    listener.onSuccess(taskList);
                                }
                            }
                        });
                        updatedBaseDataLoadingAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
                                mRepositoryLoadHelper.getTaskListCreateOrUpdateParameters(taskList, url, requestMethod));

                    }
                });
            }
        });*/
    }

    private void createOrUpdateTaskListInDb(String response,
                                            FirebaseWebService.RequestMethods requestMethod) {
        TaskListsParser taskListsParser = new TaskListsParser();
        if (requestMethod == FirebaseWebService.RequestMethods.POST) {
            mTaskListsDbStore.addTaskList(taskListsParser.parseTaskList(response));
        } else if (requestMethod == FirebaseWebService.RequestMethods.PATCH) {
            mTaskListsDbStore.updateTaskList(taskListsParser.parseTaskList(response));
        }
    }

    public void deleteTaskList(final TaskList taskList, final OnTaskCompletedListener listener) {
        final String url = BASE_TASK_LISTS_URL + taskList.getTaskListId();
/*
        BaseDataLoadingAsyncTask baseDataLoadingAsyncTask = new BaseDataLoadingAsyncTask();

        baseDataLoadingAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
                mRepositoryLoadHelper.getDeleteParameters(url));

        baseDataLoadingAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) {
                mTaskListsDbStore.deleteTaskList(taskList);
                listener.onSuccess(taskList);
            }

            @Override
            public void onFail() {
                FirebaseWebService firebaseWebService = new FirebaseWebService();
                firebaseWebService.refreshAccessToken(mContext, new FirebaseWebService.AccessTokenUpdatedListener() {
                    @Override
                    public void onAccessTokenUpdated() {
                        BaseDataLoadingAsyncTask updatedBaseDataLoadingAsyncTask = new BaseDataLoadingAsyncTask();

                        updatedBaseDataLoadingAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener() {
                            @Override
                            public void onDataLoaded(String response) {
                                mTaskListsDbStore.deleteTaskList(taskList);
                                listener.onSuccess(taskList);
                            }

                            @Override
                            public void onFail() {
                                mTaskListsDbStore.deleteTaskList(taskList);
                            }
                        });

                        updatedBaseDataLoadingAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
                                mRepositoryLoadHelper.getDeleteParameters(url));
                    }
                });
            }
        });*/
    }

    public interface OnTaskCompletedListener {
        void onSuccess(TaskList taskList);

        void onSuccess(List<TaskList> taskListArrayList);

        void onFail();
    }
}
