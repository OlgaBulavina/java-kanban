package service;

import model.Status;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class InMemoryHistoryManagerTest {
    TaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());

    @AfterEach
    void clearTasksHistory(){
        taskManager.getInMemoryHistoryManager().getHistory().clear();
    }

    @Test
    void savingTaskChangeInHistory() {
        Task taskOne = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.createTask(taskOne);
        int taskOneId = taskOne.getUin();
        final Task savedTask = taskManager.getTask(taskOneId);

        Task taskTwo = new Task("Test changedTask", "Test changedTask description", Status.IN_PROGRESS);
        taskManager.updateTask(taskOneId, taskTwo);
        int taskTwoId = taskTwo.getUin();
        final Task changedTask = taskManager.getTask(taskTwoId);

        List<Task> history = taskManager.getInMemoryHistoryManager().getHistory();
        assertEquals(history.get(0).getUin(), history.get(1).getUin(), "айди должны быть равны");
        assertNotEquals(history.get(0), history.get(1), "информация в Задачах должна быть разной");
    }

    @Test
    void checkHistoryListLengthIfMoreThan10TasksCalled() {
        Task taskOne = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.createTask(taskOne);
        int taskOneId = taskOne.getUin();
        for (int i = 0; i < 15; i++) {
            taskManager.getTask(taskOneId);
        }
        assertEquals(10, taskManager.getInMemoryHistoryManager().getHistory().size(), "Длина списка должна оставаться 10");
    }
}
