package com.example.arturarzumanyan.taskmanager.data.repository.tasklists;

import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.specification.AllTaskListsSpecification;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.specification.TaskListFromIdSpecification;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.specification.TaskListsSpecification;
import com.example.arturarzumanyan.taskmanager.data.repository.tasks.TasksCloudStore;
import com.example.arturarzumanyan.taskmanager.data.repository.tasks.TasksDbStore;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.util.TaskListsParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;

import io.reactivex.Single;
import okhttp3.ResponseBody;

public class TaskListsRepository {
    private TaskListsCloudStore mTaskListsCloudStore;
    private TaskListsDbStore mTaskListsDbStore;
    private TasksDbStore mTasksDbStore;
    private TasksCloudStore mTasksCloudStore;
    private RepositoryLoadHelper mRepositoryLoadHelper;
    private TaskListsParser mTaskListsParser;

    public TaskListsRepository() {
        mTaskListsCloudStore = new TaskListsCloudStore();
        mTaskListsDbStore = new TaskListsDbStore();
        mTasksCloudStore = new TasksCloudStore();
        mTasksDbStore = new TasksDbStore();
        mRepositoryLoadHelper = new RepositoryLoadHelper();
        mTaskListsParser = new TaskListsParser();
    }

    public Single<List<TaskList>> loadTaskLists(TaskListsSpecification taskListsSpecification) {
        Single<List<TaskList>> taskListsSingle;
        if (RepositoryLoadHelper.isOnline()) {
            taskListsSingle = mTaskListsCloudStore.getTaskListsFromServer()
                    .filter(responseBody -> responseBody != null).toSingle()
                    .flatMap(this::updateDbQuery)
                    .flatMap(aBoolean -> {
                        if (aBoolean) {
                            return mTaskListsDbStore.getTaskLists(taskListsSpecification);
                        } else {
                            throw new IOException();
                        }
                    });
        } else {
            taskListsSingle = mTaskListsDbStore.getTaskLists(taskListsSpecification);
        }

        return taskListsSingle;
    }

    private Single<Boolean> updateDbQuery(ResponseBody responseBody) throws IOException {
        List<TaskList> taskLists = mTaskListsParser.parseTaskLists(responseBody.string());
        return mTaskListsDbStore.addOrUpdateTaskLists(taskLists);
    }

    public Single<TaskList> addTaskList(TaskList taskList) {
        AllTaskListsSpecification allTaskListsSpecification = new AllTaskListsSpecification();

        Single<TaskList> taskListsSingle;
        if (RepositoryLoadHelper.isOnline()) {
            taskListsSingle = mTaskListsCloudStore.addTaskListOnServer(taskList)
                    .filter(responseBody -> responseBody != null).toSingle()
                    .map(this::parseTaskList)
                    .flatMap(parsedTaskList -> mTaskListsDbStore.addOrUpdateTaskLists(Collections.singletonList(parsedTaskList)))
                    .flatMap(aBoolean -> {
                        if (aBoolean) {
                            return mTaskListsDbStore.getTaskLists(allTaskListsSpecification);
                        } else {
                            throw new IOException();
                        }
                    })
                    .map(taskLists -> taskLists.get(taskLists.size() - 1));
        } else {
            taskListsSingle = mTaskListsDbStore.addOrUpdateTaskLists(Collections.singletonList(taskList))
                    .flatMap(aBoolean -> {
                        if (aBoolean) {
                            return mTaskListsDbStore.getTaskLists(allTaskListsSpecification);
                        } else {
                            throw new IOException();
                        }
                    })
                    .map(taskLists -> taskLists.get(taskLists.size() - 1));
        }

        return taskListsSingle;
    }

    public Single<TaskList> updateTaskList(TaskList taskList) {
        TaskListFromIdSpecification taskListFromIdSpecification = new TaskListFromIdSpecification();
        taskListFromIdSpecification.setTaskListId(taskList.getTaskListId());

        Single<TaskList> taskListsSingle;
        if (RepositoryLoadHelper.isOnline()) {
            taskListsSingle = mTaskListsCloudStore.updateTaskListOnServer(taskList)
                    .filter(responseBody -> responseBody != null).toSingle()
                    .map(this::parseTaskList)
                    .flatMap(parsedTaskList -> mTaskListsDbStore.addOrUpdateTaskLists(Collections.singletonList(parsedTaskList)))
                    .flatMap(aBoolean -> {
                        if (aBoolean) {
                            return mTaskListsDbStore.getTaskLists(taskListFromIdSpecification);
                        } else {
                            throw new IOException();
                        }
                    })
                    .map(taskLists -> taskLists.get(0));
        } else {
            taskListsSingle = mTaskListsDbStore.addOrUpdateTaskLists(Collections.singletonList(taskList))
                    .flatMap(aBoolean -> {
                        if (aBoolean) {
                            return mTaskListsDbStore.getTaskLists(taskListFromIdSpecification);
                        } else {
                            throw new IOException();
                        }
                    })
                    .map(taskLists -> taskLists.get(0));
        }

        return taskListsSingle;
    }

    private TaskList parseTaskList(ResponseBody responseBody) throws IOException {
        TaskList taskList = null;
        if (responseBody != null) {
            taskList = mTaskListsParser.parseTaskList(responseBody.string());
        }
        return taskList;
    }

    public Single<TaskList> deleteTaskList(TaskList taskList, TaskListsSpecification taskListsSpecification) {
        Single<TaskList> taskListsSingle;
        if (RepositoryLoadHelper.isOnline()) {
            taskListsSingle = mTaskListsCloudStore.deleteTaskListOnServer(taskList)
                    .filter(response -> {
                        if (response.code() == HttpURLConnection.HTTP_NO_CONTENT) {
                            return true;
                        } else {
                            throw new IOException();
                        }
                    }).toSingle()
                    .flatMap(response -> mTaskListsDbStore.deleteTaskList(taskList))
                    .flatMap(aBoolean -> {
                        if (aBoolean) {
                            return mTaskListsDbStore.getTaskLists(taskListsSpecification);
                        } else {
                            throw new IOException();
                        }
                    })
                    .map(taskLists -> taskLists.get(0));
        } else {
            taskListsSingle = mTaskListsDbStore.deleteTaskList(taskList)
                    .flatMap(aBoolean -> {
                        if (aBoolean) {
                            return mTaskListsDbStore.getTaskLists(taskListsSpecification);
                        } else {
                            throw new IOException();
                        }
                    })
                    .map(taskLists -> taskLists.get(0));
        }

        return taskListsSingle;
    }
}
