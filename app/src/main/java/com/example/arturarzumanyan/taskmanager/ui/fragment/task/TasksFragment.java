package com.example.arturarzumanyan.taskmanager.ui.fragment.task;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.example.arturarzumanyan.taskmanager.BuildConfig;
import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.TaskManagerApp;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.util.Log;
import com.example.arturarzumanyan.taskmanager.ui.activity.BaseActivity;
import com.example.arturarzumanyan.taskmanager.ui.activity.intention.IntentionActivity;
import com.example.arturarzumanyan.taskmanager.ui.adapter.task.TasksAdapter;
import com.example.arturarzumanyan.taskmanager.ui.adapter.task.mvp.TasksListPresenter;
import com.example.arturarzumanyan.taskmanager.ui.fragment.task.mvp.contract.TasksContract;
import com.example.arturarzumanyan.taskmanager.ui.fragment.task.mvp.presenter.TasksPresenterImpl;
import com.squareup.leakcanary.RefWatcher;

import java.util.List;

import static com.example.arturarzumanyan.taskmanager.ui.activity.intention.IntentionActivity.TASKS_KEY;
import static com.example.arturarzumanyan.taskmanager.ui.activity.intention.IntentionActivity.TASK_LISTS_KEY;

public class TasksFragment extends Fragment implements TasksContract.TasksView {
    private RecyclerView mTasksRecyclerView;
    private ProgressBar mProgressBar;
    private TasksAdapter mTasksAdapter;
    private TasksContract.TasksPresenter mTasksPresenter;

    public TasksFragment() {
    }

    public static TasksFragment newInstance(TaskList taskList) {
        TasksFragment fragment = new TasksFragment();
        Bundle args = new Bundle();
        args.putParcelable(TASK_LISTS_KEY, taskList);
        fragment.setArguments(args);
        return fragment;
    }

    public void setTaskList(TaskList taskList) {
        setProgressBarVisible();
        mTasksPresenter.loadTasks(taskList);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v("onCreate");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("onCreateView");
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        mProgressBar = view.findViewById(R.id.tasks_progress_bar);
        mTasksRecyclerView = view.findViewById(R.id.recycler_tasks);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mTasksRecyclerView.setLayoutManager(layoutManager);

        if (mTasksPresenter == null) {
            mTasksPresenter = new TasksPresenterImpl(this);
        } else {
            mTasksPresenter.attachView(this);
            mTasksPresenter.processRetainedState();
        }
        mTasksPresenter.processReceivedBundle(getArguments());
        mTasksPresenter.processTasks();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.v("onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        ((IntentionActivity) requireActivity()).setTaskFragmentInteractionListener(new IntentionActivity.TaskFragmentInteractionListener() {
            @Override
            public void onTasksReady(List<Task> tasks) {
                updateTasksAdapter(tasks);
            }
        });

        ((IntentionActivity) requireActivity()).setTaskListFragmentInteractionListener(new IntentionActivity.TaskListFragmentInteractionListener() {
            @Override
            public void onTaskListReady(TaskList taskList) {
                updateAppTitle(taskList.getTitle());
            }
        });

        ((IntentionActivity) requireActivity()).setFloatingActionButtonVisible();
    }

    @Override
    public void setTasksAdapter(List<Task> taskArrayList) {
        TasksListPresenter tasksListPresenter = new TasksListPresenter(taskArrayList, new TasksListPresenter.OnItemClickListener() {
            @Override
            public void onItemDelete(Task task) {
                mTasksPresenter.deleteTask(task);
            }

            @Override
            public void onItemClick(Task task) {
                mTasksPresenter.processTaskDialog(task);
            }

            @Override
            public void onChangeItemExecuted(Task task) {
                mTasksPresenter.processItemExecutedStatus(task);
            }
        });
        mTasksAdapter = new TasksAdapter(tasksListPresenter);
        mTasksRecyclerView.setAdapter(mTasksAdapter);
    }

    @Override
    public void showDialog(DialogFragment dialogFragment) {
        dialogFragment.show(requireFragmentManager(), TASKS_KEY);
    }

    @Override
    public void updateTasksAdapter(List<Task> tasks) {
        if (isAdded()) {
            mTasksAdapter.updateList(tasks);
        }
    }

    @Override
    public void updateAppTitle(String title) {
        if (isAdded()) {
            requireActivity().setTitle(title);
        }
    }

    @Override
    public void setProgressBarVisible() {
        if (isAdded()) {
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
        }
    }

    @Override
    public void setProgressBarInvisible() {
        if (isAdded()) {
            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        }
    }

    @Override
    public void setScreenNotTouchable() {
        if (isAdded()) {
            requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    @Override
    public void onFail(String message) {
        if (isAdded()) {
            ((BaseActivity) requireActivity()).onError(message);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        ((IntentionActivity) requireActivity()).unsubscribeTaskListeners();
        if (mTasksAdapter != null) {
            mTasksAdapter.unsubscribe();
        }
        mTasksPresenter.unsubscribe();
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (BuildConfig.DEBUG) {
            RefWatcher refWatcher = TaskManagerApp.getRefWatcher(requireActivity());
            refWatcher.watch(this);
        }
    }
}
