package service;

import model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    protected File storageFile = new File("backup.csv");
    protected final String heading = "UIN,TYPE,NAME,DESCRIPTION,STATUS,EPIC_NUMBER(FOR SUBTASK)\n";

    public FileBackedTaskManager(HistoryManager inMemoryHistoryManager) {
        super(inMemoryHistoryManager);
    }

    public void save() {
        try (FileWriter fileWriter = new FileWriter(storageFile)) {
            fileWriter.write(heading);

            for (Task task : super.showAllTasks()) {
                fileWriter.write(task.toString() + "\n");
            }
            for (Epic epic : super.showAllEpics()) {
                fileWriter.write(epic.toString() + "\n");
            }
            for (Subtask subtask : super.showAllSubtasks()) {
                fileWriter.write(subtask.toString() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка с записью в файл\n");
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static Task fromString(String value) {
        if (!value.isEmpty() && !value.isBlank()) {
            String[] taskData = value.split(",");
            int taskUin = Integer.parseInt(taskData[0]);
            TaskType taskType = TaskType.valueOf(taskData[1]);
            String taskName = taskData[2];
            String taskDescription = taskData[3];
            Status taskStatus = Status.valueOf(taskData[4]);

            switch (taskType) {
                case TASK -> {
                    Task task = new Task(taskName, taskDescription, taskStatus);
                    task.setUin(taskUin);
                    return task;
                }
                case EPIC -> {
                    Epic task = new Epic(taskName, taskDescription);
                    task.setStatus(taskStatus);
                    task.setUin(taskUin);
                    return task;
                }
                case SUBTASK -> {
                    Subtask task = new Subtask(taskName, taskDescription);
                    task.setStatus(taskStatus);
                    task.setUin(taskUin);
                    task.setEpicUin(Integer.parseInt(taskData[5]));
                    return task;
                }
            }
        }
        return new Task(null, null);
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerReadException, NullPointerException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(new InMemoryHistoryManager());
        try {
            Collection<Task> backupHistoryAllTasksList = new ArrayList<>();
            int lastCommonUin = 0;
            try {
                String taskDataInString = Files.readString(file.toPath());
                if (taskDataInString.isBlank() || taskDataInString.isEmpty()) {
                    return fileBackedTaskManager;
                }
                String[] tasksDescription = taskDataInString.split("\n");
                for (String line : tasksDescription) {
                    if (!(line.trim().equals(fileBackedTaskManager.heading.trim())) && !line.isBlank()) {
                        backupHistoryAllTasksList.add(fromString(line.trim()));
                    }
                }
            } catch (IOException e) {
                throw new ManagerReadException("Произошла ошибка чтения файла\n");
            } catch (ManagerReadException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }

            for (Task task : backupHistoryAllTasksList) {
                TaskType taskType = (task.getTaskType());
                switch (taskType) {
                    case TASK -> {
                        fileBackedTaskManager.getTaskStorage().put(task.getUin(), task);
                        if (task.getUin() > lastCommonUin) {
                            lastCommonUin = task.getUin();
                        }
                    }
                    case EPIC -> {
                        fileBackedTaskManager.getEpicStorage().put(task.getUin(), (Epic) task);
                        if (task.getUin() > lastCommonUin) {
                            lastCommonUin = task.getUin();
                        }
                    }
                    case SUBTASK -> {
                        Subtask subtask = (Subtask) task;
                        HashMap<Integer, Subtask> innerHashMap = new HashMap<>();
                        innerHashMap.put(subtask.getUin(), subtask);
                        if (!fileBackedTaskManager.getSubtaskStorage().containsKey(subtask.getThisEpicUin())) {
                            fileBackedTaskManager.getSubtaskStorage().put(subtask.getThisEpicUin(), innerHashMap);
                        } else {
                            HashMap<Integer, Subtask> innerEpicHashMap = fileBackedTaskManager.getSubtaskStorage().get(subtask.getThisEpicUin());
                            innerEpicHashMap.put(subtask.getUin(), subtask);
                        }
                        if (subtask.getUin() > lastCommonUin) {
                            lastCommonUin = subtask.getUin();
                        }
                    }
                    default -> {
                    }
                }
            }
            fileBackedTaskManager.uin = lastCommonUin;
        } catch (ManagerReadException e) {
            System.out.println(e.getMessage());
        }
        return fileBackedTaskManager;
    }

    @Override
    public int getUin(Task task) {
        return super.getUin(task);
    }

    @Override
    public int setUin(Task task) {
        return super.setUin(task);
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(int epicUin, Subtask subtask) {
        super.createSubtask(epicUin, subtask);
        save();
    }

    @Override
    public Task getTask(int uin) {
        return super.getTask(uin);
    }

    @Override
    public Epic getEpic(int uin) {
        return super.getEpic(uin);
    }

    @Override
    public Subtask getSubtask(int uin) {
        return super.getSubtask(uin);
    }

    @Override
    public void updateTask(int uin, Task newTask) {
        super.updateTask(uin, newTask);
        save();
    }

    @Override
    public void updateEpic(int uin, Epic newEpic) {
        super.updateEpic(uin, newEpic);
        save();
    }

    @Override
    public void updateSubtask(int subtaskUin, Subtask newSubtask, Status status) {
        super.updateSubtask(subtaskUin, newSubtask, status);
        save();
    }

    @Override
    public void deleteTask(int uin) {
        super.deleteTask(uin);
        save();
    }

    @Override
    public void deleteEpic(int uin) {
        super.deleteEpic(uin);
        save();
    }

    @Override
    public void deleteSubtask(int subtaskUin) {
        super.deleteSubtask(subtaskUin);
        save();
    }

    @Override
    public ArrayList<Task> showAllTasks() {
        return super.showAllTasks();
    }

    @Override
    public ArrayList<Epic> showAllEpics() {
        return super.showAllEpics();
    }

    @Override
    public ArrayList<Subtask> showEpicSubtasks(int epicUin) {
        return super.showEpicSubtasks(epicUin);
    }

    @Override
    public ArrayList<Subtask> showAllSubtasks() {
        return super.showAllSubtasks();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasksForOneEpic(int uin) {
        super.deleteAllSubtasksForOneEpic(uin);
        save();
    }

    @Override
    public void deleteAllSubtasksForAllEpics() {
        super.deleteAllSubtasksForAllEpics();
        save();
    }

    @Override
    public Collection getTasksHistoryFromInMemoryHM() {
        return super.getTasksHistoryFromInMemoryHM();
    }

    @Override
    public Map<Integer, Task> getTaskStorage() {
        return super.getTaskStorage();
    }

    @Override
    public Map<Integer, Epic> getEpicStorage() {
        return super.getEpicStorage();
    }

    @Override
    public Map<Integer, HashMap<Integer, Subtask>> getSubtaskStorage() {
        return super.getSubtaskStorage();
    }

    @Override
    public HistoryManager getInMemoryHistoryManager() {
        return super.getInMemoryHistoryManager();
    }
}
