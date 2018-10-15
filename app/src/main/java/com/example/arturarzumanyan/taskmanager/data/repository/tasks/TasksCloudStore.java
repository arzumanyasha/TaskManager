package com.example.arturarzumanyan.taskmanager.data.repository.tasks;

import android.content.Context;
import com.example.arturarzumanyan.taskmanager.data.repository.RepositoryLoadHelper;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.UserDataAsyncTask;
import com.example.arturarzumanyan.taskmanager.networking.util.TasksParser;

import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;

public class TasksCloudStore {
    private static final String BASE_TASKS_URL = "https://www.googleapis.com/tasks/v1/lists/";

    private RepositoryLoadHelper mRepositoryLoadHelper;
    private ArrayList<UserDataAsyncTask> mUserTasksAsyncTaskList = new ArrayList<>();

    private Context mContext;

    public TasksCloudStore(Context context) {
        this.mContext = context;
        mRepositoryLoadHelper = new RepositoryLoadHelper(mContext);
    }

    public void getTasksFromTaskList(TaskList taskList, final OnTaskCompletedListener listener) {
        String url = BASE_TASKS_URL + taskList.getTaskListId() + "/tasks?showHidden=true";
        final int taskListId = taskList.getId();
        mUserTasksAsyncTaskList.add(new UserDataAsyncTask());
        int position = mUserTasksAsyncTaskList.size() - 1;
        mRepositoryLoadHelper.requestUserData(mUserTasksAsyncTaskList.get(position), url);
        mUserTasksAsyncTaskList.get(position).setDataInfoLoadingListener(new UserDataAsyncTask.UserDataLoadingListener() {
            @Override
            public void onDataLoaded(String response) throws JSONException, ParseException {
                TasksParser tasksParser = new TasksParser();
                listener.onSuccess(tasksParser.parseTasks(response, taskListId));
            }
        });

    }

    public void addTask(Task task) {

    }

    public void updateTaskList(Task task) {

    }

    public void deleteTask(Task task) {

    }

    public interface OnTaskCompletedListener {
        void onSuccess(ArrayList<Task> taskArrayList);

        void onfail();
    }
}
