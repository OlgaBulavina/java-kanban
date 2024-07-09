package model;

public class Subtask extends Task {
    private int thisEpicUin;

    public Subtask(String name, String description) {
        super(name, description);
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
