package com.example.arturarzumanyan.taskmanager.data.repository.tasks;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.domain.ResponseDto;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;

import java.util.HashMap;

import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.PATCH;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.POST;
import static com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper.BASE_GOOGLE_APIS_URL;

public class TasksCloudStore {
    private static final String BASE_TASKS_URL = "tasks/v1/lists/";

    private RepositoryLoadHelper mRepositoryLoadHelper;

    public TasksCloudStore() {
        mRepositoryLoadHelper = new RepositoryLoadHelper();
    }

    public ResponseDto getTasksFromServer(TaskList taskList) {
        String url = BASE_GOOGLE_APIS_URL + BASE_TASKS_URL + taskList.getTaskListId() + "/tasks?showHidden=true";

        FirebaseWebService.RequestMethods requestMethod = FirebaseWebService.RequestMethods.GET;
        RequestParameters requestParameters = new RequestParameters(
                url,
                requestMethod,
                null
        );
        requestParameters.setRequestHeaderParameters(new HashMap<String, String>());

        return null;
    }

    public ResponseDto addTaskOnServer(TaskList taskList, Task task) {
        String url = BASE_GOOGLE_APIS_URL +
                BASE_TASKS_URL +
                taskList.getTaskListId() +
                "/tasks";

        RequestParameters requestParameters = mRepositoryLoadHelper.getTaskCreateOrUpdateParameters(task, url, POST);

        return null;
    }

    public ResponseDto updateTaskOnServer(TaskList taskList, Task task) {
        String url = BASE_GOOGLE_APIS_URL +
                BASE_TASKS_URL +
                taskList.getTaskListId() +
                "/tasks/" +
                task.getId();

        RequestParameters requestParameters = mRepositoryLoadHelper.getTaskCreateOrUpdateParameters(task, url, PATCH);

        return null;
    }

    public ResponseDto deleteTaskOnServer(TaskList taskList, Task task) {
        String url = BASE_GOOGLE_APIS_URL +
                BASE_TASKS_URL +
                taskList.getTaskListId() +
                "/tasks/" +
                task.getId();

        RequestParameters requestParameters = mRepositoryLoadHelper.getDeleteParameters(url);

        return null;
    }
}
