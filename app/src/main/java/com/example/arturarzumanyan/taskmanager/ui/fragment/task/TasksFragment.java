package com.example.arturarzumanyan.taskmanager.ui.fragment.task;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.example.arturarzumanyan.taskmanager.data.repository.tasks.TasksRepository;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.util.Log;
import com.example.arturarzumanyan.taskmanager.ui.activity.BaseActivity;
import com.example.arturarzumanyan.taskmanager.ui.activity.intention.IntentionActivity;
import com.example.arturarzumanyan.taskmanager.ui.adapter.task.TasksAdapter;
import com.example.arturarzumanyan.taskmanager.ui.adapter.task.mvp.TasksListPresenter;
import com.example.arturarzumanyan.taskmanager.ui.dialog.task.TasksDialog;
import com.squareup.leakcanary.RefWatcher;

import java.util.List;

import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.PATCH;
import static com.example.arturarzumanyan.taskmanager.ui.activity.intention.IntentionActivity.TASKS_KEY;
import static com.example.arturarzumanyan.taskmanager.ui.activity.intention.IntentionActivity.TASK_LISTS_KEY;

public class TasksFragment extends Fragment {
    private RecyclerView mTasksRecyclerView;
    private ProgressBar mProgressBar;
    private TasksAdapter mTasksAdapter;

    private TasksRepository mTasksRepository;
    private TaskList mTaskList;
    private List<Task> mTasks;

    public TasksFragment() {
        mTasksRepository = new TasksRepository();
    }

    public static TasksFragment newInstance(TaskList taskList) {
        TasksFragment fragment = new TasksFragment();
        Bundle args = new Bundle();
        args.putParcelable(TASK_LISTS_KEY, taskList);
        fragment.setArguments(args);
        return fragment;
    }

    public void setTaskList(TaskList mTaskList) {
        this.mTaskList = mTaskList;
        mProgressBar.setVisibility(View.VISIBLE);
        loadTasks();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v("onCreate");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mTaskList = getArguments().getParcelable(TASK_LISTS_KEY);
        }
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

        if (mTasks == null) {
            loadTasks();
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            setTasksAdapter(mTasks);
            requireActivity().setTitle(mTaskList.getTitle());
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.v("onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        ((IntentionActivity) requireActivity()).setTaskFragmentInteractionListener(new IntentionActivity.TaskFragmentInteractionListener() {
            @Override
            public void onTasksReady(List<Task> tasks) {
                mTasksAdapter.updateList(tasks);
            }
        });

        ((IntentionActivity) requireActivity()).setTaskListFragmentInteractionListener(new IntentionActivity.TaskListFragmentInteractionListener() {
            @Override
            public void onTaskListReady(TaskList taskList) {
                requireActivity().setTitle(taskList.getTitle());
            }
        });

        ((IntentionActivity) requireActivity()).setFloatingActionButtonVisible();
    }

    public void loadTasks() {
        Log.v("Loading tasks");
        mTasksRepository.loadTasks(mTaskList, new TasksRepository.OnTasksLoadedListener() {
            @Override
            public void onSuccess(List<Task> taskArrayList) {
                mTasks = taskArrayList;
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                setTasksAdapter(taskArrayList);
            }

            @Override
            public void onFail(String message) {
                ((BaseActivity) requireActivity()).onError(message);
            }

            @Override
            public void onPermissionDenied() {
                /** To-do: add realization with start signInActivity*/
            }
        });
        requireActivity().setTitle(mTaskList.getTitle());
    }

    private void setTasksAdapter(List<Task> taskArrayList) {
        TasksListPresenter tasksListPresenter = new TasksListPresenter(taskArrayList, new TasksListPresenter.OnItemClickListener() {
            @Override
            public void onItemDelete(Task task) {
                deleteTask(task);
            }

            @Override
            public void onItemClick(Task task) {
                openTasksDialog(task);
            }

            @Override
            public void onChangeItemExecuted(Task task) {
                mProgressBar.setVisibility(View.VISIBLE);
                requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                updateTask(task);
            }
        });
        mTasksAdapter = new TasksAdapter(tasksListPresenter);
        /*mTasksAdapter = new TasksAdapter(taskArrayList, new TasksAdapter.OnItemClickListener() {
            @Override
            public void onItemDelete(final Task task) {
                deleteTask(task);
            }

            @Override
            public void onItemClick(Task task) {
                openTasksDialog(task);
            }

            @Override
            public void onChangeItemExecuted(final Task task) {
                mProgressBar.setVisibility(View.VISIBLE);
                requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                updateTask(task);
            }
        });*/

        mTasksRecyclerView.setAdapter(mTasksAdapter);
    }

    private void openTasksDialog(Task task) {
        TasksDialog tasksDialog = TasksDialog.newInstance(task, mTaskList);
        tasksDialog.setTasksReadyListener(new TasksDialog.TasksReadyListener() {
            @Override
            public void onTasksReady(List<Task> tasks) {
                mTasksAdapter.updateList(tasks);
            }
        });

        tasksDialog.show(requireFragmentManager(), TASKS_KEY);
    }

    private void updateTask(Task task) {
        mTasksRepository.addOrUpdateTask(mTaskList,
                task, PATCH, new TasksRepository.OnTasksLoadedListener() {
                    @Override
                    public void onSuccess(List<Task> taskArrayList) {
                        if (isVisible()) {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            mTasksAdapter.updateList(taskArrayList);
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        if (isVisible()) {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            ((BaseActivity) requireActivity()).onError(message);
                        }
                    }

                    @Override
                    public void onPermissionDenied() {
                        /** To-do: add realization with start signInActivity*/
                    }
                });
    }

    private void deleteTask(Task task) {
        mTasksRepository.deleteTask(mTaskList, task, new TasksRepository.OnTasksLoadedListener() {
            @Override
            public void onSuccess(List<Task> taskArrayList) {
                mTasksAdapter.updateList(taskArrayList);
            }

            @Override
            public void onFail(String message) {
                if (isVisible()) {
                    ((BaseActivity) requireActivity()).onError(message);
                }
            }

            @Override
            public void onPermissionDenied() {
                /** To-do: add realization with start signInActivity*/
            }
        });
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
