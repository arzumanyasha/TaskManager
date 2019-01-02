package com.example.arturarzumanyan.taskmanager.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.data.repository.tasklists.TaskListsRepository;
import com.example.arturarzumanyan.taskmanager.domain.TaskList;

import java.util.List;
import java.util.UUID;

import static com.example.arturarzumanyan.taskmanager.ui.activity.IntentionActivity.TASK_LISTS_KEY;

public class TaskListsDialog extends AppCompatDialogFragment {
    private EditText mEditTextTaskListTitle;

    public TaskListsDialog() {
        this.taskListReadyListener = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
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
                        String taskListName = mEditTextTaskListTitle.getText().toString();
                        TaskListsRepository taskListsRepository = new TaskListsRepository(getActivity());
                        if (!taskListName.isEmpty() && bundle != null) {
                            TaskList taskList = bundle.getParcelable(TASK_LISTS_KEY);
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
                                public void onFail() {

                                }
                            });
                        } else if (!taskListName.isEmpty() && bundle == null) {
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
                                public void onFail() {

                                }
                            });
                        }
                    }
                });

        if (bundle != null) {
            TaskList taskList = bundle.getParcelable(TASK_LISTS_KEY);
            mEditTextTaskListTitle.setText(taskList.getTitle());
        }
        return builder.create();
    }

    public interface TaskListReadyListener {
        void onTaskListReady(TaskList taskList);
    }

    public void setTaskListReadyListener(TaskListReadyListener listener) {
        this.taskListReadyListener = listener;
    }

    private TaskListReadyListener taskListReadyListener;
}
