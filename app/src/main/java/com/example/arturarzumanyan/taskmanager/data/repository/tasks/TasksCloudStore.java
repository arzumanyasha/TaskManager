package com.example.arturarzumanyan.taskmanager.data.repository.tasks;

import android.content.Context;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.domain.ResponseDto;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.NetworkUtil;
import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;

import java.util.HashMap;

import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.PATCH;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.POST;

public class TasksCloudStore {
    private static final String BASE_TASKS_URL = "https://www.googleapis.com/tasks/v1/lists/";

    private RepositoryLoadHelper mRepositoryLoadHelper;

    private Context mContext;

    public TasksCloudStore(Context context) {
        this.mContext = context;
        mRepositoryLoadHelper = new RepositoryLoadHelper(mContext);
    }

    public ResponseDto getTasksFromServer(TaskList taskList) {
        String url = BASE_TASKS_URL + taskList.getTaskListId() + "/tasks?showHidden=true";

        FirebaseWebService.RequestMethods requestMethod = FirebaseWebService.RequestMethods.GET;
        RequestParameters requestParameters = new RequestParameters(mContext,
                url,
                requestMethod,
                new HashMap<String, Object>()
        );
        requestParameters.setRequestHeaderParameters(new HashMap<String, String>());

        return NetworkUtil.getResultFromServer(requestParameters);
    }

    public ResponseDto addTaskOnServer(TaskList taskList, Task task) {
        String url = BASE_TASKS_URL +
                taskList.getTaskListId() +
                "/tasks";

        RequestParameters requestParameters = mRepositoryLoadHelper.getTaskCreateOrUpdateParameters(task, url, POST);

        return NetworkUtil.getResultFromServer(requestParameters);
    }

    public ResponseDto updateTaskOnServer(TaskList taskList, Task task) {
        String url = BASE_TASKS_URL +
                taskList.getTaskListId() +
                "/tasks/" +
                task.getId();

        RequestParameters requestParameters = mRepositoryLoadHelper.getTaskCreateOrUpdateParameters(task, url, PATCH);

        return NetworkUtil.getResultFromServer(requestParameters);
    }

    public ResponseDto deleteTaskOnServer(TaskList taskList, Task task) {
        String url = BASE_TASKS_URL +
                taskList.getTaskListId() +
                "/tasks/" +
                task.getId();

        RequestParameters requestParameters = mRepositoryLoadHelper.getDeleteParameters(url);

        return NetworkUtil.getResultFromServer(requestParameters);
    }
}
