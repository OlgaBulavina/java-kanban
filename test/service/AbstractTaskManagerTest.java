package service;

import exception.IntersectionException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractTaskManagerTest<T extends TaskManager> {
    T taskManager;

    Task taskOne;
    Task taskTwo;
    Task taskThree;
    Epic epicOne;
    Epic epicTwo;
    Subtask subtaskOne;
    Subtask subtaskTwo;
    Subtask subtaskThree;
    LocalDateTime countStart;

    protected abstract T makeTaskManager();

    @BeforeEach
    void setUp() throws IOException, IntersectionException {

        this.taskManager = makeTaskManager();

        countStart = LocalDateTime.of(2024, 01, 02, 12, 30, 00);
        taskOne = new Task(Duration.ofMinutes(10), countStart, "Test addNewTaskOne",
                "Test addNewTaskOne description");
        taskTwo = new Task(Duration.ofMinutes(20), countStart.plus(20, ChronoUnit.MINUTES),
                "Test addNewTaskTwo", "Test addNewTaskTwo description");
        taskThree = new Task(Duration.ofMinutes(30), countStart.plus(45, ChronoUnit.MINUTES),
                "Test addNewTaskThree", "Test addNewTaskThree description");

        epicOne = new Epic("Test addNewEpicOne", "Test addNewEpicOne description");
        epicTwo = new Epic("Test addNewEpicTwo", "Test addNewEpicTwo description");
        subtaskOne = new Subtask(Duration.ofMinutes(10),
                countStart.plus(120, ChronoUnit.MINUTES), "Test addNewSubtaskOne",
                "Test addNewSubtaskOne description");
        subtaskTwo = new Subtask(Duration.ofMinutes(30),
                countStart.plus(80, ChronoUnit.MINUTES), "Test addNewSubtaskTwo",
                "Test addNewSubtaskTwo description");
        subtaskThree = new Subtask(Duration.ofMinutes(25),
                countStart.plus(135, ChronoUnit.MINUTES), "Test addNewSubtaskThree",
                "Test addNewSubtaskThree description");

        taskManager.createTask(taskOne);
        taskManager.createTask(taskTwo);
        taskManager.createTask(taskThree);

        taskManager.createEpic(epicOne);
        taskManager.createEpic(epicTwo);

        taskManager.createSubtask(epicOne.getUin(), subtaskOne);
        taskManager.createSubtask(epicOne.getUin(), subtaskTwo);
        taskManager.createSubtask(epicTwo.getUin(), subtaskThree);
    }

    @AfterEach
    void closure() {
        InMemoryTaskManager.uin = 0;
    }

    @Test
    void checkAddingAndGettingOfDifferentTypeTasks() {

        int taskOneId = taskOne.getUin();
        final Task savedTaskOne = taskManager.getTask(taskOneId);

        int taskTwoId = taskTwo.getUin();
        final Task savedTaskTwo = taskManager.getTask(taskTwoId);

        int taskThreeId = taskThree.getUin();
        final Task savedTaskThree = taskManager.getTask(taskThreeId);

        int epicOneId = epicOne.getUin();
        final Epic savedEpicOne = taskManager.getEpic(epicOneId);

        int epicTwoId = epicTwo.getUin();
        final Epic savedEpicTwo = taskManager.getEpic(epicTwoId);

        int subtaskOneId = subtaskOne.getUin();
        final Subtask savedSubtaskOne = taskManager.getSubtask(subtaskOneId);

        int subtaskTwoId = subtaskTwo.getUin();
        final Subtask savedSubtaskTwo = taskManager.getSubtask(subtaskTwoId);

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
    void checkDifferentTypesOfTasksUpdate() throws IntersectionException {

        int taskOneId = taskOne.getUin();
        final Task savedTaskOne = taskManager.getTask(taskOneId);

        Task taskOneUpdated = new Task(Duration.ofMinutes(5), countStart.plus(5, ChronoUnit.MINUTES),
                "Test addUpdatedTaskOne", "Test addUpdatedTaskOne description");
        taskManager.updateTask(taskOneId, taskOneUpdated);

        assertEquals(taskOneId, taskOneUpdated.getUin(), "айди должен остаться тот же");
        assertNotEquals(savedTaskOne, taskManager.getTask(taskOneId), "данные задачи должны поменяться");


        int epicOneId = epicOne.getUin();
        final Epic savedEpicOne = taskManager.getEpic(epicOneId);

        Epic epicOneUpdated = new Epic("Test addUpdatedEpicOne", "Test addUpdatedEpicOne description");
        taskManager.updateEpic(epicOneId, epicOneUpdated);

        assertEquals(epicOneId, epicOneUpdated.getUin(), "айди должен остаться тот же");
        assertNotEquals(savedEpicOne, taskManager.getEpic(epicOneId), "данные задачи должны поменяться");


        int subtaskOneId = subtaskOne.getUin();
        final Subtask savedSubtaskOne = taskManager.getSubtask(subtaskOneId);

        Subtask subtaskOneUpdated = new Subtask(Duration.ofMinutes(5),
                countStart.plus(115, ChronoUnit.MINUTES), "Test addUpdatedSubtaskOne",
                "Test addUpdatedSubtaskOne description");
        taskManager.updateSubtask(subtaskOneId, subtaskOneUpdated, Status.IN_PROGRESS);

        assertEquals(subtaskOneId, subtaskOneUpdated.getUin(), "айди должен остаться тот же");
        assertNotEquals(savedSubtaskOne, taskManager.getSubtask(subtaskOneId),
                "данные задачи должны поменяться");
    }

    @Test
    void checkDifferentTypesOfTasksDelete() {

        int taskOneId = taskOne.getUin();

        int epicOneId = epicOne.getUin();

        int subtaskOneId = subtaskOne.getUin();

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
    void gettingOfPrioritizedTasksList() {

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
    void checkTimeCrossingOfNewTaskWithOldOnes() throws IntersectionException {

        // s, e - startTime, endTime of new Task; S, E - startTime, EndTime of Task(s) in memory

        Task notCrossedTaskOne = new Task(Duration.ofMinutes(10), countStart.minus(1, ChronoUnit.DAYS),
                "Test notCrossedTaskOne", "Test notCrossedTaskOne description");

        assertFalse(taskManager.taskStartEndTimeValidator(notCrossedTaskOne, 0), "case seSE");

        Task crossedTaskOne = new Task(Duration.ofMinutes(30), countStart.minus(20, ChronoUnit.MINUTES),
                "Test crossedTaskOne", "Test crossedTaskOne description");

        assertThrows(IntersectionException.class, () -> taskManager
                .taskStartEndTimeValidator(crossedTaskOne, 0), "case sSeE");

        Task crossedTaskTwo = new Task(Duration.ofMinutes(10), countStart.plus(50, ChronoUnit.MINUTES),
                "Test crossedTaskTwo", "Test crossedTaskTwo description");

        assertThrows(IntersectionException.class, () -> taskManager
                .taskStartEndTimeValidator(crossedTaskTwo, 0), "case SseE");

        Task crossedTaskThree = new Task(Duration.ofMinutes(80), countStart.plus(140, ChronoUnit.MINUTES),
                "Test addNewTaskThree", "Test addNewTaskThree description");

        assertThrows(IntersectionException.class, () -> taskManager
                .taskStartEndTimeValidator(crossedTaskThree, 0), "caseSsEe");

        Task notCrossedTaskTwo = new Task(Duration.ofMinutes(60), countStart.plus(300, ChronoUnit.MINUTES),
                "Test notCrossedTaskTwo", "Test notCrossedTaskTwo description");

        assertFalse(taskManager.taskStartEndTimeValidator(notCrossedTaskTwo, 0), "case SEse");

        Task crossedTaskFour = new Task(Duration.ofMinutes(200), countStart.minus(20, ChronoUnit.MINUTES),
                "Test crossedTaskFour", "Test crossedTaskFour description");

        assertThrows(IntersectionException.class, () -> taskManager
                .taskStartEndTimeValidator(crossedTaskFour, 0), "case sSEe");
    }

    @Test
    void eachSubtaskHasItsEpic() {
        assertEquals(Epic.class, taskManager.getEpic(subtaskOne.getThisEpicUin()).getClass());
        assertEquals(Epic.class, taskManager.getEpic(subtaskTwo.getThisEpicUin()).getClass());
        assertEquals(Epic.class, taskManager.getEpic(subtaskThree.getThisEpicUin()).getClass());
    }
}
