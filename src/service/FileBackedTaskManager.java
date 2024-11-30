package service;

import exception.IntersectionException;
import model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    protected File storageFile = new File("backup.csv");
    protected final String heading = "UIN,TYPE,NAME,DESCRIPTION,STATUS,START_TIME,DURATION,EPIC_NUMBER(FOR SUBTASK)\n";

    public FileBackedTaskManager(HistoryManager inMemoryHistoryManager) {
        super(inMemoryHistoryManager);
    }

    public void save() {
        try (FileWriter fileWriter = new FileWriter(storageFile)) {
            fileWriter.write(heading);
            super.showAllTasks().stream()
                    .forEach(task -> {
                        try {
                            fileWriter.write(task.toString() + "\n");
                        } catch (IOException e) {
                            throw new ManagerSaveException("\"Произошла ошибка с записью в файл\\n\"");
                        }
                    });
            super.showAllEpics().stream()
                    .forEach(epic -> {
                        try {
                            fileWriter.write(epic.toString() + "\n");
                        } catch (IOException e) {
                            throw new ManagerSaveException("\"Произошла ошибка с записью в файл\\n\"");
                        }
                    });
            super.showAllSubtasks().stream()
                    .forEach(subtask -> {
                        try {
                            fileWriter.write(subtask.toString() + "\n");
                        } catch (IOException e) {
                            throw new ManagerSaveException("\"Произошла ошибка с записью в файл\\n\"");
                        }
                    });
        } catch (IOException e) {
            throw new ManagerSaveException("\"Произошла ошибка с записью в файл\\n\"");
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
            String startTime = taskData[5];
            long duration = Long.parseLong(taskData[6]);

            switch (taskType) {
                case TASK -> {
                    Task task = new Task(Duration.ofMinutes(duration), LocalDateTime.parse(startTime), taskName,
                            taskDescription, taskStatus);
                    task.setUin(taskUin);
                    return task;
                }
                case EPIC -> {
                    Epic task = new Epic(Duration.ofMinutes(duration), LocalDateTime.parse(startTime), taskName,
                            taskDescription, taskStatus);
                    task.setStatus(taskStatus);
                    task.setUin(taskUin);
                    return task;
                }
                case SUBTASK -> {
                    Subtask task = new Subtask(Duration.ofMinutes(duration), LocalDateTime.parse(startTime), taskName,
                            taskDescription, taskStatus);
                    task.setStatus(taskStatus);
                    task.setUin(taskUin);
                    task.setEpicUin(Integer.parseInt(taskData[7]));
                    return task;
                }
            }
        }
        return new Task(null, null);
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerReadException, NullPointerException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(new InMemoryHistoryManager());

        Collection<Task> backupHistoryAllTasksList = new ArrayList<>();
        //int lastCommonUin = 0;
        try {
            String taskDataInString = Files.readString(file.toPath());
            if (taskDataInString.isBlank() || taskDataInString.isEmpty()) {
                return fileBackedTaskManager;
            }
            String[] tasksDescription = taskDataInString.split("\n");
            Arrays.stream(tasksDescription)
                    .filter(line -> !(line.trim().equals(fileBackedTaskManager.heading.trim())) && !line.isBlank())
                    .forEach(line -> backupHistoryAllTasksList.add(fromString(line.trim())));
        } catch (IOException e) {
            throw new ManagerReadException("Произошла ошибка чтения файла\n");
        }

        fileBackedTaskManager.uin =

                backupHistoryAllTasksList.stream() //added
                        .mapToInt(task -> {
                            TaskType taskType = (task.getTaskType());
                            switch (taskType) {
                                case TASK -> {
                                    fileBackedTaskManager.getTaskStorage().put(task.getUin(), task);
                                }
                                case EPIC -> {
                                    fileBackedTaskManager.getEpicStorage().put(task.getUin(), (Epic) task);
                                }
                                case SUBTASK -> {
                                    Subtask subtask = (Subtask) task;
                                    HashMap<Integer, Subtask> innerHashMap = new HashMap<>();
                                    innerHashMap.put(subtask.getUin(), subtask);
                                    if (!fileBackedTaskManager.getSubtaskStorage()
                                            .containsKey(subtask.getThisEpicUin())) {
                                        fileBackedTaskManager.getSubtaskStorage().put(subtask.getThisEpicUin(),
                                                innerHashMap);
                                    } else {
                                        HashMap<Integer, Subtask> innerEpicHashMap = fileBackedTaskManager
                                                .getSubtaskStorage().get(subtask.getThisEpicUin());
                                        innerEpicHashMap.put(subtask.getUin(), subtask);
                                    }
                                }
                                default -> {
                                }
                            }
                            return task.getUin();
                        })
                        .max().getAsInt();

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
    public void createTask(Task task) throws IntersectionException {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(int epicUin, Subtask subtask) throws IntersectionException {
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
    public void updateTask(int uin, Task newTask) throws IntersectionException {
        super.updateTask(uin, newTask);
        save();
    }

    @Override
    public void updateEpic(int uin, Epic newEpic) {
        super.updateEpic(uin, newEpic);
        save();
    }

    @Override
    public void updateSubtask(int subtaskUin, Subtask newSubtask, Status status) throws IntersectionException {
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
