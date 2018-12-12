package com.example.arturarzumanyan.taskmanager.data.repository.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.TaskListsDbStore;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.data.repository.BaseDataLoadingAsyncTask;
import com.example.arturarzumanyan.taskmanager.networking.util.TasksParser;

import java.util.ArrayList;
import java.util.List;

public class TasksCloudStore {
    public static final String BASE_TASKS_URL = "https://www.googleapis.com/tasks/v1/lists/";

    private TaskListsDbStore mTaskListsDbStore;
    private TasksDbStore mTasksDbStore;
    private RepositoryLoadHelper mRepositoryLoadHelper;
    private ArrayList<BaseDataLoadingAsyncTask> mUserTasksAsyncTaskList = new ArrayList<>();

    private Context mContext;

    public TasksCloudStore(Context context) {
        this.mContext = context;
        mRepositoryLoadHelper = new RepositoryLoadHelper(mContext);
        mTaskListsDbStore = new TaskListsDbStore(mContext);
        mTasksDbStore = new TasksDbStore(mContext);
    }

    public void getTasksFromTaskList(TaskList taskList, final OnTaskCompletedListener listener) {
        String url = BASE_TASKS_URL + taskList.getTaskListId() + "/tasks?showHidden=true";
        /*final int taskListId = taskList.getId();
        mUserTasksAsyncTaskList.add(new BaseDataLoadingAsyncTask());
        int position = mUserTasksAsyncTaskList.size() - 1;
        mRepositoryLoadHelper.requestUserData(mUserTasksAsyncTaskList.get(position), url);
        mUserTasksAsyncTaskList.get(position).setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) {
                TasksParser tasksParser = new TasksParser();
                listener.onSuccess(tasksParser.parseTasks(response, taskListId));
            }

            @Override
            public void onFail() {
                listener.onSuccess(mTasksDbStore.getTasksFromTaskList(taskListId));
            }
        });*/

    }

    public void addTask(Task task, final OnTaskCompletedListener listener) {
        /*
        final String url = BASE_TASKS_URL +
                mTaskListsDbStore.getTaskList(task.getListId()).getTaskListId() +
                "/tasks";

        sendRequest(task, url, FirebaseWebService.RequestMethods.POST, listener);*/
    }

    public void updateTask(Task task, final OnTaskCompletedListener listener) {
        /*
        final String url = BASE_TASKS_URL +
                mTaskListsDbStore.getTaskList(task.getListId()).getTaskListId() +
                "/tasks/" +
                task.getId();

        sendRequest(task, url, FirebaseWebService.RequestMethods.PATCH, listener);*/
    }

    private void sendRequest(final Task task,
                             final String url,
                             final FirebaseWebService.RequestMethods requestMethod,
                             final OnTaskCompletedListener listener) {
/*
        BaseDataLoadingAsyncTask baseDataLoadingAsyncTask = new BaseDataLoadingAsyncTask();
        baseDataLoadingAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
                mRepositoryLoadHelper.getTaskCreateOrUpdateParameters(task, url, requestMethod));

        baseDataLoadingAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) {
                createOrUpdateTaskInDb(response, task, requestMethod);
                listener.onSuccess(mTasksDbStore.getTasksFromTaskList(task.getListId()));
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
                                createOrUpdateTaskInDb(response, task, requestMethod);
                                listener.onSuccess(mTasksDbStore.getTasksFromTaskList(task.getListId()));
                            }

                            @Override
                            public void onFail() {
                                if (requestMethod == FirebaseWebService.RequestMethods.POST) {
                                    mTasksDbStore.addTask(task);
                                } else if (requestMethod == FirebaseWebService.RequestMethods.PATCH) {
                                    mTasksDbStore.updateTask(task);
                                }
                            }
                        });
                        updatedBaseDataLoadingAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
                                mRepositoryLoadHelper.getTaskCreateOrUpdateParameters(task, url, requestMethod));

                    }
                });
            }
        });*/
    }

    private void createOrUpdateTaskInDb(String response,
                                        Task task,
                                        FirebaseWebService.RequestMethods requestMethod) {
        TasksParser tasksParser = new TasksParser();
        if (requestMethod == FirebaseWebService.RequestMethods.POST) {
            //mTasksDbStore.addTask(tasksParser.parseTask(response, task.getListId()));
        } else if (requestMethod == FirebaseWebService.RequestMethods.PATCH) {
            //mTasksDbStore.updateTask(tasksParser.parseTask(response, task.getListId()));
        }
    }

    public void deleteTask(final Task task) {

/*        final String url = BASE_TASKS_URL +
                mTaskListsDbStore.getTaskList(task.getListId()).getTaskListId() +
                "/tasks/" +
                task.getId();

        BaseDataLoadingAsyncTask baseDataLoadingAsyncTask = new BaseDataLoadingAsyncTask();

        baseDataLoadingAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
                mRepositoryLoadHelper.getDeleteParameters(url));

        baseDataLoadingAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) {
                mTasksDbStore.deleteTask(task);
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
                                mTasksDbStore.deleteTask(task);
                            }

                            @Override
                            public void onFail() {
                                mTasksDbStore.deleteTask(task);
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
        void onSuccess(List<Task> taskArrayList);

        void onFail();
    }
}
