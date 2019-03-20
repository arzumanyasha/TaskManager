package com.example.arturarzumanyan.taskmanager.data.repository.tasks;

import com.example.arturarzumanyan.taskmanager.auth.TokenStorage;
import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.domain.Task;
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

public class TasksCloudStore {
    private static final String BASE_TASKS_URL = "tasks/v1/lists/";
    private GoogleTasksApi mGoogleTasksApi;

    private RepositoryLoadHelper mRepositoryLoadHelper;

    public TasksCloudStore() {
        mGoogleTasksApi = GoogleSuiteApiFactory.getRetrofitInstance().create(GoogleTasksApi.class);
        mRepositoryLoadHelper = new RepositoryLoadHelper();
    }

    public Single<ResponseBody> getTasksFromServer(TaskList taskList) {
        String url = BASE_TASKS_URL + taskList.getTaskListId() + "/tasks?showHidden=true";

        Map<String, String> requestHeaderParameters = new HashMap<>();
        requestHeaderParameters.put(AUTHORIZATION_KEY, TOKEN_TYPE + TokenStorage.getTokenStorageInstance().getAccessToken());

        return mGoogleTasksApi.getTasksFromTaskList(url, requestHeaderParameters);
    }

    public Single<ResponseBody> addTaskOnServer(TaskList taskList, Task task) {
        String url = BASE_TASKS_URL + taskList.getTaskListId() + "/tasks";

        Map<String, String> requestHeaderParameters = new HashMap<>();
        requestHeaderParameters.put(CONTENT_TYPE_KEY, JSON_CONTENT_TYPE_VALUE);
        requestHeaderParameters.put(AUTHORIZATION_KEY, TOKEN_TYPE + TokenStorage.getTokenStorageInstance().getAccessToken());

        Map<String, Object> requestBodyParameters = mRepositoryLoadHelper.getTaskBodyParameters(task);

        return mGoogleTasksApi.addTask(url, requestHeaderParameters, requestBodyParameters);
    }

    public Single<ResponseBody> updateTaskOnServer(TaskList taskList, Task task) {
        String url = BASE_TASKS_URL + taskList.getTaskListId() + "/tasks/" + task.getTaskId();

        Map<String, String> requestHeaderParameters = new HashMap<>();
        requestHeaderParameters.put(AUTHORIZATION_KEY, TOKEN_TYPE + TokenStorage.getTokenStorageInstance().getAccessToken());
        requestHeaderParameters.put(CONTENT_TYPE_KEY, JSON_CONTENT_TYPE_VALUE);

        Map<String, Object> requestBodyParameters = mRepositoryLoadHelper.getTaskBodyParameters(task);

        return mGoogleTasksApi.updateTask(url, requestHeaderParameters, requestBodyParameters);
    }

    public Single<Response<ResponseBody>> deleteTaskOnServer(TaskList taskList, Task task) {
        String url = BASE_TASKS_URL + taskList.getTaskListId() + "/tasks/" + task.getTaskId();

        Map<String, String> requestHeaderParameters = new HashMap<>();
        requestHeaderParameters.put(AUTHORIZATION_KEY, TOKEN_TYPE + TokenStorage.getTokenStorageInstance().getAccessToken());

        return mGoogleTasksApi.deleteTask(url, requestHeaderParameters);
    }
}
