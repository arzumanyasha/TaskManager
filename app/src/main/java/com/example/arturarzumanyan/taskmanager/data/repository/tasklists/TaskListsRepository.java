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
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.util.Log;
import com.example.arturarzumanyan.taskmanager.networking.util.TaskListsParser;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.DELETE;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.GET;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.PATCH;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.POST;

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
                mFirebaseWebService, mTaskListsDbStore, mTaskListsCloudStore,
                mTasksDbStore, mTasksCloudStore, taskListsSpecification, listener);

        taskListsAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener<TaskList>() {
            @Override
            public void onSuccess(List<TaskList> list) {
                Log.v("TaskLists loaded successfully");
                listener.onSuccess(list);
            }

            @Override
            public void onFail(String message) {
                listener.onFail(message + '\n' + "Failed to get events");
            }
        });

        taskListsAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, GET);
    }

    public void addTaskList(TaskList taskList, final OnTaskListsLoadedListener listener) {
        AllTaskListsSpecification allTaskListsSpecification = new AllTaskListsSpecification();

        TaskListsAsyncTask taskListsAsyncTask = new TaskListsAsyncTask(taskList, mContext,
                mRepositoryLoadHelper, mFirebaseWebService, mTaskListsDbStore, mTaskListsCloudStore,
                mTasksDbStore, null, allTaskListsSpecification, null);

        taskListsAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener<TaskList>() {
            @Override
            public void onSuccess(List<TaskList> list) {
                listener.onSuccess(list.get(list.size() - 1));
            }

            @Override
            public void onFail(String message) {
                listener.onFail(message + '\n' + "Failed to create task list");
            }
        });
        taskListsAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, POST);
    }

    public void updateTaskList(TaskList taskList, final OnTaskListsLoadedListener listener) {
        TaskListFromIdSpecification taskListFromIdSpecification = new TaskListFromIdSpecification();
        taskListFromIdSpecification.setTaskListId(taskList.getId());

        TaskListsAsyncTask taskListsAsyncTask = new TaskListsAsyncTask(taskList, mContext,
                mRepositoryLoadHelper, mFirebaseWebService, mTaskListsDbStore, mTaskListsCloudStore,
                mTasksDbStore, null, taskListFromIdSpecification, null);

        taskListsAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener<TaskList>() {
            @Override
            public void onSuccess(List<TaskList> list) {
                listener.onSuccess(list.get(0));
            }

            @Override
            public void onFail(String message) {
                listener.onFail(message + '\n' + "Failed to update task list");
            }
        });
        taskListsAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, PATCH);
    }

    public void deleteTaskList(TaskList taskList, final OnTaskListsLoadedListener listener) {
        TaskListFromIdSpecification taskListFromIdSpecification = new TaskListFromIdSpecification();
        taskListFromIdSpecification.setTaskListId(taskList.getId());

        TaskListsAsyncTask taskListsAsyncTask = new TaskListsAsyncTask(taskList, mContext,
                mRepositoryLoadHelper, mFirebaseWebService, mTaskListsDbStore, mTaskListsCloudStore,
                mTasksDbStore, null, taskListFromIdSpecification, null);

        taskListsAsyncTask.setDataInfoLoadingListener(new BaseDataLoadingAsyncTask.UserDataLoadingListener<TaskList>() {
            @Override
            public void onSuccess(List<TaskList> list) {
                listener.onSuccess(list);
            }

            @Override
            public void onFail(String message) {
                listener.onFail(message + '\n' + "Failed to delete task list");
            }
        });
        taskListsAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, DELETE);
    }

    public interface OnTaskListsLoadedListener {
        void onSuccess(List<TaskList> taskListArrayList);

        void onUpdate(List<TaskList> taskLists);

        void onSuccess(TaskList taskList);

        void onFail(String message);
    }

    public static class TaskListsAsyncTask extends BaseDataLoadingAsyncTask<TaskList> {

        private TaskList mTaskList;
        private WeakReference<Context> mContextWeakReference;
        private RepositoryLoadHelper mRepositoryLoadHelper;
        private FirebaseWebService mFirebaseWebService;
        private TaskListsDbStore mTaskListsDbStore;
        private TaskListsCloudStore mTaskListsCloudStore;
        private TasksDbStore mTasksDbStore;
        private TasksCloudStore mTasksCloudStore;
        private TaskListsSpecification mTaskListsSpecification;
        private OnTaskListsLoadedListener mListener;

        public TaskListsAsyncTask(TaskList taskList,
                                  Context context,
                                  RepositoryLoadHelper repositoryLoadHelper,
                                  FirebaseWebService firebaseWebService,
                                  TaskListsDbStore taskListsDbStore,
                                  TaskListsCloudStore taskListsCloudStore,
                                  TasksDbStore tasksDbStore,
                                  TasksCloudStore tasksCloudStore,
                                  TaskListsSpecification taskListsSpecification,
                                  OnTaskListsLoadedListener listener) {
            this.mTaskList = taskList;
            this.mContextWeakReference = new WeakReference<>(context);
            this.mRepositoryLoadHelper = repositoryLoadHelper;
            this.mFirebaseWebService = firebaseWebService;
            this.mTaskListsDbStore = taskListsDbStore;
            this.mTaskListsCloudStore = taskListsCloudStore;
            this.mTasksDbStore = tasksDbStore;
            this.mTasksCloudStore = tasksCloudStore;
            this.mTaskListsSpecification = taskListsSpecification;
            this.mListener = listener;
        }

        @Override
        protected List<TaskList> doInBackground(final FirebaseWebService.RequestMethods... requestMethods) {
            return super.doInBackground(requestMethods[0]);
        }

        @Override
        protected ResponseDto doGetRequest() {
            return mTaskListsCloudStore.getTaskListsFromServer();
        }

        @Override
        protected ResponseDto doPostRequest() {
            return mTaskListsCloudStore.addTaskListOnServer(mTaskList);
        }

        @Override
        protected ResponseDto doPatchRequest() {
            return mTaskListsCloudStore.updateTaskListOnServer(mTaskList);
        }

        @Override
        protected ResponseDto doDeleteRequest() {
            return mTaskListsCloudStore.deleteTaskListOnServer(mTaskList);
        }

        @Override
        protected List<TaskList> doSelectQuery() {
            return mTaskListsDbStore.getTaskLists(mTaskListsSpecification);
        }

        @Override
        protected void refreshDbQuery(ResponseDto responseDto) {
            List<TaskList> taskLists = parseTaskListsData(responseDto.getResponseData());
            updateDbQuery(taskLists);
        }

        @Override
        protected void doInsertQuery(ResponseDto responseDto) {
            TaskList taskList;
            if (responseDto != null) {
                taskList = parseTaskList(responseDto.getResponseData());
            } else {
                taskList = mTaskList;
            }
            mTaskListsDbStore.addOrUpdateTaskLists(Collections.singletonList(taskList));
        }

        @Override
        protected void doUpdateQuery() {
            mTaskListsDbStore.addOrUpdateTaskLists(Collections.singletonList(mTaskList));
        }

        @Override
        protected void doDeleteQuery() {
            mTaskListsDbStore.deleteTaskList(mTaskList);
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
                    Log.v("Access token refreshed successfully");
                    TaskListsAsyncTask taskListsAsyncTask = new TaskListsAsyncTask(null,
                            mContextWeakReference.get(), mRepositoryLoadHelper, mFirebaseWebService,
                            mTaskListsDbStore, mTaskListsCloudStore, mTasksDbStore, mTasksCloudStore,
                            mTaskListsSpecification, mListener);
                    taskListsAsyncTask.setDataInfoLoadingListener(new UserDataLoadingListener<TaskList>() {
                        @Override
                        public void onSuccess(List<TaskList> list) {
                            Log.v("TaskLists loading retried successfully");
                            mListener.onUpdate(list);
                        }

                        @Override
                        public void onFail(String message) {
                            mListener.onFail(message);
                        }
                    });

                    taskListsAsyncTask.executeOnExecutor(SERIAL_EXECUTOR, requestMethod);
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

        /*private void loadTasks(TaskList taskList, int taskListNumber) {
            ResponseDto responseDto = mTasksCloudStore.getTasksFromServer(taskList);

            int responseCode = responseDto.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK ||
                    responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                TasksParser tasksParser = new TasksParser();
                updateTasksInDbQuery(tasksParser.parseTasks(responseDto.getResponseData(), taskListNumber));
            }
        }

        private void updateTasksInDbQuery(List<Task> tasks) {
            mTasksDbStore.addOrUpdateTasks(tasks);
        }*/

        private void updateDbQuery(List<TaskList> events) {
            mTaskListsDbStore.addOrUpdateTaskLists(events);
            mTaskListsDbStore.getTaskLists(mTaskListsSpecification);
        }

    }
}
