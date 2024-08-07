package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private Duration duration;

    private LocalDateTime startTime;
    private String name;
    private String description;
    private int uin;
    public Status status;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(Duration duration, LocalDateTime startTime, String name, String description) {
        this.duration = duration;
        this.startTime = startTime;
        this.name = name;
        this.description = description;
    }

    public Task(Duration duration, LocalDateTime startTime, String name, String description, Status status) {
        this.duration = duration;
        this.startTime = startTime;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object task) {
        if (this == task) return true;
        if (this == null) return false;
        if (this.getClass() != task.getClass()) return false;
        Task currentTask = (Task) task;
        return (Objects.equals(name, currentTask.name) &&
                uin == currentTask.uin);
    }

    @Override
    public int hashCode() {
        int hash = 31;
        if (this.getName() != null) {
            hash = hash * this.uin;
        }
        return hash;
    }

    @Override
    public String toString() {
        return (this.uin + "," + this.getTaskType() + "," + this.getName() + "," + this.description + "," +
                this.getStatus()) + (this.startTime != null ? "," +
                this.startTime : "") +
                (this.getDuration() != null ? "," + this.getDuration().toMinutes() : "") +
                (this.getClass() == Subtask.class ? "," + ((Subtask) this).getThisEpicUin() : "");
    }

    public int getUin() {
        return uin;
    }

    public int setUin(int uin) {
        return this.uin = uin;
    }

    public TaskType getTaskType() {
        return TaskType.TASK;
    }

    public LocalDateTime getEndTime() {
        return (this.startTime == null) ? null : (this.startTime.plus(this.duration));
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

}
