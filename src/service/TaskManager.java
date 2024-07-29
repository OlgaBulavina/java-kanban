package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.*;

public interface TaskManager {
    int getUin(Task task);

    int setUin(Task task);

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(int epicUin, Subtask subtask);

    Task getTask(int uin);

    Epic getEpic(int uin);

    Subtask getSubtask(int uin);

    void updateTask(int uin, Task newTask);

    void updateEpic(int uin, Epic newEpic);

    void updateSubtask(int subtaskUin, Subtask newSubtask, Status status);

    void deleteTask(int uin);

    void deleteEpic(int uin);

    void deleteSubtask(int subtaskUin);

    Map<Integer, Task> getTaskStorage();

    Map<Integer, Epic> getEpicStorage();

    Map<Integer, HashMap<Integer, Subtask>> getSubtaskStorage();

    ArrayList<Task> showAllTasks();

    ArrayList<Epic> showAllEpics();

    ArrayList<Subtask> showAllSubtasks();

    ArrayList<Subtask> showEpicSubtasks(int epicUin);

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasksForOneEpic(int uin);

    void deleteAllSubtasksForAllEpics();

    HistoryManager getInMemoryHistoryManager();

    Collection<Task> getTasksHistoryFromInMemoryHM();

    void prioritizeTasks(Task task, String typeOfChange);

    void prioritizeTasks(List<Integer> severalSubtasksUins);

    Set<Task> getPrioritizedTasks();

    boolean taskStartEndTimeValidator(Task task, int oldTaskUin);
}
