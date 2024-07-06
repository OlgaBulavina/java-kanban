package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    protected static Integer uin = 0;
    private Map<Integer, Task> taskStorage = new HashMap<>();
    protected Map<Integer, Epic> epicStorage = new HashMap<>();

    private Map<Integer, HashMap<Integer, Subtask>> subtaskStorage = new HashMap<>();
    private HistoryManager inMemoryHistoryManager;

    public InMemoryTaskManager(HistoryManager inMemoryHistoryManager) {
        this.inMemoryHistoryManager = inMemoryHistoryManager;
    }

    public int getUin(Task task) {
        return task.getUin();
    }

    public int setUin(Task task) {
        uin++;
        return task.setUin(uin);
    }

    @Override
    public void createTask(Task task) {
        setUin(task);
        task.setStatus(Status.NEW);
        taskStorage.put(getUin(task), task);
    }

    @Override
    public void createEpic(Epic epic) {
        setUin(epic);
        epic.setStatus(Status.NEW);
        epicStorage.put(getUin(epic), epic);
        HashMap<Integer, Subtask> subtaskList = new HashMap<>();
        subtaskStorage.put(getUin(epic), subtaskList);
    }

    @Override
    public void createSubtask(int epicUin, Subtask subtask) {
        setUin(subtask);
        subtask.setEpicUin(epicUin);
        subtask.setStatus(Status.NEW);
        HashMap<Integer, Subtask> currentSubtaskList = subtaskStorage.get(subtask.getThisEpicUin());
        currentSubtaskList.put(getUin(subtask), subtask);
    }

    @Override
    public Task getTask(int uin) {
        Task currentTask = taskStorage.get(uin);
        inMemoryHistoryManager.add(currentTask);
        return currentTask;
    }

    @Override
    public Epic getEpic(int uin) {
        Epic currentEpic = epicStorage.get(uin);
        inMemoryHistoryManager.add(currentEpic);
        return currentEpic;
    }

    @Override
    public Subtask getSubtask(int uin) {
        Subtask currentSubtask = null;
        for (HashMap<Integer, Subtask> value : subtaskStorage.values()) {
            for (Integer key : value.keySet()) {
                if (uin == key) {
                    currentSubtask = value.get(key);
                }
            }
        }
        inMemoryHistoryManager.add(currentSubtask);
        return currentSubtask;
    }

    @Override
    public void updateTask(int uin, Task newTask) {
        taskStorage.remove(uin);
        newTask.setUin(uin);
        taskStorage.put(uin, newTask);
    }

    @Override
    public void updateEpic(int uin, Epic newEpic) {
        newEpic.setUin(getUin(getEpic(uin)));
        epicStorage.remove(uin);
        epicStorage.put(uin, newEpic);
        countEpicStatus(uin);
    }

    @Override
    public void updateSubtask(int subtaskUin, Subtask newSubtask, Status status) {
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

    @Override
    public void deleteTask(int uin) {
        taskStorage.remove(uin);
        inMemoryHistoryManager.remove(uin);
    }

    @Override
    public void deleteEpic(int uin) {
        ArrayList<Subtask> subtasksList = showEpicSubtasks(uin);
        for (Subtask subtask : subtasksList) {
            inMemoryHistoryManager.remove(subtask.getUin());
        }
        epicStorage.remove(uin);
        inMemoryHistoryManager.remove(uin);
        subtaskStorage.remove(uin);

    }

    @Override
    public void deleteSubtask(int subtaskUin) {
        final int epicUin = getSubtask(subtaskUin).getThisEpicUin();
        for (HashMap<Integer, Subtask> value : subtaskStorage.values()) {
            for (Integer key : value.keySet()) {
                if (subtaskUin == key) {
                    value.remove(key);
                    break;
                }
            }
        }
        inMemoryHistoryManager.remove(subtaskUin);
        countEpicStatus(epicUin);
    }

    @Override
    public ArrayList<Task> showAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Integer i : taskStorage.keySet()) {
            tasks.add(taskStorage.get(i));
        }
        return tasks;
    }

    @Override
    public ArrayList<Epic> showAllEpics() {
        ArrayList<Epic> epics = new ArrayList<>();
        for (Integer i : epicStorage.keySet()) {
            epics.add(epicStorage.get(i));
        }
        return epics;
    }

    @Override
    public ArrayList<Subtask> showEpicSubtasks(int epicUin) {
        HashMap<Integer, Subtask> currentSubtaskList = subtaskStorage.get(epicUin);
        ArrayList<Subtask> subtasksListToPrint = new ArrayList<>();
        for (Subtask subtask : currentSubtaskList.values()) {
            subtasksListToPrint.add(subtask);
        }
        return subtasksListToPrint;
    }

    @Override
    public ArrayList<Subtask> showAllSubtasks() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (Integer key : epicStorage.keySet()) {
            ArrayList subtasksByEpicUin = showEpicSubtasks(key);
            subtasks.addAll(subtasksByEpicUin);
        }
        return subtasks;
    }

    @Override
    public void deleteAllTasks() {
        taskStorage.clear();
    }

    @Override
    public void deleteAllEpics() {
        epicStorage.clear();
    }

    @Override
    public void deleteAllSubtasksForOneEpic(int uin) {
        HashMap<Integer, Subtask> currentSubtaskList = subtaskStorage.get(uin);
        for (Subtask subtask : currentSubtaskList.values()) {
            inMemoryHistoryManager.remove(subtask.getUin());
        }
        currentSubtaskList.clear();
    }

    @Override
    public void deleteAllSubtasksForAllEpics() {
        for (Integer epicUin : epicStorage.keySet()) {
            deleteAllSubtasksForOneEpic(epicUin);
        }
    }

    private void countEpicStatus(int uin) {
        Epic epicToCheck = epicStorage.get(uin);
        int newStatus = 0;
        int doneStatus = 0;
        HashMap<Integer, Subtask> currentSubtaskList = subtaskStorage.get(uin);
        for (Subtask currentSubtask : currentSubtaskList.values()) {
            switch (currentSubtask.status) {
                case NEW:
                    newStatus++;
                    break;
                case IN_PROGRESS:
                    break;
                case DONE:
                    doneStatus++;
                    break;
            }
        }
        if ((newStatus == currentSubtaskList.size())) {
            epicToCheck.setStatus(Status.NEW);
        } else if ((doneStatus == currentSubtaskList.size())) {
            epicToCheck.setStatus(Status.DONE);
        } else {
            epicToCheck.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public Collection getTasksHistoryFromInMemoryHM() {
        return inMemoryHistoryManager.getHistory();
    }

    @Override
    public Map<Integer, Task> getTaskStorage() {
        return taskStorage;
    }

    @Override
    public Map<Integer, Epic> getEpicStorage() {
        return epicStorage;
    }

    @Override
    public Map<Integer, HashMap<Integer, Subtask>> getSubtaskStorage() {
        return subtaskStorage;
    }

    @Override
    public HistoryManager getInMemoryHistoryManager() {
        return inMemoryHistoryManager;
    }
}
