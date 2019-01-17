package com.example.arturarzumanyan.taskmanager.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.TaskListsRepository;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;

import java.util.List;
import java.util.UUID;

import static com.example.arturarzumanyan.taskmanager.ui.activity.IntentionActivity.TASK_LISTS_KEY;

public class TaskListsDialog extends AppCompatDialogFragment {
    private EditText mEditTextTaskListTitle;
    private TaskListReadyListener taskListReadyListener;

    public TaskListsDialog() {
        this.taskListReadyListener = null;
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

        final Bundle bundle = getArguments();

        mEditTextTaskListTitle = view.findViewById(R.id.edit_text_tasklist_name);

        builder.setView(view)
                .setTitle(R.string.task_lists_add_title)
                .setNegativeButton(getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(getString(R.string.ok_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addOrUpdateTaskList(bundle);
                    }
                });

        if (bundle != null) {
            TaskList taskList = bundle.getParcelable(TASK_LISTS_KEY);
            if (taskList != null) {
                mEditTextTaskListTitle.setText(taskList.getTitle());
            }
        }

        return builder.create();
    }

    private void addOrUpdateTaskList(Bundle bundle) {
        String taskListName = mEditTextTaskListTitle.getText().toString();
        TaskListsRepository taskListsRepository = new TaskListsRepository();
        if (!taskListName.isEmpty() && bundle != null) {
            TaskList taskList = bundle.getParcelable(TASK_LISTS_KEY);
            if (taskList != null) {
                updateTaskList(taskListsRepository, taskList, taskListName);
            }
        } else if (!taskListName.isEmpty()) {
            addTaskList(taskListsRepository, taskListName);
        }
    }

    private void updateTaskList(TaskListsRepository taskListsRepository, TaskList taskList, String taskListName){
        taskList.setTitle(taskListName);
        taskListsRepository.updateTaskList(taskList, new TaskListsRepository.OnTaskListsLoadedListener() {
            @Override
            public void onSuccess(List<TaskList> taskListArrayList) {

            }

            @Override
            public void onUpdate(List<TaskList> taskLists) {

            }

            @Override
            public void onSuccess(TaskList taskList) {
                taskListReadyListener.onTaskListReady(taskList);
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addTaskList(TaskListsRepository taskListsRepository, String taskListName) {
        TaskList taskList = new TaskList(UUID.randomUUID().toString(),
                taskListName);
        taskListsRepository.addTaskList(taskList, new TaskListsRepository.OnTaskListsLoadedListener() {
            @Override
            public void onSuccess(List<TaskList> taskListArrayList) {

            }

            @Override
            public void onUpdate(List<TaskList> taskLists) {

            }

            @Override
            public void onSuccess(TaskList taskList) {
                taskListReadyListener.onTaskListReady(taskList);
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public interface TaskListReadyListener {
        void onTaskListReady(TaskList taskList);
    }

    public void setTaskListReadyListener(TaskListReadyListener listener) {
        this.taskListReadyListener = listener;
    }
}
