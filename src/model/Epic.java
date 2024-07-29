package model;

import java.time.Duration;
import java.time.LocalDateTime;


public class Epic extends Task {
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.setDuration(Duration.ofMinutes(0));
    }

    public Epic(Duration duration, LocalDateTime startTime, String name, String description, Status status) {
        super(duration, startTime, name, description, status);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

}

