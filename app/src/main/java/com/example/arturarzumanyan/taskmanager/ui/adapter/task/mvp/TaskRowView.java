package com.example.arturarzumanyan.taskmanager.ui.adapter.task.mvp;

public interface TaskRowView {
    void setItemViewClickListener(int position);

    void setChecked(int position, boolean isChecked);

    void setName(String name);

    void setDescription(String description);

    void setDelete(int position);
}
