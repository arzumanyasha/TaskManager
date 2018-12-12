package com.example.arturarzumanyan.taskmanager.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.TaskListsRepository;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.specification.TaskListFromIdSpecification;
import com.example.arturarzumanyan.taskmanager.data.repository.tasks.TasksCloudStore;
import com.example.arturarzumanyan.taskmanager.data.repository.tasks.TasksDbStore;
import com.example.arturarzumanyan.taskmanager.data.repository.tasks.TasksRepository;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;
import com.example.arturarzumanyan.taskmanager.ui.activity.IntentionActivity;
import com.example.arturarzumanyan.taskmanager.ui.adapter.TasksAdapter;
import com.example.arturarzumanyan.taskmanager.ui.dialog.TasksDialog;

import java.util.ArrayList;
import java.util.List;

import static com.example.arturarzumanyan.taskmanager.ui.activity.IntentionActivity.TASKS_KEY;
import static com.example.arturarzumanyan.taskmanager.ui.activity.IntentionActivity.TASK_LISTS_KEY;

public class TasksFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    public static final String TASK_LIST_ID_KEY = "taskListId";
    public static final String TASK_LIST_STRING_ID_KEY = "stringTaskListId";
    public static final String TASK_LIST_TITLE_KEY = "taskListTitle";

    private RecyclerView mTasksRecyclerView;
    private TasksAdapter mTasksAdapter;

    private int mParam1;
    private String mParam2;
    private String mParam3;

    private OnFragmentInteractionListener mListener;

    public TasksFragment() {
    }

    public static TasksFragment newInstance(String param1, String param2, String param3) {
        TasksFragment fragment = new TasksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(TASK_LIST_ID_KEY);
            mParam2 = getArguments().getString(TASK_LIST_TITLE_KEY);
            mParam3 = getArguments().getString(TASK_LIST_STRING_ID_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        mTasksRecyclerView = view.findViewById(R.id.recycler_tasks);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mTasksRecyclerView.setLayoutManager(layoutManager);

        TasksRepository tasksRepository = new TasksRepository(getActivity());

        tasksRepository.loadTasks(new TaskList(mParam1, mParam3, mParam2), new TasksRepository.OnTasksLoadedListener() {
            @Override
            public void onSuccess(List<Task> taskArrayList) {
                setTasksAdapter(taskArrayList);
            }

            @Override
            public void onFail() {

            }
        });
        getActivity().setTitle(mParam2);

/*
        ((IntentionActivity) getActivity()).setTaskFragmentInteractionListener(new IntentionActivity.TaskFragmentInteractionListener() {
            @Override
            public void onTasksReady(ArrayList<Task> tasks) {
                mTasksAdapter.updateList(tasks);
            }
        });*/

        ((IntentionActivity) getActivity()).setTaskListFragmentInteractionListener(new IntentionActivity.TaskListFragmentInteractionListener() {
            @Override
            public void onTaskListReady(TaskList taskList) {
                getActivity().setTitle(taskList.getTitle());
            }
        });

    }

    private void setTasksAdapter(List<Task> taskArrayList) {
        mTasksAdapter = new TasksAdapter(taskArrayList, new TasksAdapter.OnItemClickListener() {
            @Override
            public void onItemDelete(final Task task) {
                TasksRepository tasksRepository = new TasksRepository(getActivity());
                tasksRepository.deleteTask(new TaskList(mParam1, mParam3, mParam2), task);
            }

            @Override
            public void onItemClick(Task task) {
                openTasksDialog(task);
            }

            @Override
            public void onChangeItemExecuted(final Task task) {
                TasksRepository tasksRepository = new TasksRepository(getActivity());
                tasksRepository.updateTask(new TaskList(mParam1, mParam3, mParam2),
                        task, new TasksRepository.OnTasksLoadedListener() {
                            @Override
                            public void onSuccess(List<Task> taskArrayList) {
                                mTasksAdapter.updateList(taskArrayList);
                            }

                            @Override
                            public void onFail() {

                            }
                        });
            }
        });

        mTasksRecyclerView.setAdapter(mTasksAdapter);
    }


    private void openTasksDialog(Task task) {
        TasksDialog tasksDialog = new TasksDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(TASKS_KEY, task);

        TaskList taskList = new TaskList(mParam1, mParam3, mParam2);
        bundle.putParcelable(TASK_LISTS_KEY, taskList);
        /*bundle.putInt(TASK_LIST_ID_KEY, mParam1);
        bundle.putString(TASK_LIST_STRING_ID_KEY, mParam3);*/
        tasksDialog.setArguments(bundle);
        tasksDialog.setTasksReadyListener(new TasksDialog.TasksReadyListener() {
            @Override
            public void onTasksReady(List<Task> tasks) {
                mTasksAdapter.updateList(tasks);
            }
        });
        tasksDialog.show(getFragmentManager(), TASKS_KEY);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
