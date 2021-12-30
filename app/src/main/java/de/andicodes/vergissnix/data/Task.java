package de.andicodes.vergissnix.data;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

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

    public Task() {

    }

    public Task(Task task) {
        this.id = task.id;
        this.time = task.time;
        this.timeDone = task.timeDone;
        this.timeCreated = task.timeCreated;
        this.text = task.text;
    }

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

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", time=" + time +
                ", timeDone=" + timeDone +
                ", timeCreated=" + timeCreated +
                ", text='" + text + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
                Objects.equals(time, task.time) &&
                Objects.equals(timeDone, task.timeDone) &&
                Objects.equals(timeCreated, task.timeCreated) &&
                Objects.equals(text, task.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, time, timeDone, timeCreated, text);
    }
}
