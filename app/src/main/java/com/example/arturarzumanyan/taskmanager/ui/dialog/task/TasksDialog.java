package com.example.arturarzumanyan.taskmanager.ui.dialog.task;

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

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.domain.Task;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;
import com.example.arturarzumanyan.taskmanager.ui.activity.BaseActivity;
import com.example.arturarzumanyan.taskmanager.ui.dialog.task.mvp.TasksDialogContract;
import com.example.arturarzumanyan.taskmanager.ui.dialog.task.mvp.TasksDialogPresenterImpl;

import java.util.List;

import static com.example.arturarzumanyan.taskmanager.ui.activity.intention.IntentionActivity.TASKS_KEY;
import static com.example.arturarzumanyan.taskmanager.ui.activity.intention.IntentionActivity.TASK_LISTS_KEY;

public class TasksDialog extends AppCompatDialogFragment implements TasksDialogContract.TasksDialogView {
    private EditText mEditTextTaskName, mEditTextTaskDescription;
    private TextView mTextViewTaskDate;
    private TasksDialogContract.TasksDialogPresenter mTasksDialogPresenter;

    private TasksReadyListener tasksReadyListener;

    public TasksDialog() {
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

        mTasksDialogPresenter = new TasksDialogPresenterImpl(this);

        final Bundle bundle = getArguments();

        setViews(builder, view, bundle);

        setTaskDatePickerListener();

        mTasksDialogPresenter.processReceivedBundle(bundle);

        return builder.create();
    }

    private void setViews(AlertDialog.Builder builder, View view, final Bundle bundle) {
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
                        mTasksDialogPresenter.processOkButtonClick(bundle, mEditTextTaskName.getText().toString(),
                                mEditTextTaskDescription.getText().toString(),
                                mTextViewTaskDate.getText().toString().equals(getString(R.string.set_task_date_title)) ? null :
                                        mTextViewTaskDate.getText().toString());
                    }
                });
    }

    private void setTaskDatePickerListener() {
        final int year = DateUtils.getYear();
        final int month = DateUtils.getMonth();
        final int day = DateUtils.getDay();
        mTextViewTaskDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mTextViewTaskDate.setText(DateUtils.getStringDateFromInt(year, monthOfYear, dayOfMonth));
                        mTasksDialogPresenter.setTaskDate(DateUtils.getTaskDateFromString(
                                DateUtils.formatReversedYearMonthDayDate(mTextViewTaskDate.getText().toString())));
                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        });
    }

    @Override
    public void setTaskInfoViews(Task task) {
        mEditTextTaskName.setText(task.getName());
        mEditTextTaskDescription.setText(task.getDescription());
        if (task.getDate() != null) {
            mTextViewTaskDate.setText(DateUtils.formatReversedDayMonthYearDate(
                    DateUtils.getTaskDate(DateUtils.getTaskDateFromString(task.getDate()))));
        }
    }

    @Override
    public void onTaskReady(List<Task> tasks) {
        tasksReadyListener.onTasksReady(tasks);
    }

    @Override
    public void onFail(String message) {
        ((BaseActivity) requireActivity()).onError(message);
    }

    public void setTasksReadyListener(TasksReadyListener listener) {
        this.tasksReadyListener = listener;
    }

    public interface TasksReadyListener {
        void onTasksReady(List<Task> tasks);
    }
}
