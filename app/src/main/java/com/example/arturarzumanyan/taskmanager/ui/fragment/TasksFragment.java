package com.example.arturarzumanyan.taskmanager.ui.fragment;

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

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.data.repository.tasks.TasksRepository;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.util.Log;
import com.example.arturarzumanyan.taskmanager.ui.activity.BaseActivity;
import com.example.arturarzumanyan.taskmanager.ui.activity.IntentionActivity;
import com.example.arturarzumanyan.taskmanager.ui.adapter.TasksAdapter;
import com.example.arturarzumanyan.taskmanager.ui.dialog.TasksDialog;

import java.util.List;

import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.PATCH;
import static com.example.arturarzumanyan.taskmanager.ui.activity.IntentionActivity.TASKS_KEY;
import static com.example.arturarzumanyan.taskmanager.ui.activity.IntentionActivity.TASK_LISTS_KEY;

public class TasksFragment extends Fragment {
    private RecyclerView mTasksRecyclerView;
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
        mTasksRecyclerView = view.findViewById(R.id.recycler_tasks);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mTasksRecyclerView.setLayoutManager(layoutManager);

        if (mTasks == null) {
            loadTasks();
        } else {
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

    }

    public void loadTasks() {
        Log.v("Loading tasks");
        mTasksRepository.loadTasks(mTaskList, new TasksRepository.OnTasksLoadedListener() {
            @Override
            public void onSuccess(List<Task> taskArrayList) {
                mTasks = taskArrayList;
                setTasksAdapter(taskArrayList);
            }

            @Override
            public void onFail(String message) {
                ((BaseActivity) requireActivity()).onError(message);
            }
        });
        requireActivity().setTitle(mTaskList.getTitle());
    }

    private void setTasksAdapter(List<Task> taskArrayList) {
        mTasksAdapter = new TasksAdapter(taskArrayList, new TasksAdapter.OnItemClickListener() {
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
                updateTask(task);
            }
        });

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
                        mTasksAdapter.updateList(taskArrayList);
                    }

                    @Override
                    public void onFail(String message) {
                        ((BaseActivity) requireActivity()).onError(message);
                    }
                });
    }

    private void deleteTask(Task task) {
        mTasksRepository.deleteTask(mTaskList, task, new TasksRepository.OnTasksLoadedListener() {
            @Override
            public void onSuccess(List<Task> taskArrayList) {

            }

            @Override
            public void onFail(String message) {
                ((BaseActivity) requireActivity()).onError(message);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
