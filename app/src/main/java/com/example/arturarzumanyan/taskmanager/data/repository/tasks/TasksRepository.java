package com.example.arturarzumanyan.taskmanager.data.repository.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.data.repository.BaseDataLoadingAsyncTask;
import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsDbStore;
import com.example.arturarzumanyan.taskmanager.data.repository.events.EventsRepository;
import com.example.arturarzumanyan.taskmanager.data.repository.events.specification.EventsSpecification;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.TaskListsRepository;
import com.example.arturarzumanyan.taskmanager.domain.Event;
import com.example.arturarzumanyan.taskmanager.domain.ResponseDto;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.NetworkUtil;
import com.example.arturarzumanyan.taskmanager.networking.base.RequestParameters;
import com.example.arturarzumanyan.taskmanager.networking.util.EventsParser;
import com.example.arturarzumanyan.taskmanager.networking.util.TasksParser;

import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;

import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.DELETE;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.GET;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.PATCH;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.POST;
import static com.example.arturarzumanyan.taskmanager.data.repository.tasks.TasksCloudStore.BASE_TASKS_URL;

public class TasksRepository {
    private TasksDbStore mTasksDbStore;
    private TasksCloudStore mTasksCloudStore;
    private RepositoryLoadHelper mRepositoryLoadHelper;
    private FirebaseWebService mFirebaseWebService;

    private Context mContext;

    public TasksRepository(Context context) {
        this.mContext = context;
        mTasksCloudStore = new TasksCloudStore(mContext);
        mTasksDbStore = new TasksDbStore(mContext);
        mRepositoryLoadHelper = new RepositoryLoadHelper(mContext);
        mFirebaseWebService = new FirebaseWebService(mContext);
    }

    public void loadTasks(TaskList taskList, final OnTasksLoadedListener listener) {
        String url = BASE_TASKS_URL + taskList.getTaskListId() + "/tasks?showHidden=true";

        TasksAsyncTask tasksAsyncTask = new TasksAsyncTask(null, taskList.getId(), mRepositoryLoadHelper,
                mFirebaseWebService, mTasksDbStore, listener);

        tasksAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener<Task>() {
            @Override
            public void onSuccess(List<Task> list) {
                listener.onSuccess(list);
            }
        });

        mRepositoryLoadHelper.requestUserData(tasksAsyncTask, url);
        /*
        List<Task> tasks = mTasksDbStore.getTasksFromTaskList(taskList.getId());

        if ((mRepositoryLoadHelper.isOnline()) && (tasks.size() == 0)) {
            mTasksCloudStore.getTasksFromTaskList(taskList, new TasksCloudStore.OnTaskCompletedListener() {
                @Override
                public void onSuccess(List<Task> taskArrayList) {
                    listener.onSuccess(taskArrayList);
                    //mTasksDbStore.addTasks(taskArrayList);
                }

                @Override
                public void onFail() {

                }
            });
        } else if ((mRepositoryLoadHelper.isOnline() && (tasks.size() != 0)) ||
                (!mRepositoryLoadHelper.isOnline() && (tasks.size() != 0))) {
            listener.onSuccess(tasks);
        } else {
            listener.onFail();
        }*/
    }
/*
    public List<Task> getTasksFromTaskList(int taskListId) {
        return mTasksDbStore.getTasksFromTaskList(taskListId);
    }*/

    public interface OnTasksLoadedListener {
        void onSuccess(List<Task> taskArrayList);

        void onFail();
    }

    public void addTask(TaskList taskList, Task task, final OnTasksLoadedListener listener) {
        String url = BASE_TASKS_URL +
                taskList.getTaskListId() +
                "/tasks";

        TasksAsyncTask tasksAsyncTask = new TasksAsyncTask(task, taskList.getId(), mRepositoryLoadHelper,
                mFirebaseWebService, mTasksDbStore, listener);
        tasksAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener<Task>() {
            @Override
            public void onSuccess(List<Task> list) {
                listener.onSuccess(list);
            }
        });

        RequestParameters requestParameters = mRepositoryLoadHelper.getTaskCreateOrUpdateParameters(task, url, POST);

        tasksAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, requestParameters);
    }

    public void updateTask(TaskList taskList, Task task, final OnTasksLoadedListener listener) {
        String url = BASE_TASKS_URL +
                taskList.getTaskListId() +
                "/tasks/" +
                task.getId();

        TasksAsyncTask tasksAsyncTask = new TasksAsyncTask(task, taskList.getId(), mRepositoryLoadHelper,
                mFirebaseWebService, mTasksDbStore, listener);
        tasksAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener<Task>() {
            @Override
            public void onSuccess(List<Task> list) {
                listener.onSuccess(list);
            }
        });

        RequestParameters requestParameters = mRepositoryLoadHelper.getTaskCreateOrUpdateParameters(task, url, PATCH);

        tasksAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, requestParameters);
    }

    public void deleteTask(TaskList taskList, Task task) {
        String url = BASE_TASKS_URL +
                taskList.getTaskListId() +
                "/tasks/" +
                task.getId();

        RequestParameters requestParameters = mRepositoryLoadHelper.getDeleteParameters(url);

        TasksAsyncTask tasksAsyncTask = new TasksAsyncTask(task, taskList.getId(), mRepositoryLoadHelper,
                mFirebaseWebService, mTasksDbStore, null);
        tasksAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener<Task>() {
            @Override
            public void onSuccess(List<Task> list) {

            }
        });
        tasksAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, requestParameters);
    }

    public static class TasksAsyncTask extends BaseDataLoadingAsyncTask<Task> {

        private int mTaskListId;
        private Task mTask;
        //private WeakReference<Context> mContextWeakReference;
        private RepositoryLoadHelper mRepositoryLoadHelper;
        private FirebaseWebService mFirebaseWebService;
        private TasksDbStore mTasksDbStore;
        //private TasksSpecification mTasksSpecification;
        private TasksRepository.OnTasksLoadedListener mListener;

        public TasksAsyncTask(Task task,
                              //Context context,
                              int taskListId,
                              RepositoryLoadHelper repositoryLoadHelper,
                              FirebaseWebService firebaseWebService,
                              TasksDbStore tasksDbStore,
                              //                 TasksSpecification tasksSpecification,
                              TasksRepository.OnTasksLoadedListener listener) {
            this.mTask = task;
            this.mTaskListId = taskListId;
            //this.mContextWeakReference = new WeakReference<>(context);
            this.mRepositoryLoadHelper = repositoryLoadHelper;
            this.mFirebaseWebService = firebaseWebService;
            this.mTasksDbStore = tasksDbStore;
            //this.mTasksSpecification = tasksSpecification;
            this.mListener = listener;
        }

        @Override
        protected List<Task> doInBackground(final RequestParameters... requestParameters) {
            if (mRepositoryLoadHelper.isOnline()) {
                ResponseDto responseDto = NetworkUtil.getResultFromServer(requestParameters[0]);

                int responseCode = responseDto.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    retryGetResultFromServer(requestParameters[0]);
                }

                if (responseCode == HttpURLConnection.HTTP_OK ||
                        responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                    if (requestParameters[0].getRequestMethod() == POST ||
                            requestParameters[0].getRequestMethod() == PATCH) {
                        dbQuery(parseTask(responseDto.getResponseData()), requestParameters[0]);
                    } else if (requestParameters[0].getRequestMethod() == GET) {
                        updateDbQuery(parseTasksData(responseDto.getResponseData()));
                    } else {
                        dbQuery(mTask, requestParameters[0]);
                    }
                }

            } else {
                if (requestParameters[0].getRequestMethod() != GET) {
                    dbQuery(mTask, requestParameters[0]);
                } else {
                    return mTasksDbStore.getTasksFromTaskList(mTaskListId);
                }
            }

            return mTasksDbStore.getTasksFromTaskList(mTaskListId);
        }

        private void retryGetResultFromServer(final RequestParameters requestParameters) {
            mFirebaseWebService.refreshAccessToken(new FirebaseWebService.AccessTokenUpdatedListener() {
                @Override
                public void onAccessTokenUpdated() {
                    TasksAsyncTask tasksAsyncTask = new TasksRepository.TasksAsyncTask(mTask, mTaskListId,
                            mRepositoryLoadHelper, mFirebaseWebService, mTasksDbStore, mListener);

                    if (requestParameters.getRequestMethod() != DELETE) {
                        tasksAsyncTask.setDataInfoLoadingListener(new UserDataLoadingListener<Task>() {
                            @Override
                            public void onSuccess(List<Task> list) {
                                mListener.onSuccess(list);
                            }
/*
                        @Override
                        public void onFail() {
                            mListener.onFail();
                        }*/
                        });
                    }

                    tasksAsyncTask.executeOnExecutor(SERIAL_EXECUTOR, requestParameters);
                }
            });
        }

        private Task parseTask(String data) {
            TasksParser tasksParser = new TasksParser();
            return tasksParser.parseTask(data, mTaskListId);
        }

        private List<Task> parseTasksData(String data) {
            TasksParser tasksParser = new TasksParser();
            return tasksParser.parseTasks(data, mTaskListId);
        }

        private void dbQuery(Task task, RequestParameters requestParameters) {
            if (requestParameters.getRequestMethod() == POST ||
                    requestParameters.getRequestMethod() == PATCH) {
                mTasksDbStore.addOrUpdateTasks(Collections.singletonList(task));
            } else if (requestParameters.getRequestMethod() == FirebaseWebService.RequestMethods.DELETE) {
                mTasksDbStore.deleteTask(task);
            }
        }

        private void updateDbQuery(List<Task> tasks) {
            mTasksDbStore.addOrUpdateTasks(tasks);
            mTasksDbStore.getTasksFromTaskList(mTaskListId);
        }

    }
}

