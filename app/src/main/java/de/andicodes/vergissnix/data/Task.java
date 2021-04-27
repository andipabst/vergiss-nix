package de.andicodes.vergissnix.data;

import java.io.Serializable;
import java.time.ZonedDateTime;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Task implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private ZonedDateTime time;
    private ZonedDateTime timeDone;
    private ZonedDateTime timeCreated;
    private String text;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public void setTime(ZonedDateTime time) {
        this.time = time;
    }

    public ZonedDateTime getTimeDone() {
        return timeDone;
    }

    public void setTimeDone(ZonedDateTime timeDone) {
        this.timeDone = timeDone;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ZonedDateTime getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(ZonedDateTime timeCreated) {
        this.timeCreated = timeCreated;
    }
}
