package com.example.arturarzumanyan.taskmanager.data.repository.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService;
import com.example.arturarzumanyan.taskmanager.data.repository.BaseDataLoadingAsyncTask;
import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.domain.ResponseDto;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.util.Log;
import com.example.arturarzumanyan.taskmanager.networking.util.TasksParser;

import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;

import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.DELETE;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.GET;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.PATCH;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.POST;

public class TasksRepository {
    private TasksDbStore mTasksDbStore;
    private TasksCloudStore mTasksCloudStore;
    private RepositoryLoadHelper mRepositoryLoadHelper;
    private FirebaseWebService mFirebaseWebService;

    public TasksRepository(Context context) {
        mTasksCloudStore = new TasksCloudStore(context);
        mTasksDbStore = new TasksDbStore(context);
        mRepositoryLoadHelper = new RepositoryLoadHelper(context);
        mFirebaseWebService = new FirebaseWebService(context);
    }

    public void loadTasks(TaskList taskList, final OnTasksLoadedListener listener) {
        TasksAsyncTask tasksAsyncTask = new TasksAsyncTask(null, taskList, mRepositoryLoadHelper,
                mFirebaseWebService, mTasksDbStore, mTasksCloudStore, listener);

        tasksAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener<Task>() {
            @Override
            public void onSuccess(List<Task> list) {
                listener.onSuccess(list);
            }
        });

        tasksAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, GET);
    }

    public interface OnTasksLoadedListener {
        void onSuccess(List<Task> taskArrayList);

        void onFail();
    }

    public void addOrUpdateTask(TaskList taskList, Task task, FirebaseWebService.RequestMethods requestMethod,
                                final OnTasksLoadedListener listener) {
        TasksAsyncTask tasksAsyncTask = new TasksAsyncTask(task, taskList, mRepositoryLoadHelper,
                mFirebaseWebService, mTasksDbStore, mTasksCloudStore, listener);
        tasksAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener<Task>() {
            @Override
            public void onSuccess(List<Task> list) {
                listener.onSuccess(list);
            }
        });

        tasksAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, requestMethod);
    }

    public void deleteTask(TaskList taskList, Task task) {
        TasksAsyncTask tasksAsyncTask = new TasksAsyncTask(task, taskList, mRepositoryLoadHelper,
                mFirebaseWebService, mTasksDbStore, mTasksCloudStore, null);

        tasksAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener<Task>() {
            @Override
            public void onSuccess(List<Task> list) {
                Log.v("Task successfully deleted");
            }
        });

        tasksAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, DELETE);
    }

    public static class TasksAsyncTask extends BaseDataLoadingAsyncTask<Task> {

        private TaskList mTaskList;
        private Task mTask;
        private RepositoryLoadHelper mRepositoryLoadHelper;
        private FirebaseWebService mFirebaseWebService;
        private TasksDbStore mTasksDbStore;
        private TasksCloudStore mTasksCloudStore;
        private TasksRepository.OnTasksLoadedListener mListener;

        public TasksAsyncTask(Task task,
                              TaskList taskList,
                              RepositoryLoadHelper repositoryLoadHelper,
                              FirebaseWebService firebaseWebService,
                              TasksDbStore tasksDbStore,
                              TasksCloudStore tasksCloudStore,
                              TasksRepository.OnTasksLoadedListener listener) {
            this.mTask = task;
            this.mTaskList = taskList;
            this.mRepositoryLoadHelper = repositoryLoadHelper;
            this.mFirebaseWebService = firebaseWebService;
            this.mTasksDbStore = tasksDbStore;
            this.mTasksCloudStore = tasksCloudStore;
            this.mListener = listener;
        }

        @Override
        protected List<Task> doInBackground(FirebaseWebService.RequestMethods... requestMethods) {
            if (mRepositoryLoadHelper.isOnline()) {
                ResponseDto responseDto = getResponseFromServer(requestMethods[0]);

                int responseCode = 0;
                if (responseDto != null) {
                    responseCode = responseDto.getResponseCode();
                }

                if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    retryGetResultFromServer(requestMethods[0]);
                } else {
                    if (responseCode == HttpURLConnection.HTTP_OK ||
                            responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                        if (requestMethods[0] == POST || requestMethods[0] == PATCH) {
                            Task task = parseTask(responseDto.getResponseData());
                            dbQuery(task, requestMethods[0]);
                        } else if (requestMethods[0] == GET) {
                            List<Task> tasks = parseTasksData(responseDto.getResponseData());
                            updateDbQuery(tasks);
                        } else {
                            dbQuery(mTask, requestMethods[0]);
                        }
                    }
                }
            } else {
                if (requestMethods[0] != GET) {
                    dbQuery(mTask, requestMethods[0]);
                } else {
                    return mTasksDbStore.getTasksFromTaskList(mTaskList.getId());
                }
            }

            return mTasksDbStore.getTasksFromTaskList(mTaskList.getId());
        }

        private ResponseDto getResponseFromServer(FirebaseWebService.RequestMethods requestMethod) {
            switch (requestMethod) {
                case GET: {
                    return mTasksCloudStore.getTasksFromServer(mTaskList);
                }
                case POST: {
                    return mTasksCloudStore.addTaskOnServer(mTaskList, mTask);
                }
                case PATCH: {
                    return mTasksCloudStore.updateTaskOnServer(mTaskList, mTask);
                }
                case DELETE: {
                    return mTasksCloudStore.deleteTaskOnServer(mTaskList, mTask);
                }
                default: {
                    return null;
                }
            }
        }

        private void retryGetResultFromServer(final FirebaseWebService.RequestMethods requestMethod) {
            mFirebaseWebService.refreshAccessToken(new FirebaseWebService.AccessTokenUpdatedListener() {
                @Override
                public void onAccessTokenUpdated() {
                    TasksAsyncTask tasksAsyncTask = new TasksRepository.TasksAsyncTask(mTask, mTaskList,
                            mRepositoryLoadHelper, mFirebaseWebService, mTasksDbStore, mTasksCloudStore, mListener);

                    if (requestMethod != DELETE) {
                        tasksAsyncTask.setDataInfoLoadingListener(new UserDataLoadingListener<Task>() {
                            @Override
                            public void onSuccess(List<Task> list) {
                                mListener.onSuccess(list);
                            }
                        });
                    }

                    tasksAsyncTask.executeOnExecutor(SERIAL_EXECUTOR, requestMethod);
                }
            });
        }

        private Task parseTask(String data) {
            TasksParser tasksParser = new TasksParser();
            return tasksParser.parseTask(data, mTaskList.getId());
        }

        private List<Task> parseTasksData(String data) {
            TasksParser tasksParser = new TasksParser();
            return tasksParser.parseTasks(data, mTaskList.getId());
        }

        private void dbQuery(Task task, FirebaseWebService.RequestMethods requestMethod) {
            if (requestMethod == POST || requestMethod == PATCH) {
                mTasksDbStore.addOrUpdateTasks(Collections.singletonList(task));
            } else if (requestMethod == FirebaseWebService.RequestMethods.DELETE) {
                mTasksDbStore.deleteTask(task);
            }
        }

        private void updateDbQuery(List<Task> tasks) {
            mTasksDbStore.addOrUpdateTasks(tasks);
            mTasksDbStore.getTasksFromTaskList(mTaskList.getId());
        }

    }
}

