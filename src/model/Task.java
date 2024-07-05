package model;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int uin;
    public Status status;
    public TaskType taskType;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, Status status) {
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

        if (this.getClass() == Task.class) {
            this.taskType = TaskType.TASK;
        } else if (this.getClass() == Subtask.class) {
            this.taskType = TaskType.SUBTASK;
        } else if (this.getClass() == Epic.class) {
            this.taskType = TaskType.EPIC;
        }
        return (this.uin + "," + this.taskType +   "," + this.getName() + "," + this.description + "," +
                this.getStatus()) + (this.getClass() == Subtask.class ? "," +
                ((Subtask) this).getThisEpicUin() : "");
    }

    public int getUin() {
        return uin;
    }

    public int setUin(int uin) {
        return this.uin = uin;
    }
}
