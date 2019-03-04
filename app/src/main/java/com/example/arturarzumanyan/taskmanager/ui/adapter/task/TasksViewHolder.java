package com.example.arturarzumanyan.taskmanager.ui.adapter.task;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.ui.adapter.task.mvp.TaskRowView;
import com.example.arturarzumanyan.taskmanager.ui.fragment.task.mvp.TasksContract;

public class TasksViewHolder extends RecyclerView.ViewHolder implements TaskRowView {
    private TasksContract.TasksPresenter tasksListPresenter;
    private TextView taskName;
    private TextView taskDescription;
    private ImageView taskDelete;
    private CheckBox isExecutedCheckBox;

    TasksViewHolder(View view, TasksContract.TasksPresenter tasksListPresenter) {
        super(view);
        this.tasksListPresenter = tasksListPresenter;
        taskName = view.findViewById(R.id.text_task_name);
        taskDescription = view.findViewById(R.id.text_task_description);
        taskDelete = view.findViewById(R.id.image_delete);
        isExecutedCheckBox = view.findViewById(R.id.chb_is_executed);
    }

    @Override
    public void setItemViewClickListener() {
        this.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerItemClick();
            }
        });
    }

    private void triggerItemClick() {
        int position = getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            tasksListPresenter.processItemClick(position);
        }
    }

    @Override
    public void setChecked(boolean isChecked) {
        this.isExecutedCheckBox.setChecked(isChecked);
        this.isExecutedCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerTaskStatusChanging();
            }
        });
    }

    private void triggerTaskStatusChanging() {
        int position = getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            tasksListPresenter.processTaskStatusChanging(position);
        }
    }

    @Override
    public void setName(String name) {
        this.taskName.setText(name);
    }

    @Override
    public void setDescription(String description) {
        if (description.isEmpty()) {
            this.taskDescription.setVisibility(View.GONE);
        } else {
            this.taskDescription.setText(description);
        }
    }

    @Override
    public void setDelete() {
        this.taskDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerDeleteItem();
            }
        });
    }

    private void triggerDeleteItem() {
        int position = getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            tasksListPresenter.processItemDelete(position);
        }
    }
}
