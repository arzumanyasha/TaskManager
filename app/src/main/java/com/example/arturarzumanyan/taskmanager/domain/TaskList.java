package com.example.arturarzumanyan.taskmanager.domain;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "tasklist_table")
public class TaskList implements Parcelable{
    @PrimaryKey
    private int id;
    private String taskListId;
    private String title;

    public TaskList(String taskListId, String title) {
        this.taskListId = taskListId;
        this.title = title;
    }

    @Ignore
    public TaskList(int id, String taskListId, String title) {
        this.id = id;
        this.taskListId = taskListId;
        this.title = title;
    }

    protected TaskList(Parcel in) {
        id = in.readInt();
        taskListId = in.readString();
        title = in.readString();
    }

    public static final Creator<TaskList> CREATOR = new Creator<TaskList>() {
        @Override
        public TaskList createFromParcel(Parcel in) {
            return new TaskList(in);
        }

        @Override
        public TaskList[] newArray(int size) {
            return new TaskList[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskListId() {
        return taskListId;
    }

    public void setTaskListId(String taskListId) {
        this.taskListId = taskListId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(taskListId);
        dest.writeString(title);
    }
}
