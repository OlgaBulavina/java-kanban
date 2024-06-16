package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class InMemoryTaskManagerTest {

    TaskManager taskManager;

    Task taskOne = new Task("Test addNewTaskOne", "Test addNewTaskOne description");
    Task taskTwo = new Task("Test addNewTaskTwo", "Test addNewTaskTwo description");
    Task taskThree = new Task("Test addNewTaskThree", "Test addNewTaskThree description");

    Epic epicOne = new Epic("Test addNewEpicOne", "Test addNewEpicOne description");
    Epic epicTwo = new Epic("Test addNewEpicTwo", "Test addNewEpicTwo description");
    Subtask subtaskOne = new Subtask("Test addNewSubtaskOne", "Test addNewSubtaskOne description");
    Subtask subtaskTwo = new Subtask("Test addNewSubtaskTwo", "Test addNewSubtaskTwo description");
    Subtask subtaskThree = new Subtask("Test addNewSubtaskThree", "Test addNewSubtaskThree description");

    @BeforeEach
    void setManagers() {
        taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    }

    @Test
    void checkAddingOfDifferentTypeTasks() {
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
        assertEquals(taskManager.getTask(taskOneId), savedTaskOne, "вызываемая и сохраненная Задачи должны быть равны");
        assertEquals(taskManager.getEpic(epicOneId), savedEpicOne, "вызываемая и сохраненная Задачи должны быть равны");
        assertEquals(taskManager.getSubtask(subtaskOneId), savedSubtaskOne, "вызываемая и сохраненная Задачи должны быть равны");
    }

    @Test
    void checkTasksUpdate() {
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
        assertNotEquals(savedSubtaskOne, taskManager.getSubtask(subtaskOneId), "данные задачи должны поменяться");
        assertNotEquals(savedEpicOne.getStatus(), taskManager.getEpic(epicOneId).getStatus(), "статус эпика должен поменяться");
    }

    @Test
    void checkTasksDelete() {
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

        assertNotEquals(savedAllTasksLength, taskManager.showAllTasks().size(), "длина списка должна отличаться на 1");
        assertNotEquals(savedAllSubtasksLength, taskManager.showAllSubtasks().size(), "длина списка должна отличаться на 1");
        assertNotEquals(savedAllEpicsLength, taskManager.showAllEpics().size(), "длина списка должна отличаться на 1");

        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasksForAllEpics();

        assertEquals(0, taskManager.showAllTasks().size(), "длина списка должна быть нулевой");
        assertEquals(0, taskManager.showAllSubtasks().size(), "длина списка должна быть нулевой");
        assertEquals(0, taskManager.showAllEpics().size(), "длина списка должна быть нулевой");
    }
}