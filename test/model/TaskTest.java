package model;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.Managers;
import service.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TaskTest {
    TaskManager taskManager = Managers.getDefault();
    HistoryManager historyManager = Managers.getDefaultHistory();

    @AfterEach
    void clearTasksHistory(){
        historyManager.getHistory().clear();
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.createTask(task);
        int taskId = task.getUin();
        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.showAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
        historyManager.getHistory().clear();
    }

    @Test
    void add() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.createTask(task);
        taskManager.getTask(task.getUin());
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
        history.clear();
    }

    @Test
    void taskEqualsById() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.createTask(task);
        int taskId = task.getUin();
        final Task savedTaskOne = taskManager.getTask(taskId);
        final Task savedTaskTwo = taskManager.getTask(taskId);

        assertEquals(savedTaskOne, savedTaskTwo, "Вызванные задачи по одному айди не совпадают");
        historyManager.getHistory().clear();
    }
}

