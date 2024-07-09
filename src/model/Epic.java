package model;

public class Epic extends Task {

    public Epic(String name, String description) {
        super(name, description);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }
}

