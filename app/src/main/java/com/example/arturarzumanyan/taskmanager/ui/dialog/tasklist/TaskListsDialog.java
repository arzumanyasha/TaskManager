package com.example.arturarzumanyan.taskmanager.ui.dialog.tasklist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;
import com.example.arturarzumanyan.taskmanager.ui.activity.BaseActivity;
import com.example.arturarzumanyan.taskmanager.ui.dialog.tasklist.mvp.TaskListsDialogContract;
import com.example.arturarzumanyan.taskmanager.ui.dialog.tasklist.mvp.TaskListsDialogPresenterImpl;

import static com.example.arturarzumanyan.taskmanager.ui.activity.intention.IntentionActivity.TASK_LISTS_KEY;

public class TaskListsDialog extends AppCompatDialogFragment implements TaskListsDialogContract.TaskListsDialogView {
    private EditText mEditTextTaskListTitle;
    private TaskListReadyListener taskListReadyListener;
    private TaskListsDialogContract.TaskListsDialogPresenter mTaskListsDialogPresenter;

    public TaskListsDialog() {
    }

    public static TaskListsDialog newInstance(TaskList taskList) {
        TaskListsDialog taskListsDialog = new TaskListsDialog();
        if (taskList != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(TASK_LISTS_KEY, taskList);
            taskListsDialog.setArguments(bundle);
        }
        return taskListsDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_tasklists, null);

        mTaskListsDialogPresenter = new TaskListsDialogPresenterImpl(this);

        final Bundle bundle = getArguments();

        int titleResId;
        if (bundle == null) {
            titleResId = R.string.task_lists_add_title;
        } else {
            titleResId = R.string.task_list_rename_title;
        }

        mEditTextTaskListTitle = view.findViewById(R.id.edit_text_tasklist_name);

        builder.setView(view)
                .setTitle(titleResId)
                .setNegativeButton(getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(getString(R.string.ok_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mTaskListsDialogPresenter.processOkButtonClick(bundle, mEditTextTaskListTitle.getText().toString());
                    }
                });

        mTaskListsDialogPresenter.processReceivedBundle(bundle);

        return builder.create();
    }

    @Override
    public void setTaskListInfoViews(TaskList taskList) {
        mEditTextTaskListTitle.setText(taskList.getTitle());

    }

    @Override
    public void onTaskListReady(TaskList taskList) {
        taskListReadyListener.onTaskListReady(taskList);
    }

    @Override
    public void onFail(String message) {
        ((BaseActivity) requireActivity()).onError(message);
    }

    public void setTaskListReadyListener(TaskListReadyListener listener) {
        this.taskListReadyListener = listener;
    }

    public interface TaskListReadyListener {
        void onTaskListReady(TaskList taskList);
    }
}
