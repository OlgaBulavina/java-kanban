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
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    TaskManager taskManager;

    @BeforeEach
    void setManagers() {
        taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    }

    @Test
    void addNewEpic() {
        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
        taskManager.createEpic(epic);
        int epicId = epic.getUin();
        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final List<Epic> epics = taskManager.showAllEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void add() {
        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
        taskManager.createEpic(epic);
        taskManager.getEpic(epic.getUin());
        final Collection<Task> history = taskManager.getInMemoryHistoryManager().getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void epicEqualsById() {
        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
        taskManager.createEpic(epic);
        int epicId = epic.getUin();
        final Epic savedEpicOne = taskManager.getEpic(epicId);
        final Epic savedEpicTwo = taskManager.getEpic(epicId);

        assertEquals(savedEpicOne, savedEpicTwo, "Вызванные задачи по одному айди не совпадают");
    }

    @Test
    void cannotAddEpicAsSubtask() {
        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
        taskManager.createEpic(epic);
        int epicId = epic.getUin();
        final Epic savedEpic = taskManager.getEpic(epicId);
        assertFalse(savedEpic.getClass().equals(Subtask.class), "Эпик может быть добавлен в качестве сабтаска");
    }

    @Test
    void checkEpicStartDTAndEndDTAndDuration() {
        Epic epicOne = new Epic("Test addNewEpicOne", "Test addNewEpicOne description");
        Subtask subtaskOne = new Subtask(Duration.ofMinutes(10),
                LocalDateTime.now().plus(15, ChronoUnit.MINUTES), "Test addNewSubtaskOne",
                "Test addNewSubtaskOne description");
        Subtask subtaskTwo = new Subtask(Duration.ofMinutes(30),
                LocalDateTime.now().plus(30, ChronoUnit.MINUTES), "Test addNewSubtaskTwo",
                "Test addNewSubtaskTwo description");
        Subtask subtaskThree = new Subtask(Duration.ofMinutes(25),
                LocalDateTime.now().plus(120, ChronoUnit.MINUTES), "Test addNewSubtaskThree",
                "Test addNewSubtaskThree description");

        taskManager.createEpic(epicOne);

        taskManager.createSubtask(epicOne.getUin(), subtaskOne);
        taskManager.createSubtask(epicOne.getUin(), subtaskTwo);
        taskManager.createSubtask(epicOne.getUin(), subtaskThree);

        assertEquals(subtaskOne.startTime, epicOne.startTime);

        LocalDateTime maxLDT = List.of(subtaskOne.getEndTime(),
                        subtaskTwo.getEndTime(), subtaskThree.getEndTime()).stream()
                .max(LocalDateTime::compareTo).get();

        assertTrue(maxLDT.equals(epicOne.getEndTime()));

        Duration totalSubtasksDuration = subtaskOne.getDuration().plus(subtaskTwo.getDuration())
                .plus(subtaskThree.getDuration());

        assertEquals(totalSubtasksDuration, epicOne.getDuration());
    }
    @Test
    void checkEpicStartDTAndEndDTAndDurationIfNoSubtasks(){
        Epic epicOne = new Epic("Test addNewEpicOne", "Test addNewEpicOne description");

        Duration defaultDuration= Duration.of(0L, ChronoUnit.MINUTES);
        assertNull(epicOne.startTime);
        assertEquals(epicOne.getDuration(), defaultDuration);
        assertNull(epicOne.getEndTime());
    }
}