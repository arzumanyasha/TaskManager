package com.example.arturarzumanyan.taskmanager.ui.adapter.event.mvp;

public interface EventRowView {
    void setItemViewClickListener();

    void setName(String name);

    void setDescription(String description);

    void setTime(String time);

    void setEventColor(int colorId);

    void setDelete();
}
