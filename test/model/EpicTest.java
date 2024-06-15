package model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    TaskManager taskManager = Managers.getDefault();

    @AfterEach
    void clearTasksHistory(){
        taskManager.getInMemoryHistoryManager().getHistory().clear();
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
        final List<Task> history = taskManager.getInMemoryHistoryManager().getHistory();
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
}