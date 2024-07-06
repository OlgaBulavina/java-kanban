package model;

public class Subtask extends Task {
    private int thisEpicUin;
    private static TaskType subtaskType = TaskType.SUBTASK;

    public Subtask(String name, String description) {
        super(name, description);
    }

    @Override
    public TaskType getTaskType() {
        return subtaskType;
    }

    public void setEpicUin(int epicUin) {
        this.thisEpicUin = epicUin;
    }

    public int getThisEpicUin() {
        return thisEpicUin;
    }

}
