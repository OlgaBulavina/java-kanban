package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.Status;

import java.util.ArrayList;
import java.util.HashMap;


public class ObjectManager {
    private static Integer uin = 0;
    public static HashMap<Integer, Task> taskStorage = new HashMap<>();
    public static HashMap<Integer, Epic> epicStorage = new HashMap<>();

    public static HashMap<Integer, HashMap<Integer, Subtask>> subtaskStorage = new HashMap<>();

    public static int getUin(Task task) {
        return task.getUin();
    }

    public static int setUin(Task task) {
        uin++;
        return task.setUin(uin);
    }

    public static void createTask(Task task) {
        setUin(task);
        task.setStatus(Status.NEW);
        taskStorage.put(getUin(task), task);
    }

    public static void createEpic(Epic epic) {
        setUin(epic);
        epic.setStatus(Status.NEW);
        epicStorage.put(getUin(epic), epic);
        HashMap<Integer, Subtask> subtaskList = new HashMap<>();
        subtaskStorage.put(getUin(epic), subtaskList);
    }

    public static void createSubtask(int epicUin, Subtask subtask) {
        setUin(subtask);
        subtask.setEpicUin(epicUin);
        subtask.setStatus(Status.NEW);
        HashMap<Integer, Subtask> currentSubtaskList = subtaskStorage.get(subtask.getThisEpicUin());
        currentSubtaskList.put(getUin(subtask), subtask);
    }

    public static Task getTask(int uin) {
        return taskStorage.get(uin);
    }

    public static Epic getEpic(int uin) {
        return epicStorage.get(uin);
    }

    public Subtask getSubtask(int uin) {
        Subtask currentSubtask = null;
        for (HashMap<Integer, Subtask> value : subtaskStorage.values()) {
            for (Integer key : value.keySet()) {
                if (uin == key) {
                    currentSubtask = value.get(key);
                }
            }
        }
        return currentSubtask;
    }


    public static void updateTask(int uin, Task newTask) {
        taskStorage.remove(uin);
        newTask.setUin(uin);
        taskStorage.put(uin, newTask);
    }

    public static void updateEpic(int uin, Epic newEpic) {
        newEpic.setUin(getUin(getEpic(uin)));
        epicStorage.remove(uin);
        epicStorage.put(uin, newEpic);
        countEpicStatus(uin);
    }

    public static void updateSubtask(int subtaskUin, Subtask newSubtask, Status status) {
        newSubtask.setStatus(status);
        for (HashMap<Integer, Subtask> value : subtaskStorage.values()) {
            for (Integer key : value.keySet()) {
                if (subtaskUin == key) {
                    newSubtask.setUin(getUin(value.get(key)));
                    newSubtask.setEpicUin(value.get(key).getThisEpicUin());
                    value.remove(key);
                    value.put(key, newSubtask);
                    break;
                }
            }
        }
        countEpicStatus(newSubtask.getThisEpicUin());
    }

    public static void deleteTask(int uin) {
        taskStorage.remove(uin);
    }

    public static void deleteEpic(int uin) {
        epicStorage.remove(uin);
        subtaskStorage.remove(uin);
    }

    public void deleteSubtask(int subtaskUin) {
        int epicUin = getSubtask(subtaskUin).getThisEpicUin();
        for (HashMap<Integer, Subtask> value : subtaskStorage.values()) {
            for (Integer key : value.keySet()) {
                if (subtaskUin == key) {
                    value.remove(key);
                    break;
                }
            }
        }
        countEpicStatus(epicUin);
    }

    public static void showAllTasks() {
        for (Integer i : taskStorage.keySet()) {
            System.out.println(taskStorage.get(i));
        }
    }

    public static void showAllEpics() {
        for (Integer i : epicStorage.keySet()) {
            System.out.println(epicStorage.get(i));
        }
    }

    public static ArrayList<Subtask> showEpicSubtasks(int epicUin) {
        HashMap<Integer, Subtask> currentSubtaskList = subtaskStorage.get(epicUin);
        ArrayList<Subtask> subtasksListToPrint = new ArrayList<>();
        for (Subtask subtask : currentSubtaskList.values()) {
            subtasksListToPrint.add(subtask);
        }
        return subtasksListToPrint;
    }

    public static void deleteAllTasks() {
        taskStorage.clear();
    }

    public static void deleteAllEpics() {
        epicStorage.clear();
    }

    public static void deleteAllSubtasksForOneEpic(int uin) {
        HashMap<Integer, Subtask> currentSubtaskList = subtaskStorage.get(uin);
        currentSubtaskList.clear();
    }

    public static void deleteAllSubtasksForAllEpics() {
        subtaskStorage.clear();
    }

    private static void countEpicStatus(int uin) {
        Epic epicToCheck = epicStorage.get(uin);
        int newStatus = 0;
        int inProgressStatus = 0;
        int doneStatus = 0;
        HashMap<Integer, Subtask> currentSubtaskList = subtaskStorage.get(uin);
        for (Subtask currentSubtask : currentSubtaskList.values()) {
            switch (currentSubtask.status) {
                case NEW:
                    newStatus++;
                    break;
                case IN_PROGRESS:
                    inProgressStatus++;
                    break;
                case DONE:
                    doneStatus++;
                    break;
            }
        }
        if ((newStatus != 0 && inProgressStatus == 0 && doneStatus == 0)) {
            epicToCheck.setStatus(Status.NEW);
        } else if ((newStatus == 0 && inProgressStatus == 0 && doneStatus != 0)) {
            epicToCheck.setStatus(Status.DONE);
        } else {
            epicToCheck.setStatus(Status.IN_PROGRESS);
        }
    }
}
