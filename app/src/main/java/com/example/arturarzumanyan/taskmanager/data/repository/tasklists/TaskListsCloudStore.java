package com.example.arturarzumanyan.taskmanager.data.repository.tasklists;

import android.content.Context;
import android.os.AsyncTask;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.UserDataAsyncTask;
import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;
import com.example.arturarzumanyan.taskmanager.networking.util.TaskListsParser;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.arturarzumanyan.taskmanager.networking.base.BaseHttpUrlConnection.JSON_CONTENT_TYPE_VALUE;
import static com.example.arturarzumanyan.taskmanager.networking.util.TaskListsParser.TITLE_KEY;

public class TaskListsCloudStore {
    private static final String BASE_TASK_LISTS_URL = "https://www.googleapis.com/tasks/v1/users/@me/lists/";

    private UserDataAsyncTask mUserTaskListsAsyncTask;
    private Context mContext;

    public TaskListsCloudStore(Context context) {
        this.mContext = context;
        mUserTaskListsAsyncTask = new UserDataAsyncTask();
    }

    public void getTaskLists(final OnTaskCompletedListener listener) {
        RepositoryLoadHelper repositoryLoadHelper = new RepositoryLoadHelper(mContext);
        repositoryLoadHelper.requestUserData(mUserTaskListsAsyncTask, BASE_TASK_LISTS_URL);
        mUserTaskListsAsyncTask.setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) throws JSONException {
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
                             String url,
                             final FirebaseWebService.RequestMethods requestMethod) {
        HashMap<String, String> requestBody = new HashMap<>();
        requestBody.put(TITLE_KEY, taskList.getTitle());

        HashMap<String, String> requestHeaderParameters = new HashMap<>();

        requestHeaderParameters.put("Content-Type", JSON_CONTENT_TYPE_VALUE);

        RequestParameters requestParameters = new RequestParameters(mContext,
                url,
                requestMethod,
                requestBody);
        requestParameters.setRequestHeaderParameters(requestHeaderParameters);

        UserDataAsyncTask userDataAsyncTask = new UserDataAsyncTask();
        userDataAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, requestParameters);

        userDataAsyncTask.setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) {
                if (!response.equals("")) {
                    TaskListsDbStore taskListsDbStore = new TaskListsDbStore(mContext);
                    TaskListsParser taskListsParser = new TaskListsParser();
                    if (requestMethod == FirebaseWebService.RequestMethods.POST) {
                        taskListsDbStore.addTaskList(taskListsParser.parseTaskList(response));
                    } else if (requestMethod == FirebaseWebService.RequestMethods.PATCH) {
                        taskListsDbStore.updateTaskList(taskListsParser.parseTaskList(response));
                    }
                } else {
                    FirebaseWebService firebaseWebService = new FirebaseWebService();
                    firebaseWebService.refreshAccessToken(mContext);
                }
            }
        });
    }

    public void deleteTaskList(final TaskList taskList) {
        final String url = BASE_TASK_LISTS_URL + taskList.getTaskListId();

        RequestParameters requestParameters = new RequestParameters(
                mContext,
                url,
                FirebaseWebService.RequestMethods.DELETE,
                new HashMap<String, String>()
        );

        requestParameters.setRequestHeaderParameters(new HashMap<String, String>());
        UserDataAsyncTask userDataAsyncTask = new UserDataAsyncTask();

        userDataAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, requestParameters);

        userDataAsyncTask.setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) {
                if (response.equals("")) {
                    FirebaseWebService firebaseWebService = new FirebaseWebService();
                    firebaseWebService.refreshAccessToken(mContext);
                } else if (response.equals("ok")){
                    TaskListsDbStore taskListsDbStore = new TaskListsDbStore(mContext);
                    taskListsDbStore.deleteTaskList(taskList);
                }
            }
        });
    }

    public interface OnTaskCompletedListener {
        void onSuccess(ArrayList<TaskList> taskListArrayList);

        void onfail();
    }
}
