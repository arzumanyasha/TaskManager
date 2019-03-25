package com.example.arturarzumanyan.taskmanager.data.repository.tasks;

import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.util.Log;
import com.example.arturarzumanyan.taskmanager.networking.util.TasksParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;

import io.reactivex.Single;
import okhttp3.ResponseBody;

public class TasksRepository {
    private TasksDbStore mTasksDbStore;
    private TasksCloudStore mTasksCloudStore;
    private TasksParser mTasksParser;

    public TasksRepository() {
        mTasksCloudStore = new TasksCloudStore();
        mTasksDbStore = new TasksDbStore();
        mTasksParser = new TasksParser();
    }

    public Single<List<Task>> loadTasks(TaskList taskList) {
        Log.v("Loading tasks from tasklist");
        Single<List<Task>> tasksSingle;
        if (RepositoryLoadHelper.isOnline()) {
            tasksSingle = mTasksCloudStore.getTasksFromServer(taskList)
                    .filter(responseBody -> responseBody != null).toSingle()
                    .flatMap(responseBody -> updateDbQuery(responseBody, taskList.getId()))
                    .flatMap(aBoolean -> {
                        if (aBoolean) {
                            return mTasksDbStore.getTasksFromTaskList(taskList.getId());
                        } else {
                            throw new IOException();
                        }
                    });
        } else {
            tasksSingle = mTasksDbStore.getTasksFromTaskList(taskList.getId());
        }

        return tasksSingle;
    }

    private Single<Boolean> updateDbQuery(ResponseBody responseBody, int taskListId) throws IOException {
        List<Task> tasks = mTasksParser.parseTasks(responseBody.string(), taskListId);
        return mTasksDbStore.addOrUpdateTasks(tasks);
    }

    public Single<List<Task>> addTask(TaskList taskList, Task task) {
        Single<List<Task>> tasksSingle;
        if (RepositoryLoadHelper.isOnline()) {
            tasksSingle = mTasksCloudStore.addTaskOnServer(taskList, task)
                    .filter(responseBody -> responseBody != null).toSingle()
                    .map(responseBody -> parseTask(responseBody, taskList.getId()))
                    .flatMap(parsedTask -> mTasksDbStore.addOrUpdateTasks(Collections.singletonList(parsedTask)))
                    .flatMap(aBoolean -> {
                        if (aBoolean) {
                            return mTasksDbStore.getTasksFromTaskList(taskList.getId());
                        } else {
                            throw new IOException();
                        }
                    });
        } else {
            tasksSingle = mTasksDbStore.addOrUpdateTasks(Collections.singletonList(task))
                    .flatMap(aBoolean -> {
                        if (aBoolean) {
                            return mTasksDbStore.getTasksFromTaskList(taskList.getId());
                        } else {
                            throw new IOException();
                        }
                    });
        }

        return tasksSingle;
    }

    public Single<List<Task>> updateTask(TaskList taskList, Task task) {
        Single<List<Task>> tasksSingle;
        if (RepositoryLoadHelper.isOnline()) {
            tasksSingle = mTasksCloudStore.updateTaskOnServer(taskList, task)
                    .filter(responseBody -> responseBody != null).toSingle()
                    .map(responseBody -> parseTask(responseBody, taskList.getId()))
                    .flatMap(parsedTask -> mTasksDbStore.addOrUpdateTasks(Collections.singletonList(parsedTask)))
                    .flatMap(aBoolean -> {
                        if (aBoolean) {
                            return mTasksDbStore.getTasksFromTaskList(taskList.getId());
                        } else {
                            throw new IOException();
                        }
                    });
        } else {
            tasksSingle = mTasksDbStore.addOrUpdateTasks(Collections.singletonList(task))
                    .flatMap(aBoolean -> {
                        if (aBoolean) {
                            return mTasksDbStore.getTasksFromTaskList(taskList.getId());
                        } else {
                            throw new IOException();
                        }
                    });
        }

        return tasksSingle;
    }

    private Task parseTask(ResponseBody responseBody, int taskListId) throws IOException {
        Task task = null;
        if (responseBody != null) {
            task = mTasksParser.parseTask(responseBody.string(), taskListId);
        }
        return task;
    }

    public Single<List<Task>> deleteTask(TaskList taskList, Task task) {
        Single<List<Task>> tasksSingle;
        if (RepositoryLoadHelper.isOnline()) {
            tasksSingle = mTasksCloudStore.deleteTaskOnServer(taskList, task)
                    .filter(response -> {
                        if (response.code() == HttpURLConnection.HTTP_NO_CONTENT) {
                            return true;
                        } else {
                            throw new IOException();
                        }
                    }).toSingle()
                    .flatMap(responseBody -> mTasksDbStore.deleteTask(task))
                    .flatMap(aBoolean -> {
                        if (aBoolean) {
                            return mTasksDbStore.getTasksFromTaskList(taskList.getId());
                        } else {
                            throw new IOException();
                        }
                    });
        } else {
            tasksSingle = mTasksDbStore.deleteTask(task)
                    .flatMap(aBoolean -> {
                        if (aBoolean) {
                            return mTasksDbStore.getTasksFromTaskList(taskList.getId());
                        } else {
                            throw new IOException();
                        }
                    });
        }

        return tasksSingle;
    }
}

