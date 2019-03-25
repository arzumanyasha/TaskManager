package com.example.arturarzumanyan.taskmanager.data.repository.tasklists;

import com.example.arturarzumanyan.taskmanager.data.db.DbHelper;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.specification.TaskListsSpecification;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;

import java.util.List;

import io.reactivex.Single;

public class TaskListsDbStore {

    TaskListsDbStore() {
    }

    public Single<List<TaskList>> getTaskLists(TaskListsSpecification taskListsSpecification) {
        return Single.fromCallable(() -> DbHelper.getDbHelperInstance().getTaskLists(taskListsSpecification));
    }

    public Single<Boolean> addOrUpdateTaskLists(List<TaskList> taskLists) {
        return Single.fromCallable(() -> DbHelper.getDbHelperInstance().addOrUpdateTaskLists(taskLists));
    }

    public Single<Boolean> deleteTaskList(TaskList taskList) {
        return Single.fromCallable(() -> DbHelper.getDbHelperInstance().deleteTaskList(taskList));
    }
}
