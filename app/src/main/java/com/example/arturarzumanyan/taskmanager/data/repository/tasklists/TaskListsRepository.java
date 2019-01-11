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
            public void onFail() {
                listener.onFail();
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
            public void onFail() {
                listener.onFail();
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
            public void onFail() {
                listener.onFail();
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
                listener.onSuccess(list.get(0));
            }

            @Override
            public void onFail() {
                listener.onFail();
            }
        });
        taskListsAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, DELETE);
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
            if (mRepositoryLoadHelper.isOnline()) {
                Log.v("Loading tasklists...");

                ResponseDto responseDto = getResponseFromServer(requestMethods[0]);

                int responseCode;
                if (responseDto != null) {
                    responseCode = responseDto.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        retryGetResultFromServer(requestMethods[0]);
                    } else {
                        if (responseCode == HttpURLConnection.HTTP_OK ||
                                responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                            Log.v("TaskLists loaded successfully");
                            if (requestMethods[0] == POST) {
                                dbQuery(parseTaskList(responseDto.getResponseData()), requestMethods[0]);
                            } else if (requestMethods[0] == PATCH) {
                                dbQuery(mTaskList, requestMethods[0]);
                            } else if (requestMethods[0] == GET) {
                                List<TaskList> taskLists = parseTaskListsData(responseDto.getResponseData());
                                updateDbQuery(taskLists);

                                for (int i = 0; i < taskLists.size(); i++) {
                                    loadTasks(taskLists.get(i), i + 1);
                                }

                            } else {
                                dbQuery(mTaskList, requestMethods[0]);
                                return Collections.singletonList(mTaskList);
                            }
                        }
                    }
                } else {
                    mListener.onFail();
                }
            } else {
                if (requestMethods[0] != GET) {
                    dbQuery(mTaskList, requestMethods[0]);
                } else {
                    return mTaskListsDbStore.getTaskLists(mTaskListsSpecification);
                }
            }

            return mTaskListsDbStore.getTaskLists(mTaskListsSpecification);
        }

        private ResponseDto getResponseFromServer(FirebaseWebService.RequestMethods requestMethod) {
            switch (requestMethod) {
                case GET: {
                    return mTaskListsCloudStore.getTaskListsFromServer();
                }
                case POST: {
                    return mTaskListsCloudStore.addTaskListOnServer(mTaskList);
                }
                case PATCH: {
                    return mTaskListsCloudStore.updateTaskListOnServer(mTaskList);
                }
                case DELETE: {
                    return mTaskListsCloudStore.deleteTaskListOnServer(mTaskList);
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
                        public void onFail() {
                            mListener.onFail();
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

        private void loadTasks(TaskList taskList, int taskListNumber) {
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
        }

        private void dbQuery(TaskList taskList, FirebaseWebService.RequestMethods requestMethod) {
            if (requestMethod == POST || requestMethod == PATCH) {
                mTaskListsDbStore.addOrUpdateTaskLists(Collections.singletonList(taskList));
            } else if (requestMethod == FirebaseWebService.RequestMethods.DELETE) {
                mTaskListsDbStore.deleteTaskList(taskList);
            }
        }

        private void updateDbQuery(List<TaskList> events) {
            mTaskListsDbStore.addOrUpdateTaskLists(events);
            mTaskListsDbStore.getTaskLists(mTaskListsSpecification);
        }

    }
}
