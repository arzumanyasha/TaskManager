package com.example.arturarzumanyan.taskmanager.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.arturarzumanyan.taskmanager.networking.util.DateUtils;

import java.util.Date;

public class Event implements Parcelable{
    private String id;
    private String name;
    private String description;
    private int colorId;
    private Date startTime;
    private Date endTime;
    private boolean isNotify;

    public Event(String id,
                 String name,
                 String description,
                 int colorId,
                 Date startTime,
                 Date endTime,
                 boolean isNotify) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.colorId = colorId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isNotify = isNotify;
    }

    protected Event(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        colorId = in.readInt();
        startTime = DateUtils.getEventDateFromString(in.readString());
        endTime = DateUtils.getEventDateFromString(in.readString());
        isNotify = in.readByte() != 0;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
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

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean isNotify() {
        return isNotify;
    }

    public void setNotify(boolean notify) {
        isNotify = notify;
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
        dest.writeInt(colorId);
        dest.writeString(DateUtils.formatTaskDate(startTime));
        dest.writeString(DateUtils.formatTaskDate(endTime));
        dest.writeByte((byte) (isNotify ? 1 : 0));
    }
}
