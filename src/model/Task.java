package model;

import java.util.Objects;

public class Task {
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
        return ("name = " + this.getName() + "\ndescription = " + this.description + "\nuin = " + this.uin +
                "\nstatus = " + this.getStatus());
    }

    public int getUin() {
        return uin;
    }

    public int setUin(int uin) {
        return this.uin = uin;
    }
}
