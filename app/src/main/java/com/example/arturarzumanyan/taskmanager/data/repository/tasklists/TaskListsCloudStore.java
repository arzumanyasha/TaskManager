package com.example.arturarzumanyan.taskmanager.data.repository.tasklists;

import com.example.arturarzumanyan.taskmanager.auth.TokenStorage;
import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.data.repository.tasks.GoogleTasksApi;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.GoogleSuiteApiFactory;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Single;

import okhttp3.ResponseBody;
import retrofit2.Response;

import static com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper.AUTHORIZATION_KEY;
import static com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper.CONTENT_TYPE_KEY;
import static com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper.JSON_CONTENT_TYPE_VALUE;
import static com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper.TOKEN_TYPE;

public class TaskListsCloudStore {
    private static final String BASE_TASK_LISTS_URL = "tasks/v1/users/@me/lists/";

    private GoogleTasksApi mGoogleTasksApi;
    private RepositoryLoadHelper mRepositoryLoadHelper;

    TaskListsCloudStore() {
        mGoogleTasksApi = GoogleSuiteApiFactory.getRetrofitInstance().create(GoogleTasksApi.class);
        mRepositoryLoadHelper = new RepositoryLoadHelper();
    }

    public Single<ResponseBody> getTaskListsFromServer() {
        Map<String, String> requestHeaderParameters = new HashMap<>();
        requestHeaderParameters.put(AUTHORIZATION_KEY, TOKEN_TYPE + TokenStorage.getTokenStorageInstance().getAccessToken());

        return mGoogleTasksApi.getTaskLists(BASE_TASK_LISTS_URL, requestHeaderParameters);
    }

    public Single<ResponseBody> addTaskListOnServer(TaskList taskList) {
        Map<String, String> requestHeaderParameters = new HashMap<>();
        requestHeaderParameters.put(CONTENT_TYPE_KEY, JSON_CONTENT_TYPE_VALUE);
        requestHeaderParameters.put(AUTHORIZATION_KEY, TOKEN_TYPE + TokenStorage.getTokenStorageInstance().getAccessToken());

        Map<String, Object> requestBodyParameters = mRepositoryLoadHelper.getTaskListCreateOrUpdateParameters(taskList);

        return mGoogleTasksApi.addTaskList(BASE_TASK_LISTS_URL, requestHeaderParameters, requestBodyParameters);
    }

    public Single<ResponseBody> updateTaskListOnServer(TaskList taskList) {
        String url = BASE_TASK_LISTS_URL + taskList.getTaskListId();

        Map<String, String> requestHeaderParameters = new HashMap<>();
        requestHeaderParameters.put(CONTENT_TYPE_KEY, JSON_CONTENT_TYPE_VALUE);
        requestHeaderParameters.put(AUTHORIZATION_KEY, TOKEN_TYPE + TokenStorage.getTokenStorageInstance().getAccessToken());

        Map<String, Object> requestBodyParameters = mRepositoryLoadHelper.getTaskListCreateOrUpdateParameters(taskList);

        return mGoogleTasksApi.updateTaskList(url, requestHeaderParameters, requestBodyParameters);
    }

    public Single<Response<ResponseBody>> deleteTaskListOnServer(final TaskList taskList) {
        String url = BASE_TASK_LISTS_URL + taskList.getTaskListId();

        Map<String, String> requestHeaderParameters = new HashMap<>();
        requestHeaderParameters.put(AUTHORIZATION_KEY, TOKEN_TYPE + TokenStorage.getTokenStorageInstance().getAccessToken());

        return mGoogleTasksApi.deleteTaskList(url, requestHeaderParameters);
    }
}
