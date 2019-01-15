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

import java.util.Collections;
import java.util.List;

import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.DELETE;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.GET;
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

            @Override
            public void onFail(String message) {
                listener.onFail(message + '\n' + "Failed to load tasks");
            }
        });

        tasksAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, GET);
    }

    public interface OnTasksLoadedListener {
        void onSuccess(List<Task> taskArrayList);

        void onFail(String message);
    }

    public void addOrUpdateTask(TaskList taskList, Task task, final FirebaseWebService.RequestMethods requestMethod,
                                final OnTasksLoadedListener listener) {
        TasksAsyncTask tasksAsyncTask = new TasksAsyncTask(task, taskList, mRepositoryLoadHelper,
                mFirebaseWebService, mTasksDbStore, mTasksCloudStore, listener);
        tasksAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener<Task>() {
            @Override
            public void onSuccess(List<Task> list) {
                listener.onSuccess(list);
            }

            @Override
            public void onFail(String message) {
                if (requestMethod == POST) {
                    listener.onFail(message + '\n' + "Failed to create task");
                } else {
                    listener.onFail(message + '\n' + "Failed to update task");
                }
            }
        });

        tasksAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, requestMethod);
    }

    public void deleteTask(TaskList taskList, Task task, final OnTasksLoadedListener listener) {
        TasksAsyncTask tasksAsyncTask = new TasksAsyncTask(task, taskList, mRepositoryLoadHelper,
                mFirebaseWebService, mTasksDbStore, mTasksCloudStore, null);

        tasksAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener<Task>() {
            @Override
            public void onSuccess(List<Task> list) {
                Log.v("Task successfully deleted");
            }

            @Override
            public void onFail(String message) {
                listener.onFail(message + '\n' + "Failed to delete task");
                Log.v("Failed to delete task");
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
            return super.doInBackground(requestMethods[0]);
        }

        @Override
        protected ResponseDto doGetRequest() {
            return mTasksCloudStore.getTasksFromServer(mTaskList);
        }

        @Override
        protected ResponseDto doPostRequest() {
            return mTasksCloudStore.addTaskOnServer(mTaskList, mTask);
        }

        @Override
        protected ResponseDto doPatchRequest() {
            return mTasksCloudStore.updateTaskOnServer(mTaskList, mTask);
        }

        @Override
        protected ResponseDto doDeleteRequest() {
            return mTasksCloudStore.deleteTaskOnServer(mTaskList, mTask);
        }

        @Override
        protected void refreshDbQuery(ResponseDto responseDto) {
            List<Task> tasks = parseTasksData(responseDto.getResponseData());
            updateDbQuery(tasks);
        }

        @Override
        protected void doInsertQuery(ResponseDto responseDto) {
            Task task;
            if (responseDto != null) {
                task = parseTask(responseDto.getResponseData());
            } else {
                task = mTask;
            }
            mTasksDbStore.addOrUpdateTasks(Collections.singletonList(task));
        }

        @Override
        protected void doUpdateQuery() {
            mTasksDbStore.addOrUpdateTasks(Collections.singletonList(mTask));
        }

        @Override
        protected void doDeleteQuery() {
            mTasksDbStore.deleteTask(mTask);
        }

        @Override
        protected List<Task> doSelectQuery() {
            return mTasksDbStore.getTasksFromTaskList(mTaskList.getId());
        }

        @Override
        protected void onServerError() {
            mListener.onFail("Tasks API server error");
        }

        @Override
        protected void retryGetResultFromServer(final FirebaseWebService.RequestMethods requestMethod) {
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

                            @Override
                            public void onFail(String message) {
                                mListener.onFail(message);
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

        private void updateDbQuery(List<Task> tasks) {
            mTasksDbStore.addOrUpdateTasks(tasks);
            mTasksDbStore.getTasksFromTaskList(mTaskList.getId());
        }

    }
}

