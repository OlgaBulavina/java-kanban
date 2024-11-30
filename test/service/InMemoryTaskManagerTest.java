package service;

import exception.IntersectionException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    TaskManager taskManager;

    @BeforeEach
    void setManagers() {
        taskManager = Managers.getDefault();
    }

    @Test
    void checkAddingOfDifferentTypeTasks() throws IntersectionException {
        Task taskOne = new Task(Duration.ofMinutes(10), LocalDateTime.now(), "Test addNewTaskOne",
                "Test addNewTaskOne description");
        Task taskTwo = new Task(Duration.ofMinutes(20), LocalDateTime.now().plus(20, ChronoUnit.MINUTES),
                "Test addNewTaskTwo", "Test addNewTaskTwo description");
        Task taskThree = new Task(Duration.ofMinutes(30), LocalDateTime.now().plus(45, ChronoUnit.MINUTES),
                "Test addNewTaskThree", "Test addNewTaskThree description");

        Epic epicOne = new Epic("Test addNewEpicOne", "Test addNewEpicOne description");
        Epic epicTwo = new Epic("Test addNewEpicTwo", "Test addNewEpicTwo description");
        Subtask subtaskOne = new Subtask(Duration.ofMinutes(10),
                LocalDateTime.now().plus(120, ChronoUnit.MINUTES), "Test addNewSubtaskOne",
                "Test addNewSubtaskOne description");
        Subtask subtaskTwo = new Subtask(Duration.ofMinutes(30),
                LocalDateTime.now().plus(80, ChronoUnit.MINUTES), "Test addNewSubtaskTwo",
                "Test addNewSubtaskTwo description");
        Subtask subtaskThree = new Subtask(Duration.ofMinutes(25),
                LocalDateTime.now().plus(135, ChronoUnit.MINUTES), "Test addNewSubtaskThree",
                "Test addNewSubtaskThree description");

        taskManager.createTask(taskOne);
        int taskOneId = taskOne.getUin();
        final Task savedTaskOne = taskManager.getTask(taskOneId);
        taskManager.createTask(taskTwo);
        int taskTwoId = taskTwo.getUin();
        final Task savedTaskTwo = taskManager.getTask(taskTwoId);
        taskManager.createTask(taskThree);
        int taskThreeId = taskThree.getUin();
        final Task savedTaskThree = taskManager.getTask(taskThreeId);

        taskManager.createEpic(epicOne);
        int epicOneId = epicOne.getUin();
        final Epic savedEpicOne = taskManager.getEpic(epicOneId);
        taskManager.createEpic(epicTwo);
        int epicTwoId = epicTwo.getUin();
        final Epic savedEpicTwo = taskManager.getEpic(epicTwoId);
        taskManager.getTasksHistoryFromInMemoryHM();

        taskManager.createSubtask(epicOneId, subtaskOne);
        int subtaskOneId = subtaskOne.getUin();
        final Subtask savedSubtaskOne = taskManager.getSubtask(subtaskOneId);
        taskManager.createSubtask(epicOneId, subtaskTwo);
        int subtaskTwoId = subtaskTwo.getUin();
        final Subtask savedSubtaskTwo = taskManager.getSubtask(subtaskTwoId);
        taskManager.createSubtask(epicTwoId, subtaskThree);
        int subtaskThreeId = subtaskThree.getUin();
        final Subtask savedSubtaskThree = taskManager.getSubtask(subtaskThreeId);

        final Collection<Task> history = taskManager.getTasksHistoryFromInMemoryHM();

        assertEquals(8, history.size(), "должны быть добавлены 8 Задач в историю");
        assertEquals(taskManager.getTask(taskOneId), savedTaskOne,
                "вызываемая и сохраненная Задачи должны быть равны");
        assertEquals(taskManager.getEpic(epicOneId), savedEpicOne,
                "вызываемая и сохраненная Задачи должны быть равны");
        assertEquals(taskManager.getSubtask(subtaskOneId), savedSubtaskOne,
                "вызываемая и сохраненная Задачи должны быть равны");
    }

    @Test
    void checkTasksUpdate() throws IntersectionException {
        Task taskOne = new Task(Duration.ofMinutes(10), LocalDateTime.now(), "Test addNewTaskOne",
                "Test addNewTaskOne description");
        Task taskTwo = new Task(Duration.ofMinutes(20), LocalDateTime.now().plus(20, ChronoUnit.MINUTES),
                "Test addNewTaskTwo", "Test addNewTaskTwo description");
        Task taskThree = new Task(Duration.ofMinutes(30), LocalDateTime.now().plus(45, ChronoUnit.MINUTES),
                "Test addNewTaskThree", "Test addNewTaskThree description");

        Epic epicOne = new Epic("Test addNewEpicOne", "Test addNewEpicOne description");
        Epic epicTwo = new Epic("Test addNewEpicTwo", "Test addNewEpicTwo description");
        Subtask subtaskOne = new Subtask(Duration.ofMinutes(10),
                LocalDateTime.now().plus(120, ChronoUnit.MINUTES), "Test addNewSubtaskOne",
                "Test addNewSubtaskOne description");
        Subtask subtaskTwo = new Subtask(Duration.ofMinutes(30),
                LocalDateTime.now().plus(80, ChronoUnit.MINUTES), "Test addNewSubtaskTwo",
                "Test addNewSubtaskTwo description");
        Subtask subtaskThree = new Subtask(Duration.ofMinutes(25),
                LocalDateTime.now().plus(135, ChronoUnit.MINUTES), "Test addNewSubtaskThree",
                "Test addNewSubtaskThree description");

        taskManager.createTask(taskOne);
        int taskOneId = taskOne.getUin();
        final Task savedTaskOne = taskManager.getTask(taskOneId);
        taskManager.updateTask(taskOneId, taskTwo);
        assertEquals(taskOneId, taskTwo.getUin(), "айди должен остаться тот же");
        assertNotEquals(savedTaskOne, taskManager.getTask(taskOneId), "данные задачи должны поменяться");

        taskManager.createEpic(epicOne);
        int epicOneId = epicOne.getUin();
        final Epic savedEpicOne = taskManager.getEpic(epicOneId);
        taskManager.updateEpic(epicOneId, epicTwo);
        assertEquals(epicOneId, epicTwo.getUin(), "айди должен остаться тот же");
        assertNotEquals(savedEpicOne, taskManager.getEpic(epicOneId), "данные задачи должны поменяться");

        taskManager.createSubtask(epicOneId, subtaskOne);
        int subtaskOneId = subtaskOne.getUin();
        final Subtask savedSubtaskOne = taskManager.getSubtask(subtaskOneId);
        taskManager.updateSubtask(subtaskOneId, subtaskTwo, Status.IN_PROGRESS);
        assertEquals(subtaskOneId, subtaskTwo.getUin(), "айди должен остаться тот же");
        assertNotEquals(savedSubtaskOne, taskManager.getSubtask(subtaskOneId),
                "данные задачи должны поменяться");
        assertNotEquals(savedEpicOne.getStatus(), taskManager.getEpic(epicOneId).getStatus(),
                "статус эпика должен поменяться");
    }

    @Test
    void checkTasksDelete() throws IntersectionException {
        Task taskOne = new Task(Duration.ofMinutes(10), LocalDateTime.now(), "Test addNewTaskOne",
                "Test addNewTaskOne description");
        Task taskTwo = new Task(Duration.ofMinutes(20), LocalDateTime.now().plus(20, ChronoUnit.MINUTES),
                "Test addNewTaskTwo", "Test addNewTaskTwo description");
        Task taskThree = new Task(Duration.ofMinutes(30), LocalDateTime.now().plus(45, ChronoUnit.MINUTES),
                "Test addNewTaskThree", "Test addNewTaskThree description");

        Epic epicOne = new Epic("Test addNewEpicOne", "Test addNewEpicOne description");
        Epic epicTwo = new Epic("Test addNewEpicTwo", "Test addNewEpicTwo description");
        Subtask subtaskOne = new Subtask(Duration.ofMinutes(10),
                LocalDateTime.now().plus(120, ChronoUnit.MINUTES), "Test addNewSubtaskOne",
                "Test addNewSubtaskOne description");
        Subtask subtaskTwo = new Subtask(Duration.ofMinutes(30),
                LocalDateTime.now().plus(80, ChronoUnit.MINUTES), "Test addNewSubtaskTwo",
                "Test addNewSubtaskTwo description");
        Subtask subtaskThree = new Subtask(Duration.ofMinutes(25),
                LocalDateTime.now().plus(135, ChronoUnit.MINUTES), "Test addNewSubtaskThree",
                "Test addNewSubtaskThree description");

        taskManager.createTask(taskOne);
        int taskOneId = taskOne.getUin();
        final Task savedTaskOne = taskManager.getTask(taskOneId);
        taskManager.createTask(taskTwo);
        int taskTwoId = taskTwo.getUin();
        final Task savedTaskTwo = taskManager.getTask(taskTwoId);
        taskManager.createTask(taskThree);
        int taskThreeId = taskThree.getUin();
        final Task savedTaskThree = taskManager.getTask(taskThreeId);

        taskManager.createEpic(epicOne);
        int epicOneId = epicOne.getUin();
        final Epic savedEpicOne = taskManager.getEpic(epicOneId);
        taskManager.createEpic(epicTwo);
        int epicTwoId = epicOne.getUin();
        final Epic savedEpicTwo = taskManager.getEpic(epicTwoId);

        taskManager.createSubtask(epicOneId, subtaskOne);
        int subtaskOneId = subtaskOne.getUin();
        final Subtask savedSubtaskOne = taskManager.getSubtask(subtaskOneId);
        taskManager.createSubtask(epicOneId, subtaskTwo);
        int subtaskTwoId = subtaskOne.getUin();
        final Subtask savedSubtaskTwo = taskManager.getSubtask(subtaskTwoId);
        taskManager.createSubtask(epicTwoId, subtaskThree);
        int subtaskThreeId = subtaskOne.getUin();
        final Subtask savedSubtaskThree = taskManager.getSubtask(subtaskThreeId);

        final int savedAllTasksLength = taskManager.showAllTasks().size();
        final int savedAllEpicsLength = taskManager.showAllEpics().size();
        final int savedAllSubtasksLength = taskManager.showAllSubtasks().size();

        taskManager.deleteTask(taskOneId);
        taskManager.deleteSubtask(subtaskOneId);
        taskManager.deleteEpic(epicOneId);

        assertNotEquals(savedAllTasksLength, taskManager.showAllTasks().size(),
                "длина списка должна отличаться на 1");
        assertNotEquals(savedAllSubtasksLength, taskManager.showAllSubtasks().size(),
                "длина списка должна отличаться на 1");
        assertNotEquals(savedAllEpicsLength, taskManager.showAllEpics().size(),
                "длина списка должна отличаться на 1");

        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasksForAllEpics();

        assertEquals(0, taskManager.showAllTasks().size(), "длина списка должна быть нулевой");
        assertEquals(0, taskManager.showAllSubtasks().size(), "длина списка должна быть нулевой");
        assertEquals(0, taskManager.showAllEpics().size(), "длина списка должна быть нулевой");
    }

    @Test
    void gettingOfPrioritizedTasksList() throws IntersectionException {
        Task taskOne = new Task(Duration.ofMinutes(10), LocalDateTime.now(), "Test addNewTaskOne",
                "Test addNewTaskOne description");
        Task taskTwo = new Task(Duration.ofMinutes(20), LocalDateTime.now().plus(20, ChronoUnit.MINUTES),
                "Test addNewTaskTwo", "Test addNewTaskTwo description");
        Task taskThree = new Task(Duration.ofMinutes(30), LocalDateTime.now().plus(45, ChronoUnit.MINUTES),
                "Test addNewTaskThree", "Test addNewTaskThree description");

        Epic epicOne = new Epic("Test addNewEpicOne", "Test addNewEpicOne description");
        Epic epicTwo = new Epic("Test addNewEpicTwo", "Test addNewEpicTwo description");
        Subtask subtaskOne = new Subtask(Duration.ofMinutes(10),
                LocalDateTime.now().plus(120, ChronoUnit.MINUTES), "Test addNewSubtaskOne",
                "Test addNewSubtaskOne description");
        Subtask subtaskTwo = new Subtask(Duration.ofMinutes(30),
                LocalDateTime.now().plus(80, ChronoUnit.MINUTES), "Test addNewSubtaskTwo",
                "Test addNewSubtaskTwo description");
        Subtask subtaskThree = new Subtask(Duration.ofMinutes(25),
                LocalDateTime.now().plus(135, ChronoUnit.MINUTES), "Test addNewSubtaskThree",
                "Test addNewSubtaskThree description");

        taskManager.createTask(taskOne);
        taskManager.createTask(taskTwo);
        taskManager.createTask(taskThree);

        taskManager.createEpic(epicOne);
        taskManager.createEpic(epicTwo);

        taskManager.createSubtask(epicOne.getUin(), subtaskOne);
        taskManager.createSubtask(epicOne.getUin(), subtaskTwo);
        taskManager.createSubtask(epicTwo.getUin(), subtaskThree);

        TreeSet<Task> setOfTasks = new TreeSet<>(Comparator.comparing(task -> task.getStartTime()));

        setOfTasks.add(taskOne);
        setOfTasks.add(taskTwo);
        setOfTasks.add(taskThree);
        setOfTasks.add(subtaskOne);
        setOfTasks.add(subtaskTwo);
        setOfTasks.add(subtaskThree);

        assertEquals(setOfTasks, taskManager.getPrioritizedTasks());
    }

    @Test
    void gettingOfPrioritizedTasksListIfStartTimeIsNull() throws IntersectionException {
        Task taskOne = new Task("Test addNewTaskOne",
                "Test addNewTaskOne description");
        Task taskTwo = new Task(Duration.ofMinutes(20), LocalDateTime.now().plus(20, ChronoUnit.MINUTES),
                "Test addNewTaskTwo", "Test addNewTaskTwo description");
        Task taskThree = new Task(Duration.ofMinutes(30), LocalDateTime.now().plus(45, ChronoUnit.MINUTES),
                "Test addNewTaskThree", "Test addNewTaskThree description");

        Epic epicOne = new Epic("Test addNewEpicOne", "Test addNewEpicOne description");
        Epic epicTwo = new Epic("Test addNewEpicTwo", "Test addNewEpicTwo description");
        Subtask subtaskOne = new Subtask("Test addNewSubtaskOne",
                "Test addNewSubtaskOne description");
        Subtask subtaskTwo = new Subtask(Duration.ofMinutes(30),
                LocalDateTime.now().plus(80, ChronoUnit.MINUTES), "Test addNewSubtaskTwo",
                "Test addNewSubtaskTwo description");
        Subtask subtaskThree = new Subtask(Duration.ofMinutes(25),
                LocalDateTime.now().plus(135, ChronoUnit.MINUTES), "Test addNewSubtaskThree",
                "Test addNewSubtaskThree description");

        taskManager.createTask(taskOne);
        taskManager.createTask(taskTwo);
        taskManager.createTask(taskThree);

        taskManager.createEpic(epicOne);
        taskManager.createEpic(epicTwo);

        taskManager.createSubtask(epicOne.getUin(), subtaskOne);
        taskManager.createSubtask(epicOne.getUin(), subtaskTwo);
        taskManager.createSubtask(epicTwo.getUin(), subtaskThree);

        TreeSet<Task> setOfTasks = new TreeSet<>(Comparator.comparing(task -> task.getStartTime()));

        setOfTasks.add(taskTwo);
        setOfTasks.add(taskThree);
        setOfTasks.add(subtaskTwo);
        setOfTasks.add(subtaskThree);

        assertEquals(setOfTasks, taskManager.getPrioritizedTasks());
    }

    @Test
    void checkTimeCrossingOfNewTaskWithOldOnes() throws IntersectionException {

        // s, e - startTime, endTime of new Task; S, E - startTime, EndTime of Task(s) in memory

        Task taskOne = new Task(Duration.ofMinutes(60), LocalDateTime.now().plus(60, ChronoUnit.MINUTES),
                "Test addNewTaskOne", "Test addNewTaskOne description");
        taskManager.createTask(taskOne);

        Task taskTwo = new Task(Duration.ofMinutes(10), LocalDateTime.now(),
                "Test addNewTaskTwo", "Test addNewTaskTwo description");

        assertFalse(taskManager.taskStartEndTimeValidator(taskTwo, 0), "case seSE");

        Task taskThree = new Task(Duration.ofMinutes(20), LocalDateTime.now().plus(50, ChronoUnit.MINUTES),
                "Test addNewTaskThree", "Test addNewTaskThree description");

        assertThrows(IntersectionException.class, () -> taskManager
                .taskStartEndTimeValidator(taskThree, 0), "case sSeE");

        Task taskFour = new Task(Duration.ofMinutes(10), LocalDateTime.now().plus(70, ChronoUnit.MINUTES),
                "Test addNewTaskThree", "Test addNewTaskThree description");

        assertThrows(IntersectionException.class, () -> taskManager
                .taskStartEndTimeValidator(taskFour, 0), "case SseE");

        Task taskFive = new Task(Duration.ofMinutes(60), LocalDateTime.now().plus(80, ChronoUnit.MINUTES),
                "Test addNewTaskThree", "Test addNewTaskThree description");

        assertThrows(IntersectionException.class, () -> taskManager
                .taskStartEndTimeValidator(taskFive, 0), "caseSsEe");

        Task taskSix = new Task(Duration.ofMinutes(60), LocalDateTime.now().plus(300, ChronoUnit.MINUTES),
                "Test addNewTaskThree", "Test addNewTaskThree description");

        assertFalse(taskManager.taskStartEndTimeValidator(taskSix, 0), "case SEse");

        Task taskSeven = new Task(Duration.ofMinutes(120), LocalDateTime.now().plus(50, ChronoUnit.MINUTES),
                "Test addNewTaskThree", "Test addNewTaskThree description");

        assertThrows(IntersectionException.class, () -> taskManager
                .taskStartEndTimeValidator(taskSeven, 0), "case sSEe");
    }
}