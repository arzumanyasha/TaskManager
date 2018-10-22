package com.example.arturarzumanyan.taskmanager.data.repository.tasklists;

import android.content.Context;
import android.os.AsyncTask;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.UserDataAsyncTask;
import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;
import com.example.arturarzumanyan.taskmanager.networking.util.TaskListsParser;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskListsCloudStore {
    private static final String BASE_TASK_LISTS_URL = "https://www.googleapis.com/tasks/v1/users/@me/lists/";

    private TaskListsDbStore mTaskListsDbStore;
    private UserDataAsyncTask mUserTaskListsAsyncTask;
    private RepositoryLoadHelper mRepositoryLoadHelper;
    private Context mContext;

    public TaskListsCloudStore(Context context) {
        this.mContext = context;
        mTaskListsDbStore = new TaskListsDbStore(mContext);
        mRepositoryLoadHelper = new RepositoryLoadHelper(mContext);
        mUserTaskListsAsyncTask = new UserDataAsyncTask();
    }

    public void getTaskLists(final OnTaskCompletedListener listener) {
        RepositoryLoadHelper repositoryLoadHelper = new RepositoryLoadHelper(mContext);
        repositoryLoadHelper.requestUserData(mUserTaskListsAsyncTask, BASE_TASK_LISTS_URL);
        mUserTaskListsAsyncTask.setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) {
                if (!response.equals("")) {
                    TaskListsParser taskListsParser = new TaskListsParser();
                    listener.onSuccess(taskListsParser.parseTaskLists(response));
                }
            }
        });
    }

    public void addTaskList(final TaskList taskList) {
        final String url = BASE_TASK_LISTS_URL;

        sendRequest(taskList, url, FirebaseWebService.RequestMethods.POST);
    }

    public void updateTaskList(TaskList taskList) {
        final String url = BASE_TASK_LISTS_URL + taskList.getTaskListId();

        sendRequest(taskList, url, FirebaseWebService.RequestMethods.PATCH);
    }

    private void sendRequest(final TaskList taskList,
                             final String url,
                             final FirebaseWebService.RequestMethods requestMethod) {

        UserDataAsyncTask userDataAsyncTask = new UserDataAsyncTask();
        userDataAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
                mRepositoryLoadHelper.getTaskListCreateOrUpdateParameters(taskList, url, requestMethod));

        userDataAsyncTask.setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) {
                if (!response.equals("")) {
                    createOrUpdateTaskListInDb(response, requestMethod);
                } else {
                    FirebaseWebService firebaseWebService = new FirebaseWebService();
                    firebaseWebService.refreshAccessToken(mContext, new FirebaseWebService.AccessTokenUpdatedListener() {
                        @Override
                        public void onAccessTokenUpdated() {
                            UserDataAsyncTask updatedUserDataAsyncTask = new UserDataAsyncTask();
                            updatedUserDataAsyncTask.setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
                                @Override
                                public void onDataLoaded(String response) {
                                    createOrUpdateTaskListInDb(response, requestMethod);
                                }
                            });
                            updatedUserDataAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
                                    mRepositoryLoadHelper.getTaskListCreateOrUpdateParameters(taskList, url, requestMethod));

                        }
                    });
                }
            }
        });
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

    public void deleteTaskList(final TaskList taskList) {
        final String url = BASE_TASK_LISTS_URL + taskList.getTaskListId();

        UserDataAsyncTask userDataAsyncTask = new UserDataAsyncTask();

        userDataAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
                mRepositoryLoadHelper.getTaskListDeleteParameters(url));

        userDataAsyncTask.setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) {
                if (response.equals("")) {
                    FirebaseWebService firebaseWebService = new FirebaseWebService();
                    firebaseWebService.refreshAccessToken(mContext, new FirebaseWebService.AccessTokenUpdatedListener() {
                        @Override
                        public void onAccessTokenUpdated() {
                            UserDataAsyncTask updatedUserDataAsyncTask = new UserDataAsyncTask();

                            updatedUserDataAsyncTask.setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
                                @Override
                                public void onDataLoaded(String response) {
                                    mTaskListsDbStore.deleteTaskList(taskList);
                                }
                            });

                            updatedUserDataAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
                                    mRepositoryLoadHelper.getTaskListDeleteParameters(url));
                        }
                    });
                } else if (response.equals("ok")) {
                    mTaskListsDbStore.deleteTaskList(taskList);
                }
            }
        });
    }

    public interface OnTaskCompletedListener {
        void onSuccess(ArrayList<TaskList> taskListArrayList);

        void onfail();
    }
}
