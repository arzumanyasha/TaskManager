package com.example.arturarzumanyan.taskmanager.data.repository.tasklists;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.domain.ResponseDto;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.NetworkUtil;
import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;

import java.util.HashMap;

import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.PATCH;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.POST;
import static com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper.BASE_GOOGLE_APIS_URL;

public class TaskListsCloudStore {
    private static final String BASE_TASK_LISTS_URL = "tasks/v1/users/@me/lists/";

    private RepositoryLoadHelper mRepositoryLoadHelper;

    TaskListsCloudStore() {
        mRepositoryLoadHelper = new RepositoryLoadHelper();
    }

    public ResponseDto getTaskListsFromServer() {
        FirebaseWebService.RequestMethods requestMethod = FirebaseWebService.RequestMethods.GET;
        RequestParameters requestParameters = new RequestParameters(
                BASE_GOOGLE_APIS_URL + BASE_TASK_LISTS_URL,
                requestMethod,
                null
        );
        requestParameters.setRequestHeaderParameters(new HashMap<String, String>());

        return NetworkUtil.getResultFromServer(requestParameters);
    }

    public ResponseDto addTaskListOnServer(TaskList taskList) {
        RequestParameters requestParameters = mRepositoryLoadHelper.getTaskListCreateOrUpdateParameters(taskList,
                BASE_GOOGLE_APIS_URL + BASE_TASK_LISTS_URL, POST);

        return NetworkUtil.getResultFromServer(requestParameters);
    }

    public ResponseDto updateTaskListOnServer(TaskList taskList) {
        String url = BASE_GOOGLE_APIS_URL + BASE_TASK_LISTS_URL + taskList.getTaskListId();

        RequestParameters requestParameters = mRepositoryLoadHelper.getTaskListCreateOrUpdateParameters(taskList,
                url, PATCH);

        return NetworkUtil.getResultFromServer(requestParameters);
    }

    public ResponseDto deleteTaskListOnServer(final TaskList taskList) {
        String url = BASE_GOOGLE_APIS_URL + BASE_TASK_LISTS_URL + taskList.getTaskListId();

        RequestParameters requestParameters = mRepositoryLoadHelper.getDeleteParameters(url);

        return NetworkUtil.getResultFromServer(requestParameters);
    }
}
