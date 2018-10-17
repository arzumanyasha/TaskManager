package com.example.arturarzumanyan.taskmanager.data.repository.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.auth.TokenStorage;
import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.TaskListsDbStore;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.UserDataAsyncTask;
import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;
import com.example.arturarzumanyan.taskmanager.networking.util.TasksParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper.AUTHORIZATION_KEY;
import static com.example.arturarzumanyan.taskmanager.networking.base.BaseHttpUrlConnection.JSON_CONTENT_TYPE_VALUE;
import static com.example.arturarzumanyan.taskmanager.networking.util.TaskListsParser.TITLE_KEY;
import static com.example.arturarzumanyan.taskmanager.networking.util.TasksParser.DUE_KEY;
import static com.example.arturarzumanyan.taskmanager.networking.util.TasksParser.NOTES_KEY;

public class TasksCloudStore {
    private static final String BASE_TASKS_URL = "https://www.googleapis.com/tasks/v1/lists/";

    private TaskListsDbStore mTaskListsDbStore;
    private RepositoryLoadHelper mRepositoryLoadHelper;
    private ArrayList<UserDataAsyncTask> mUserTasksAsyncTaskList = new ArrayList<>();

    private Context mContext;

    public TasksCloudStore(Context context) {
        this.mContext = context;
        mRepositoryLoadHelper = new RepositoryLoadHelper(mContext);
        mTaskListsDbStore = new TaskListsDbStore(mContext);
    }

    public void getTasksFromTaskList(TaskList taskList, final OnTaskCompletedListener listener) {
        String url = BASE_TASKS_URL + taskList.getTaskListId() + "/tasks?showHidden=true";
        final int taskListId = taskList.getId();
        mUserTasksAsyncTaskList.add(new UserDataAsyncTask());
        int position = mUserTasksAsyncTaskList.size() - 1;
        mRepositoryLoadHelper.requestUserData(mUserTasksAsyncTaskList.get(position), url);
        mUserTasksAsyncTaskList.get(position).setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) throws JSONException, ParseException {
                TasksParser tasksParser = new TasksParser();
                listener.onSuccess(tasksParser.parseTasks(response, taskListId));
            }
        });

    }

    public void addTask(Task task) {
        final String url = BASE_TASKS_URL +
                mTaskListsDbStore.getTaskList(task.getListId()).getTaskListId() +
                "/tasks";

        sendRequest(task, url, FirebaseWebService.RequestMethods.POST);
    }

    public void updateTask(Task task) {
        final String url = BASE_TASKS_URL +
                mTaskListsDbStore.getTaskList(task.getListId()).getTaskListId() +
                "/tasks/" +
                task.getId();

        sendRequest(task, url, FirebaseWebService.RequestMethods.PATCH);
    }

    private void sendRequest(final Task task, String url, final FirebaseWebService.RequestMethods requestMethod) {
        TokenStorage tokenStorage = new TokenStorage();

        HashMap<String, String> requestBodyParameters = new HashMap<>();

        requestBodyParameters.put(TITLE_KEY, task.getName());

        if (!task.getDescription().isEmpty()) {
            requestBodyParameters.put(NOTES_KEY, task.getDescription());
        }

        if (task.getDate() != null) {
            requestBodyParameters.put(DUE_KEY, DateUtils.formatTaskDate(task.getDate()));
        }

        HashMap<String, String> requestHeaderParameters = new HashMap<>();
        String token = tokenStorage.getAccessToken(mContext);
        requestHeaderParameters.put(AUTHORIZATION_KEY, "Bearer " + tokenStorage.getAccessToken(mContext));
        requestHeaderParameters.put("Content-Type", JSON_CONTENT_TYPE_VALUE);
        RequestParameters requestParameters = new RequestParameters(url,
                requestMethod,
                requestBodyParameters,
                requestHeaderParameters);
        UserDataAsyncTask userDataAsyncTask = new UserDataAsyncTask();
        userDataAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, requestParameters);

        userDataAsyncTask.setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) {
                if (!response.equals("")) {
                    String asdfa = response;
                    TasksDbStore tasksDbStore = new TasksDbStore(mContext);
                    TasksParser tasksParser = new TasksParser();
                    if (requestMethod == FirebaseWebService.RequestMethods.POST) {
                        tasksDbStore.addTask(tasksParser.parseTask(response, task.getListId()));
                    } else if (requestMethod == FirebaseWebService.RequestMethods.PATCH) {
                        tasksDbStore.updateTask(tasksParser.parseTask(response, task.getListId()));
                    }
                } else {
                    FirebaseWebService firebaseWebService = new FirebaseWebService();
                    firebaseWebService.refreshAccessToken(mContext);
                }
            }
        });
    }

    public void deleteTask(Task task) {
        final String url = BASE_TASKS_URL +
                mTaskListsDbStore.getTaskList(task.getListId()).getTaskListId() +
                "/tasks/" +
                task.getId();

        TokenStorage tokenStorage = new TokenStorage();

        HashMap<String, String> requestBodyParameters = new HashMap<>();
        HashMap<String, String> requestHeaderParameters = new HashMap<>();
        String token = tokenStorage.getAccessToken(mContext);
        requestHeaderParameters.put(AUTHORIZATION_KEY, "Bearer " + tokenStorage.getAccessToken(mContext));

        RequestParameters requestParameters = new RequestParameters(url,
                FirebaseWebService.RequestMethods.DELETE,
                requestBodyParameters,
                requestHeaderParameters);
        UserDataAsyncTask userDataAsyncTask = new UserDataAsyncTask();

        userDataAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, requestParameters);

        userDataAsyncTask.setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) {
                if (response.equals("")) {
                    FirebaseWebService firebaseWebService = new FirebaseWebService();
                    firebaseWebService.refreshAccessToken(mContext);
                }
            }
        });
    }

    public interface OnTaskCompletedListener {
        void onSuccess(ArrayList<Task> taskArrayList);

        void onfail();
    }
}
