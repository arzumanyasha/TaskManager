package com.example.arturarzumanyan.taskmanager.data.repository.tasklists;

import android.content.Context;
import android.os.AsyncTask;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.data.repository.BaseDataLoadingAsyncTask;
import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.specification.AllTaskListsSpecification;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.specification.TaskListFromIdSpecification;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.specification.TaskListsSpecification;
import com.example.arturarzumanyan.taskmanager.data.repository.tasks.TasksCloudStore;
import com.example.arturarzumanyan.taskmanager.data.repository.tasks.TasksDbStore;
import com.example.arturarzumanyan.taskmanager.domain.ResponseDto;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.NetworkUtil;
import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;
import com.example.arturarzumanyan.taskmanager.networking.util.Log;
import com.example.arturarzumanyan.taskmanager.networking.util.TaskListsParser;
import com.example.arturarzumanyan.taskmanager.networking.util.TasksParser;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.GET;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.PATCH;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.POST;
import static com.example.arturarzumanyan.taskmanager.data.repository.tasklists.TaskListsCloudStore.BASE_TASK_LISTS_URL;
import static com.example.arturarzumanyan.taskmanager.data.repository.tasks.TasksCloudStore.BASE_TASKS_URL;

public class TaskListsRepository {
    private TaskListsCloudStore mTaskListsCloudStore;
    private TaskListsDbStore mTaskListsDbStore;
    private TasksDbStore mTasksDbStore;
    private TasksCloudStore mTasksCloudStore;
    private RepositoryLoadHelper mRepositoryLoadHelper;
    private FirebaseWebService mFirebaseWebService;
    private Context mContext;

    public TaskListsRepository(Context context) {
        this.mContext = context;
        mTaskListsCloudStore = new TaskListsCloudStore(mContext);
        mTaskListsDbStore = new TaskListsDbStore(mContext);
        mTasksCloudStore = new TasksCloudStore(mContext);
        mTasksDbStore = new TasksDbStore(mContext);
        mRepositoryLoadHelper = new RepositoryLoadHelper(mContext);
        mFirebaseWebService = new FirebaseWebService(mContext);
    }

    public void loadTaskLists(TaskListsSpecification taskListsSpecification, final OnTaskListsLoadedListener listener) {
        TaskListsAsyncTask taskListsAsyncTask = new TaskListsAsyncTask(null, mContext, mRepositoryLoadHelper,
                mFirebaseWebService, mTaskListsDbStore, mTasksDbStore, taskListsSpecification, listener);

        taskListsAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener<TaskList>() {
            @Override
            public void onSuccess(List<TaskList> list) {
                Log.v("TaskLists loaded successfully");
                listener.onSuccess(list);
            }
        });

        mRepositoryLoadHelper.requestUserData(taskListsAsyncTask, BASE_TASK_LISTS_URL);
    }

    public void addTaskList(TaskList taskList, final OnTaskListsLoadedListener listener) {
        RequestParameters requestParameters = mRepositoryLoadHelper.getTaskListCreateOrUpdateParameters(taskList,
                BASE_TASK_LISTS_URL, POST);

        AllTaskListsSpecification allTaskListsSpecification = new AllTaskListsSpecification();

        TaskListsAsyncTask taskListsAsyncTask = new TaskListsAsyncTask(taskList, mContext,
                mRepositoryLoadHelper, mFirebaseWebService, mTaskListsDbStore,
                mTasksDbStore, allTaskListsSpecification, null);

        taskListsAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener<TaskList>() {
            @Override
            public void onSuccess(List<TaskList> list) {
                listener.onSuccess(list.get(list.size() - 1));
            }
        });
        taskListsAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, requestParameters);
    }

    public void updateTaskList(TaskList taskList, final OnTaskListsLoadedListener listener) {
        String url = BASE_TASK_LISTS_URL + taskList.getTaskListId();

        RequestParameters requestParameters = mRepositoryLoadHelper.getTaskListCreateOrUpdateParameters(taskList,
                url, PATCH);

        TaskListFromIdSpecification taskListFromIdSpecification = new TaskListFromIdSpecification();
        taskListFromIdSpecification.setTaskListId(taskList.getId());

        TaskListsAsyncTask taskListsAsyncTask = new TaskListsAsyncTask(taskList, mContext,
                mRepositoryLoadHelper, mFirebaseWebService, mTaskListsDbStore,
                mTasksDbStore, taskListFromIdSpecification, null);

        taskListsAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener<TaskList>() {
            @Override
            public void onSuccess(List<TaskList> list) {
                listener.onSuccess(list.get(0));
            }
        });
        taskListsAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, requestParameters);
    }

    public void deleteTaskList(TaskList taskList, final OnTaskListsLoadedListener listener) {
        String url = BASE_TASK_LISTS_URL + taskList.getTaskListId();

        RequestParameters requestParameters = mRepositoryLoadHelper.getDeleteParameters(url);

        TaskListFromIdSpecification taskListFromIdSpecification = new TaskListFromIdSpecification();
        taskListFromIdSpecification.setTaskListId(taskList.getId());

        TaskListsAsyncTask taskListsAsyncTask = new TaskListsAsyncTask(taskList, mContext,
                mRepositoryLoadHelper, mFirebaseWebService, mTaskListsDbStore,
                mTasksDbStore, taskListFromIdSpecification, null);

        taskListsAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener<TaskList>() {
            @Override
            public void onSuccess(List<TaskList> list) {
                listener.onSuccess(list.get(0));
            }
        });
        taskListsAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, requestParameters);
    }

    public interface OnTaskListsLoadedListener {
        void onSuccess(List<TaskList> taskListArrayList);

        void onUpdate(List<TaskList> taskLists);

        void onSuccess(TaskList taskList);

        void onFail();
    }

    public static class TaskListsAsyncTask extends BaseDataLoadingAsyncTask<TaskList> {

        private TaskList mTaskList;
        private WeakReference<Context> mContextWeakReference;
        private RepositoryLoadHelper mRepositoryLoadHelper;
        private FirebaseWebService mFirebaseWebService;
        private TaskListsDbStore mTaskListsDbStore;
        private TasksDbStore mTasksDbStore;
        private TaskListsSpecification mTaskListsSpecification;
        private OnTaskListsLoadedListener mListener;

        public TaskListsAsyncTask(TaskList taskList,
                                  Context context,
                                  RepositoryLoadHelper repositoryLoadHelper,
                                  FirebaseWebService firebaseWebService,
                                  TaskListsDbStore taskListsDbStore,
                                  TasksDbStore tasksDbStore,
                                  TaskListsSpecification taskListsSpecification,
                                  OnTaskListsLoadedListener listener) {
            this.mTaskList = taskList;
            this.mContextWeakReference = new WeakReference<>(context);
            this.mRepositoryLoadHelper = repositoryLoadHelper;
            this.mFirebaseWebService = firebaseWebService;
            this.mTaskListsDbStore = taskListsDbStore;
            this.mTasksDbStore = tasksDbStore;
            this.mTaskListsSpecification = taskListsSpecification;
            this.mListener = listener;
        }

        @Override
        protected List<TaskList> doInBackground(final RequestParameters... requestParameters) {
            if (mRepositoryLoadHelper.isOnline()) {
                Log.v("Loading tasklists...");
                ResponseDto responseDto = NetworkUtil.getResultFromServer(requestParameters[0]);
                int responseCode = responseDto.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    retryGetResultFromServer(requestParameters[0]);
                }

                if (responseCode == HttpURLConnection.HTTP_OK ||
                        responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                    Log.v("TaskLists loaded successfully");
                    if (requestParameters[0].getRequestMethod() == POST) {
                        dbQuery(parseTaskList(responseDto.getResponseData()), requestParameters[0]);
                    } else if (requestParameters[0].getRequestMethod() == PATCH) {
                        dbQuery(mTaskList, requestParameters[0]);
                    } else if (requestParameters[0].getRequestMethod() == GET) {
                        List<TaskList> taskLists = parseTaskListsData(responseDto.getResponseData());
                        updateDbQuery(taskLists);

                        for (int i = 0; i < taskLists.size(); i++) {
                            loadTasks(taskLists.get(i), i + 1);
                        }

                    } else {
                        dbQuery(mTaskList, requestParameters[0]);
                        return Collections.singletonList(mTaskList);
                    }
                }

            } else {
                if (requestParameters[0].getRequestMethod() != GET) {
                    dbQuery(mTaskList, requestParameters[0]);
                } else {
                    return mTaskListsDbStore.getTaskLists(mTaskListsSpecification);
                }
            }

            return mTaskListsDbStore.getTaskLists(mTaskListsSpecification);
        }

        private void retryGetResultFromServer(final RequestParameters requestParameters) {
            mFirebaseWebService.refreshAccessToken(new FirebaseWebService.AccessTokenUpdatedListener() {
                @Override
                public void onAccessTokenUpdated() {
                    Log.v("Access token refreshed successfully");
                    TaskListsAsyncTask taskListsAsyncTask = new TaskListsAsyncTask(null,
                            mContextWeakReference.get(), mRepositoryLoadHelper, mFirebaseWebService,
                            mTaskListsDbStore, mTasksDbStore, mTaskListsSpecification, mListener);
                    taskListsAsyncTask.setDataInfoLoadingListener(new UserDataLoadingListener<TaskList>() {
                        @Override
                        public void onSuccess(List<TaskList> list) {
                            Log.v("TaskLists loading retried successfully");
                            //mListener.onSuccess(list);
                            mListener.onUpdate(list);
                        }
/*
                        @Override
                        public void onFail() {
                            mListener.onFail();
                        }*/
                    });

                    mRepositoryLoadHelper.requestUserData(taskListsAsyncTask, requestParameters.getUrl());
                }
            });
        }

        private TaskList parseTaskList(String data) {
            TaskListsParser taskListsParser = new TaskListsParser();
            return taskListsParser.parseTaskList(data);
        }

        private List<TaskList> parseTaskListsData(String data) {
            TaskListsParser taskListsParser = new TaskListsParser();
            return taskListsParser.parseTaskLists(data);
        }

        private void loadTasks(TaskList taskList, int taskListNumber) {
            String url = BASE_TASKS_URL + taskList.getTaskListId() + "/tasks?showHidden=true";
            RequestParameters requestParameters = new RequestParameters(mContextWeakReference.get(),
                    url,
                    FirebaseWebService.RequestMethods.GET,
                    new HashMap<String, Object>()
            );

            requestParameters.setRequestHeaderParameters(new HashMap<String, String>());

            ResponseDto responseDto = NetworkUtil.getResultFromServer(requestParameters);

            int responseCode = responseDto.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK ||
                    responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                TasksParser tasksParser = new TasksParser();
                updateTasksInDbQuery(tasksParser.parseTasks(responseDto.getResponseData(), taskListNumber));
            }
        }

        private void updateTasksInDbQuery(List<Task> tasks) {
            mTasksDbStore.addOrUpdateTasks(tasks);
        }

        private void dbQuery(TaskList taskList, RequestParameters requestParameters) {
            if (requestParameters.getRequestMethod() == POST ||
                    requestParameters.getRequestMethod() == PATCH) {
                mTaskListsDbStore.addOrUpdateTaskLists(Collections.singletonList(taskList));
            } else if (requestParameters.getRequestMethod() == FirebaseWebService.RequestMethods.DELETE) {
                mTaskListsDbStore.deleteTaskList(taskList);
            }
        }

        private void updateDbQuery(List<TaskList> events) {
            mTaskListsDbStore.addOrUpdateTaskLists(events);
            mTaskListsDbStore.getTaskLists(mTaskListsSpecification);
        }

    }
}
