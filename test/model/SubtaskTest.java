package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    TaskManager taskManager;

    @BeforeEach
    void setManagers() {
        taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    }

    @Test
    void addNewSubtask() {
        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
        taskManager.createEpic(epic);
        int epicId = epic.getUin();

        Subtask subtask = new Subtask(Duration.ofMinutes(30),
                LocalDateTime.now().plus(15, ChronoUnit.MINUTES), "Test addNewSubtaskOne",
                "Test addNewSubtaskOne description");
        taskManager.createSubtask(epicId, subtask);
        int subtaskId = subtask.getUin();

        final Subtask savedSubtask = taskManager.getSubtask(subtaskId);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");

        final List<Subtask> subtasks = taskManager.showEpicSubtasks(epicId);

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void add() {
        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
        taskManager.createEpic(epic);
        int epicId = epic.getUin();

        Subtask subtask = new Subtask(Duration.ofMinutes(30),
                LocalDateTime.now().plus(15, ChronoUnit.MINUTES), "Test addNewSubtaskOne",
                "Test addNewSubtaskOne description");
        taskManager.createSubtask(epicId, subtask);
        taskManager.getSubtask(subtask.getUin());
        final Collection<Task> history = taskManager.getInMemoryHistoryManager().getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void subtaskEqualsById() {
        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
        taskManager.createEpic(epic);
        int epicId = epic.getUin();
        Subtask subtask = new Subtask(Duration.ofMinutes(30),
                LocalDateTime.now().plus(15, ChronoUnit.MINUTES), "Test addNewSubtaskOne",
                "Test addNewSubtaskOne description");
        taskManager.createSubtask(epicId, subtask);
        int subtaskId = subtask.getUin();

        final Subtask savedSubtaskOne = taskManager.getSubtask(subtaskId);
        final Subtask savedSubtaskTwo = taskManager.getSubtask(subtaskId);

        assertEquals(savedSubtaskOne, savedSubtaskTwo, "Вызванные задачи по одному айди не совпадают");
    }

    @Test
    void checkEpicStartDTAndEndDTAndDurationIfNoEntryData() {
        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
        taskManager.createEpic(epic);
        int epicId = epic.getUin();
        Subtask subtask = new Subtask("Test addNewSubtaskOne",
                "Test addNewSubtaskOne description");
        taskManager.createSubtask(epicId, subtask);
        Duration defaultDuration = Duration.of(0L, ChronoUnit.MINUTES);

        assertNull(subtask.getStartTime());
        assertEquals(subtask.getDuration(), defaultDuration);
        assertNull(subtask.getEndTime());
    }
}
