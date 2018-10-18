package com.example.arturarzumanyan.taskmanager.ui.dialog;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.data.repository.tasks.TasksRepository;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.UUID;

import static com.example.arturarzumanyan.taskmanager.ui.activity.IntentionActivity.TASKS_KEY;
import static com.example.arturarzumanyan.taskmanager.ui.fragment.TasksFragment.TASK_LIST_ID_KEY;

public class TasksDialog extends AppCompatDialogFragment {
    private EditText mEditTextTaskName, mEditTextTaskDescription;
    private TextView mTextViewTaskDate;
    private Date taskDate;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_tasks, null);

        final Bundle bundle = getArguments();

        mEditTextTaskName = view.findViewById(R.id.editTextTaskName);
        mEditTextTaskDescription = view.findViewById(R.id.editTextTaskDescription);
        mTextViewTaskDate = view.findViewById(R.id.textViewTaskDate);

        builder.setView(view)
                .setTitle("Tasks")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TasksRepository tasksRepository = new TasksRepository(getActivity());
                        if (!mEditTextTaskName.getText().toString().isEmpty() && (bundle.getParcelable(TASKS_KEY) != null)) {
                            Task task = bundle.getParcelable(TASKS_KEY);
                            task.setName(mEditTextTaskName.getText().toString());
                            task.setDescription(mEditTextTaskDescription.getText().toString());
                            task.setDate(taskDate);
                            tasksRepository.updateTask(task);
                        } else if (!mEditTextTaskName.getText().toString().isEmpty() && (bundle.getParcelable(TASKS_KEY) == null)) {
                            Task task;
                            if (mTextViewTaskDate.getText().equals("Set task date")) {
                                task = new Task(UUID.randomUUID().toString(),
                                        mEditTextTaskName.getText().toString(),
                                        mEditTextTaskDescription.getText().toString(),
                                        0,
                                        bundle.getInt(TASK_LIST_ID_KEY)
                                );
                            } else {
                                task = new Task(UUID.randomUUID().toString(),
                                        mEditTextTaskName.getText().toString(),
                                        mEditTextTaskDescription.getText().toString(),
                                        0,
                                        bundle.getInt(TASK_LIST_ID_KEY),
                                        DateUtils.getTaskDateFromString(mTextViewTaskDate.getText().toString())
                                );
                            }
                            tasksRepository.addTask(task);

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

        if (bundle.getParcelable(TASKS_KEY) != null) {
            Task task = bundle.getParcelable(TASKS_KEY);
            mEditTextTaskName.setText(task.getName());
            mEditTextTaskDescription.setText(task.getDescription());
            if (task.getDate() != null) {
                mTextViewTaskDate.setText(DateUtils.getTaskDate(DateUtils.formatTaskDate(task.getDate())));
            }
        }
        return builder.create();
    }
}
