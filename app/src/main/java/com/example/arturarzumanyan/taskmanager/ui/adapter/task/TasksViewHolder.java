package com.example.arturarzumanyan.taskmanager.ui.adapter.task;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.arturarzumanyan.taskmanager.R;
import com.example.arturarzumanyan.taskmanager.ui.adapter.task.mvp.TaskRowView;
import com.example.arturarzumanyan.taskmanager.ui.adapter.task.mvp.TasksListPresenter;

public class TasksViewHolder extends RecyclerView.ViewHolder implements TaskRowView {
    private TasksListPresenter tasksListPresenter;
    private TextView taskName;
    private TextView taskDescription;
    private ImageView taskDelete;
    private CheckBox isExecutedCheckBox;

    TasksViewHolder(View view, TasksListPresenter tasksListPresenter) {
        super(view);
        this.tasksListPresenter = tasksListPresenter;
        taskName = view.findViewById(R.id.text_task_name);
        taskDescription = view.findViewById(R.id.text_task_description);
        taskDelete = view.findViewById(R.id.image_delete);
        isExecutedCheckBox = view.findViewById(R.id.chb_is_executed);
    }

    @Override
    public void setItemViewClickListener(int position) {
        this.itemView.setTag(position);
        this.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerItemClick(v);
            }
        });
    }

    private void triggerItemClick(View v) {
        int position = (int) v.getTag();
        if (position != RecyclerView.NO_POSITION) {
            tasksListPresenter.processItemClick(position);
        }
    }

    @Override
    public void setChecked(int position, boolean isChecked) {
        this.isExecutedCheckBox.setTag(position);
        this.isExecutedCheckBox.setChecked(isChecked);
        this.isExecutedCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerTaskStatusChanging(v);
            }
        });
    }

    private void triggerTaskStatusChanging(View v) {
        int position = (int) v.getTag();
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
    public void setDelete(int position) {
        this.taskDelete.setTag(position);
        this.taskDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerDeleteItem(v);
            }
        });
    }

    private void triggerDeleteItem(View v) {
        int position = (int) v.getTag();
        if (position != RecyclerView.NO_POSITION) {
            tasksListPresenter.processItemDelete(position);
        }
    }
}
