package com.example.arturarzumanyan.taskmanager.domain;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "event_table")
public class Event implements Parcelable{
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String eventId;
    private String name;
    private String description;
    private int colorId;
    private String startTime;
    private String endTime;
    private int isNotify;

    public Event(String eventId,
                 String name,
                 String description,
                 int colorId,
                 String startTime,
                 String endTime,
                 int isNotify) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.colorId = colorId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isNotify = isNotify;
    }

    protected Event(Parcel in) {
        eventId = in.readString();
        name = in.readString();
        description = in.readString();
        colorId = in.readInt();
        startTime = in.readString();
        endTime = in.readString();
        isNotify = in.readInt();
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getIsNotify() {
        return isNotify;
    }

    public void setIsNotify(int isNotify) {
        this.isNotify = isNotify;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eventId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(colorId);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeInt(isNotify);
    }
}
