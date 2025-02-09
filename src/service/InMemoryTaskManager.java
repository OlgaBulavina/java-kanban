package service;

import exception.IntersectionException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryTaskManager implements TaskManager {

    protected static Integer uin = 0;
    private Map<Integer, Task> taskStorage = new HashMap<>();
    protected Map<Integer, Epic> epicStorage = new HashMap<>();

    private Map<Integer, HashMap<Integer, Subtask>> subtaskStorage = new HashMap<>();

    private Set<Task> collectedSetOfPrioritizedTasks = new TreeSet<>(Comparator.comparing(task ->
            task.getStartTime()));
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
    public void createTask(Task task) throws IntersectionException {
        setUin(task);
        task.setStatus(Status.NEW);
        boolean check = taskStartEndTimeValidator(task, 0);
        if (!check) {
            taskStorage.put(getUin(task), task);
            prioritizeTasks(task, "add_new");
        }
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
    public void createSubtask(int epicUin, Subtask subtask) throws IntersectionException {
        setUin(subtask);
        subtask.setEpicUin(epicUin);
        if (subtask.getStatus() == null) {
            subtask.setStatus(Status.NEW);
        }
        if (subtask.getDuration() == null) {
            subtask.setDuration(Duration.ofMinutes(0L));
        }
        try {
            boolean check = taskStartEndTimeValidator(subtask, 0);
            if (!check) {
                Map<Integer, Subtask> currentSubtaskList = subtaskStorage.get(epicUin);
                currentSubtaskList.put(getUin(subtask), subtask);
                updateEpicDuration(epicUin);
                countEpicStatus(epicUin);
                setEpicStartTime(epicUin);
                updateEpicEndTime(epicUin);
                prioritizeTasks(subtask, "add_new");
            }
        } catch (IntersectionException ex) {
            ex.printStackTrace();
            throw new IntersectionException("Intersection of Subtasks occurred while creating.");
        }
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
        Subtask currentSubtask = subtaskStorage.values().stream()
                .flatMap(subtasks -> subtasks.values().stream())
                .filter(subtask -> subtask.getUin() == uin)
                .findAny()
                .orElse(new Subtask("null", "null"));
        inMemoryHistoryManager.add(currentSubtask);
        return currentSubtask;
    }

    @Override
    public void updateTask(int uin, Task newTask) throws IntersectionException {

        newTask.setUin(uin);
        if (newTask.status == null) {
            newTask.setStatus(taskStorage.get(uin).getStatus());
        }
        taskStorage.remove(uin);
        try {
            boolean check = taskStartEndTimeValidator(newTask, uin);
            if (!check) {
                taskStorage.put(uin, newTask);
                prioritizeTasks(newTask, "change");
            }
        } catch (IntersectionException ex) {
            ex.printStackTrace();
            throw new IntersectionException("Intersection of Subtasks occurred while updating.");
        }
    }

    @Override
    public void updateEpic(int uin, Epic newEpic) {
        newEpic.setUin(getUin(getEpic(uin)));
        epicStorage.remove(uin);
        epicStorage.put(uin, newEpic);
        countEpicStatus(uin);
    }

    @Override
    public void updateSubtask(int subtaskUin, Subtask newSubtask, Status status) throws IntersectionException {
        newSubtask.setStatus(status);
        try {
            boolean check = taskStartEndTimeValidator(newSubtask, subtaskUin);
            if (!check) {
                prioritizeTasks(getSubtask(subtaskUin), "change");
                Optional<Subtask> subtaskOptionalToChange = subtaskStorage.values().stream()
                        .flatMap(subtasks -> subtasks.values().stream()
                                .filter(subtask -> subtask.getUin() == subtaskUin))
                        .findAny();
                if (subtaskOptionalToChange.isPresent()) {
                    Subtask subtaskToChange = subtaskOptionalToChange.get();
                    newSubtask.setUin(subtaskToChange.getUin());
                    newSubtask.setEpicUin(subtaskToChange.getThisEpicUin());
                    if (newSubtask.getStartTime() == null) {
                        newSubtask.setStartTime(subtaskToChange.getStartTime());
                    }
                    if (newSubtask.getDuration() == null) {
                        newSubtask.setDuration(subtaskToChange.getDuration());
                    }
                    if (subtaskStorage.values().stream()
                            .peek(subtasks -> {
                                subtasks.remove(subtaskUin, subtaskToChange);
                                subtasks.put(subtaskUin, newSubtask);
                            })
                            .anyMatch(subtasks -> subtasks.containsValue(newSubtask))) {
                        countEpicStatus(newSubtask.getThisEpicUin());
                        updateEpicDuration(newSubtask.getThisEpicUin());
                        setEpicStartTime(newSubtask.getThisEpicUin());
                        updateEpicEndTime(newSubtask.getThisEpicUin());
                    }
                }
            }
        } catch (IntersectionException ignored) {
        }
    }

    @Override
    public void deleteTask(int uin) {
        prioritizeTasks(getTask(uin), "delete");
        taskStorage.remove(uin);
        inMemoryHistoryManager.remove(uin);
    }

    @Override
    public void deleteEpic(int uin) {

        deleteAllSubtasksForOneEpic(uin);

        epicStorage.remove(uin);
        inMemoryHistoryManager.remove(uin);
        subtaskStorage.remove(uin);
    }

    @Override
    public void deleteSubtask(int subtaskUin) {

        prioritizeTasks(getSubtask(subtaskUin), "delete");

        final int epicUin = getSubtask(subtaskUin).getThisEpicUin();
        subtaskStorage.get(epicUin).values().remove(getSubtask(subtaskUin));

        inMemoryHistoryManager.remove(subtaskUin);
        countEpicStatus(epicUin);
        updateEpicDuration(epicUin);
        updateEpicEndTime(epicUin);
    }

    @Override
    public ArrayList<Task> showAllTasks() {
        return new ArrayList<>(taskStorage.values());
    }

    @Override
    public ArrayList<Epic> showAllEpics() {
        return new ArrayList<>(epicStorage.values());
    }

    @Override
    public ArrayList<Subtask> showEpicSubtasks(int epicUin) {
        HashMap<Integer, Subtask> currentSubtaskList = subtaskStorage.get(epicUin);
        List<Subtask> subtasksListToPrint = Stream.ofNullable(currentSubtaskList)
                .flatMap(currentCollection -> currentCollection.values().stream())
                .collect(Collectors.toList());
        return (ArrayList<Subtask>) subtasksListToPrint;
    }

    @Override
    public ArrayList<Subtask> showAllSubtasks() {
        return (epicStorage.values().stream()
                .map(epic -> epic.getUin())
                .map(epicUin -> new ArrayList<>(showEpicSubtasks(epicUin)))
                .flatMap(currentArray -> currentArray.stream())
                .collect(Collectors.toCollection(ArrayList::new)));
    }

    @Override
    public void deleteAllTasks() {
        taskStorage.clear();
        collectedSetOfPrioritizedTasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        /*List<Integer> epicsUins = epicStorage.keySet().stream()
                .collect(Collectors.toList());*/
        deleteAllSubtasksForAllEpics();
        epicStorage.clear();
    }

    @Override
    public void deleteAllSubtasksForOneEpic(int uin) {
        List<Integer> subtasksUins = subtaskStorage.get(uin).keySet().stream()
                .collect(Collectors.toList());
        prioritizeTasks(subtasksUins);
        subtaskStorage.get(uin).values().stream()
                .forEach(subtask -> inMemoryHistoryManager.remove(subtask.getUin()));

        subtaskStorage.get(uin).clear();
        subtaskStorage.remove(uin);
        countEpicStatus(uin);
        updateEpicDuration(uin);
    }

    @Override
    public void deleteAllSubtasksForAllEpics() {
        List<Integer> subtasksUins = subtaskStorage.values().stream()
                .flatMap(subtasks -> subtasks.keySet().stream())
                .collect(Collectors.toList());

        epicStorage.keySet().stream()
                .forEach(key -> deleteAllSubtasksForOneEpic(key));
    }

    private void countEpicStatus(int uin) {
        Epic epicToCheck = epicStorage.get(uin);
        HashMap<Integer, Subtask> currentSubtaskList = subtaskStorage.get(uin);
        if (currentSubtaskList == null) {
            epicToCheck.setStatus(Status.NEW);
        } else {
            int sumOfStatuses = currentSubtaskList.values().stream()
                    .mapToInt(currentSubtask -> {
                        int result = 0;
                        switch (currentSubtask.status) {
                            case NEW:
                                result = +1;
                                break;
                            case IN_PROGRESS:
                                result = 0;
                                break;
                            case DONE:
                                result = -1;
                                break;
                        }
                        return result;
                    })
                    .sum();
            if (sumOfStatuses == currentSubtaskList.size()) {
                epicToCheck.setStatus(Status.NEW);
            } else if ((sumOfStatuses == currentSubtaskList.size() * (-1))) {
                epicToCheck.setStatus(Status.DONE);
            } else {
                epicToCheck.setStatus(Status.IN_PROGRESS);
            }
        }
    }

    public void setEpicStartTime(int epicUin) {
        Epic currentEpic = epicStorage.get(epicUin);
        List<Subtask> listOfSubtasks = showEpicSubtasks(epicUin);
        boolean isEmpty = listOfSubtasks.stream()
                .allMatch(subtask -> subtask == null);
        if (isEmpty) currentEpic.setStartTime(null);
        else {
            currentEpic.setStartTime(listOfSubtasks.stream()
                    .map(subtask -> subtask.getStartTime())
                    .filter(startTime -> startTime != null)
                    .min(LocalDateTime::compareTo).orElse(null));
        }
    }

    public void updateEpicEndTime(int epicUin) {
        Epic currentEpic = epicStorage.get(epicUin);
        List<Subtask> listOfSubtasks = showEpicSubtasks(epicUin);

        currentEpic.setEndTime((listOfSubtasks.stream()
                .filter(subtask -> subtask != null && subtask.getDuration() != null &&
                        subtask.getStartTime() != null)
                .map(subtask -> subtask.getStartTime().plusMinutes(!subtask.getDuration().equals(null) ?
                        subtask.getDuration().toMinutes() : 0L))
                .max(LocalDateTime::compareTo)).orElse(null));
    }

    public void updateEpicDuration(int epicUin) {
        Epic currentEpic = epicStorage.get(epicUin);
        currentEpic.setDuration(Duration.ofMinutes(durationInMinutes(epicUin)));
    }

    private long durationInMinutes(int epicUin) {
        List<Subtask> listOfSubtasks = showEpicSubtasks(epicUin);
        if (listOfSubtasks == null || (listOfSubtasks.stream()
                .allMatch(subtask -> subtask == null))) {
            return 0L;
        } else {
            return listOfSubtasks.stream().map(subtask -> subtask.getDuration())
                    .filter(gottenDuration -> gottenDuration != null)
                    .mapToLong(Duration::toMinutes)
                    .sum();
        }
    }

    @Override
    public void prioritizeTasks(Task task, String typeOfChange) {
        if (task.getStartTime() != null) {
            switch (typeOfChange) {
                case "add_new" -> {
                    collectedSetOfPrioritizedTasks.add(task);
                }
                case "change" -> {
                    Task taskToChange = collectedSetOfPrioritizedTasks.stream()
                            .filter(currentTask -> currentTask.getUin() == task.getUin())
                            .findAny().get();
                    collectedSetOfPrioritizedTasks.remove(taskToChange);
                    collectedSetOfPrioritizedTasks.add(task);
                }
                case "delete" -> {
                    collectedSetOfPrioritizedTasks.remove(task);
                }
            }
        }
    }

    @Override
    public void prioritizeTasks(List<Integer> severalSubtasksUins) {

        severalSubtasksUins.stream()
                .forEach(uin -> {
                    Optional<Task> subtaskToDelete = collectedSetOfPrioritizedTasks.stream()
                            .filter(thisSubtask -> (thisSubtask.getUin() == uin))
                            .findAny();
                    if (subtaskToDelete.isPresent()) {
                        collectedSetOfPrioritizedTasks.remove(subtaskToDelete.get());
                    }
                });
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return collectedSetOfPrioritizedTasks;
    }

    @Override
    public boolean taskStartEndTimeValidator(Task newTask, int oldTaskUin) throws IntersectionException {
        boolean isIntersected;
        if (newTask.getStartTime() == null) return false;
        else {
            LocalDateTime newTaskStartTime = newTask.getStartTime();
            LocalDateTime newTaskEndTime = newTask.getEndTime();
            isIntersected = getPrioritizedTasks().stream()
                    .filter(task -> task.getUin() != oldTaskUin)
                    .anyMatch(task ->
                            ((newTaskEndTime.isBefore(task.getEndTime()) || newTaskEndTime.equals(task.getEndTime())) &
                                    newTaskEndTime.isAfter(task.getStartTime()) ||
                                    newTaskStartTime.isAfter(task.getStartTime())
                                            & (newTaskStartTime.isBefore(task.getEndTime()) ||
                                            newTaskStartTime.equals(task.getEndTime())) ||
                                    newTaskStartTime.isBefore(task.getStartTime())
                                            & newTaskEndTime.isAfter(task.getEndTime())));
            if (isIntersected) {
                throw new IntersectionException("The task intersects with the others.");
            }
        }
        return isIntersected;
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
