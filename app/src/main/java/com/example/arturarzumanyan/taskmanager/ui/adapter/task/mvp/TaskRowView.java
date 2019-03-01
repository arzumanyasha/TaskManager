package com.example.arturarzumanyan.taskmanager.ui.adapter.task.mvp;

public interface TaskRowView {
    void setItemViewClickListener();

    void setChecked(boolean isChecked);

    void setName(String name);

    void setDescription(String description);

    void setDelete();
}
