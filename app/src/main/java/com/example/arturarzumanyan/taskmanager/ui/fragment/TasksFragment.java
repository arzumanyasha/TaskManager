package com.example.arturarzumanyan.taskmanager.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.data.repository.tasks.TasksRepository;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
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

    private TaskList mTaskList;

    private OnFragmentInteractionListener mListener;

    public TasksFragment() {
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
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTaskList = getArguments().getParcelable(TASK_LISTS_KEY);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        mTasksRecyclerView = view.findViewById(R.id.recycler_tasks);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            mTasksRecyclerView.setLayoutManager(layoutManager);

            TasksRepository tasksRepository = new TasksRepository(getActivity());

            tasksRepository.loadTasks(mTaskList, new TasksRepository.OnTasksLoadedListener() {
                @Override
                public void onSuccess(List<Task> taskArrayList) {
                    setTasksAdapter(taskArrayList);
                }

                @Override
                public void onFail(String message) {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                }
            });
            getActivity().setTitle(mTaskList.getTitle());


            ((IntentionActivity) getActivity()).setTaskFragmentInteractionListener(new IntentionActivity.TaskFragmentInteractionListener() {
                @Override
                public void onTasksReady(List<Task> tasks) {
                    mTasksAdapter.updateList(tasks);
                }
            });

            ((IntentionActivity) getActivity()).setTaskListFragmentInteractionListener(new IntentionActivity.TaskListFragmentInteractionListener() {
                @Override
                public void onTaskListReady(TaskList taskList) {
                    getActivity().setTitle(taskList.getTitle());
                }
            });
        }
    }

    private void setTasksAdapter(List<Task> taskArrayList) {
        mTasksAdapter = new TasksAdapter(taskArrayList, new TasksAdapter.OnItemClickListener() {
            @Override
            public void onItemDelete(final Task task) {
                TasksRepository tasksRepository = new TasksRepository(getActivity());
                tasksRepository.deleteTask(mTaskList, task, new TasksRepository.OnTasksLoadedListener() {
                    @Override
                    public void onSuccess(List<Task> taskArrayList) {

                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onItemClick(Task task) {
                openTasksDialog(task);
            }

            @Override
            public void onChangeItemExecuted(final Task task) {
                TasksRepository tasksRepository = new TasksRepository(getActivity());
                tasksRepository.addOrUpdateTask(mTaskList,
                        task, PATCH, new TasksRepository.OnTasksLoadedListener() {
                            @Override
                            public void onSuccess(List<Task> taskArrayList) {
                                mTasksAdapter.updateList(taskArrayList);
                            }

                            @Override
                            public void onFail(String message) {
                                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            }
                        });
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
        if (getFragmentManager() != null) {
            tasksDialog.show(getFragmentManager(), TASKS_KEY);
        }
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
