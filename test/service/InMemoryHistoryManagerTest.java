package service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    TaskManager taskManager;

    @BeforeEach
    void setManagers() {
        taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    }

    @Test
    void getHistoryGivesTasksInFifoOrder() {
        Task taskOne = new Task("Test addNew TaskOne", "Test addNew TaskOne description");
        taskManager.createTask(taskOne);
        int taskOneId = taskOne.getUin();
        final Task savedTaskOne = taskManager.getTask(taskOneId);

        Task taskTwo = new Task("Test addNew TaskTwo", "Test addNew TaskTwo description");
        taskManager.createTask(taskTwo);
        int taskTwoId = taskTwo.getUin();
        final Task savedTaskTwo = taskManager.getTask(taskTwoId);

        Epic epicOne = new Epic("Test addNew EpicOne", "Test addNew EpicOne description");
        taskManager.createEpic(epicOne);
        int epicOneId = epicOne.getUin();
        final Epic savedEpicOne = taskManager.getEpic(epicOneId);

        Subtask subtaskOne = new Subtask("Test addNewSubtaskOne", "Test addNewSubtaskOne description");
        taskManager.createSubtask(epicOneId, subtaskOne);
        int subtaskOneId = subtaskOne.getUin();
        final Subtask savedSubtaskOne = taskManager.getSubtask(subtaskOneId);

        Collection<Task> history = taskManager.getTasksHistoryFromInMemoryHM();
        int idSimulator = taskOneId;

        for (Task task : history) {
            assertEquals(idSimulator, task.getUin(), "UIN codes of added tasks must be in FIFO order");
            idSimulator++;
        }
    }

    @Test
    void historyDoNotSaveDuplicatesOfEachKindOfTask() {
        Task taskOne = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.createTask(taskOne);
        int taskOneId = taskOne.getUin();
        final Task savedTask = taskManager.getTask(taskOneId);
        taskManager.getTask(taskOneId);
        taskManager.getTask(taskOneId);

        Epic epicOne = new Epic("Test addNewEpicOne", "Test addNewEpicOne description");
        taskManager.createEpic(epicOne);
        int epicOneId = epicOne.getUin();
        final Epic savedEpicOne = taskManager.getEpic(epicOneId);
        taskManager.getEpic(epicOneId);
        taskManager.getEpic(epicOneId);

        Subtask subtaskOne = new Subtask("Test addNewSubtaskOne", "Test addNewSubtaskOne description");
        taskManager.createSubtask(epicOneId, subtaskOne);
        int subtaskOneId = subtaskOne.getUin();
        final Subtask savedSubtaskOne = taskManager.getSubtask(subtaskOneId);
        taskManager.getSubtask(subtaskOneId);
        taskManager.getSubtask(subtaskOneId);

        final Collection<Task> history = taskManager.getInMemoryHistoryManager().getHistory();

        assertEquals(3, history.size(), "History must include 3 tasks");
    }

    @Test
    void ifDeleteAnyKindOfTaskItIsNoLongerInHistory() {
        Task taskOne = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.createTask(taskOne);
        int taskOneId = taskOne.getUin();
        final Task savedTask = taskManager.getTask(taskOneId);
        taskManager.deleteTask(taskOneId);

        Epic epicOne = new Epic("Test addNewEpicOne", "Test addNewEpicOne description");
        taskManager.createEpic(epicOne);
        int epicOneId = epicOne.getUin();
        final Epic savedEpicOne = taskManager.getEpic(epicOneId);

        Subtask subtaskOne = new Subtask("Test addNewSubtaskOne", "Test addNewSubtaskOne description");
        taskManager.createSubtask(epicOneId, subtaskOne);
        int subtaskOneId = subtaskOne.getUin();
        final Subtask savedSubtaskOne = taskManager.getSubtask(subtaskOneId);
        taskManager.deleteSubtask(subtaskOneId);

        Collection<Task> history = taskManager.getInMemoryHistoryManager().getHistory();

        assertEquals(1, history.size(), "In History must be only one Epic");

        taskManager.deleteEpic(epicOneId);

        Collection<Task> historyTwo = taskManager.getInMemoryHistoryManager().getHistory();

        assertEquals(0, historyTwo.size(), "History must be clear");
    }
}
