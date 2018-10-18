package com.example.arturarzumanyan.taskmanager.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;

import java.util.Date;

public class Task implements Parcelable {
    private String id;
    private String name;
    private String description;
    private int isExecuted;
    private Date date;
    private int listId;

    public Task(String id,
                String name,
                String description,
                int isExecuted,
                int listId,
                Date date) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isExecuted = isExecuted;
        this.listId = listId;
        this.date = date;
    }

    public Task(String id,
                String name,
                String description,
                int isExecuted,
                int listId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isExecuted = isExecuted;
        this.listId = listId;
    }

    protected Task(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        isExecuted = in.readInt();
        listId = in.readInt();

        if (in.dataAvail() > 0) {
            date = DateUtils.getTaskDateFromString(in.readString());
        }
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getIsExecuted() {
        return isExecuted;
    }

    public void setIsExecuted(int isExecuted) {
        this.isExecuted = isExecuted;
    }

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(isExecuted);
        dest.writeInt(listId);
        if (date != null) {
            dest.writeString(DateUtils.formatTaskDate(date));
        }
    }
}
