package com.example.arturarzumanyan.taskmanager.ui.dialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.data.repository.tasks.TasksRepository;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.PATCH;
import static com.example.arturarzumanyan.taskmanager.auth.FirebaseWebService.RequestMethods.POST;
import static com.example.arturarzumanyan.taskmanager.ui.activity.IntentionActivity.TASKS_KEY;
import static com.example.arturarzumanyan.taskmanager.ui.activity.IntentionActivity.TASK_LISTS_KEY;

public class TasksDialog extends AppCompatDialogFragment {
    private EditText mEditTextTaskName, mEditTextTaskDescription;
    private TextView mTextViewTaskDate;
    private Date taskDate;

    public TasksDialog() {
        this.tasksReadyListener = null;
    }

    public static TasksDialog newInstance(Task task, TaskList taskList) {
        TasksDialog tasksDialog = new TasksDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(TASK_LISTS_KEY, taskList);
        if (task != null) {
            bundle.putParcelable(TASKS_KEY, task);
        }
        tasksDialog.setArguments(bundle);
        return tasksDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_tasks, null);

        final Bundle bundle = getArguments();

        mEditTextTaskName = view.findViewById(R.id.edit_text_task_name);
        mEditTextTaskDescription = view.findViewById(R.id.edit_text_task_description);
        mTextViewTaskDate = view.findViewById(R.id.text_task_date);

        builder.setView(view)
                .setTitle(getString(R.string.tasks_title))
                .setNegativeButton(getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(getString(R.string.ok_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TasksRepository tasksRepository = new TasksRepository(getActivity());
                        String taskName = mEditTextTaskName.getText().toString();
                        if (bundle != null) {
                            if (!taskName.isEmpty() && (bundle.getParcelable(TASKS_KEY) != null)) {
                                Task task = bundle.getParcelable(TASKS_KEY);
                                TaskList taskList = bundle.getParcelable(TASK_LISTS_KEY);
                                if (task != null) {
                                    task.setName(taskName);
                                    task.setDescription(mEditTextTaskDescription.getText().toString());
                                    task.setDate(taskDate);
                                    tasksRepository.addOrUpdateTask(taskList, task, PATCH, new TasksRepository.OnTasksLoadedListener() {
                                        @Override
                                        public void onSuccess(List<Task> taskArrayList) {
                                            tasksReadyListener.onTasksReady(taskArrayList);
                                        }

                                        @Override
                                        public void onFail(String message) {
                                            Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            } else if (!taskName.isEmpty() && (bundle.getParcelable(TASKS_KEY) == null)) {
                                String taskId = UUID.randomUUID().toString();
                                String taskDescription = mEditTextTaskDescription.getText().toString();
                                int isExecuted = 0;

                                TaskList taskList = bundle.getParcelable(TASK_LISTS_KEY);
                                if (taskList != null) {
                                    int taskListId = taskList.getId();
                                    Date date = null;

                                    if (!mTextViewTaskDate.getText().equals(getString(R.string.set_task_date_title))) {
                                        date = DateUtils.getTaskDateFromString(mTextViewTaskDate.getText().toString());
                                    }

                                    Task task = new Task(taskId,
                                            taskName,
                                            taskDescription,
                                            isExecuted,
                                            taskListId,
                                            date
                                    );
                                    tasksRepository.addOrUpdateTask(taskList, task, POST, new TasksRepository.OnTasksLoadedListener() {
                                        @Override
                                        public void onSuccess(List<Task> taskArrayList) {
                                            tasksReadyListener.onTasksReady(taskArrayList);
                                        }

                                        @Override
                                        public void onFail(String message) {
                                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }
                    }
                });

        final int year = DateUtils.getYear();
        final int month = DateUtils.getMonth();
        final int day = DateUtils.getDay();
        mTextViewTaskDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mTextViewTaskDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        taskDate = DateUtils.getTaskDateFromString(mTextViewTaskDate.getText().toString());
                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        });

        if (bundle != null) {
            if (bundle.getParcelable(TASKS_KEY) != null) {
                Task task = bundle.getParcelable(TASKS_KEY);
                if (task != null) {
                    mEditTextTaskName.setText(task.getName());
                    mEditTextTaskDescription.setText(task.getDescription());
                    if (task.getDate() != null) {
                        mTextViewTaskDate.setText(DateUtils.getTaskDate(DateUtils.formatTaskDate(task.getDate())));
                    }
                }
            }
        }

        return builder.create();
    }

    public interface TasksReadyListener {
        void onTasksReady(List<Task> tasks);
    }

    public void setTasksReadyListener(TasksReadyListener listener) {
        this.tasksReadyListener = listener;
    }

    private TasksReadyListener tasksReadyListener;
}
