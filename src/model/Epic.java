package model;

public class Epic extends Task {

    private static TaskType epicType = TaskType.EPIC;

    public Epic(String name, String description) {
        super(name, description);
    }

    @Override
    public TaskType getTaskType() {
        return epicType;
    }
}

