package com.example.arturarzumanyan.taskmanager.domain;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;

import java.util.Date;

@Entity(tableName = "task_table")
@ForeignKey(entity = TaskList.class, parentColumns = "id", childColumns = "listId")
public class Task implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String taskId;
    private String name;
    private String description;
    private int isExecuted;
    private String date;
    private int listId;

    public Task(String taskId,
                String name,
                String description,
                int isExecuted,
                int listId,
                String date) {
        this.taskId = taskId;
        this.name = name;
        this.description = description;
        this.isExecuted = isExecuted;
        this.listId = listId;
        this.date = date;
    }

    protected Task(Parcel in) {
        id = in.readInt();
        taskId = in.readString();
        name = in.readString();
        description = in.readString();
        isExecuted = in.readInt();
        listId = in.readInt();

        if (in.dataAvail() > 0) {
            date = in.readString();
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String id) {
        this.taskId = id;
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
        dest.writeInt(id);
        dest.writeString(taskId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(isExecuted);
        dest.writeInt(listId);
        if (date != null) {
            dest.writeString(date);
        }
    }
}
