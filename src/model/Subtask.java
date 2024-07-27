package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int thisEpicUin;

    public Subtask(String name, String description) {
        super(name, description);
    }

    public Subtask(Duration duration, LocalDateTime startTime, String name, String description) {
        super(duration, startTime, name, description);
    }

    public Subtask(Duration duration, LocalDateTime startTime, String name, String description, Status status) {
        super(duration, startTime, name, description, status);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }

    public void setEpicUin(int epicUin) {
        this.thisEpicUin = epicUin;
    }

    public int getThisEpicUin() {
        return thisEpicUin;
    }

}
