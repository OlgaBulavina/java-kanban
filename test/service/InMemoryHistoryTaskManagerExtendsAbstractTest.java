package service;

import exception.IntersectionException;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryTaskManagerExtendsAbstractTest extends AbstractTaskManagerTest<InMemoryTaskManager> {


    protected InMemoryTaskManager makeTaskManager() {
        return new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    @BeforeEach
    @Override
    void setUp() throws IOException, IntersectionException {
        super.setUp();
    }

    @AfterEach
    @Override
    void closure() {
        super.closure();
    }

    @Test
    void checkAddingOfDifferentTypeTasks() {
        super.checkAddingAndGettingOfDifferentTypeTasks();
    }

    @Test
    void checkDifferentTypesOfTasksUpdate() throws IntersectionException {
        super.checkDifferentTypesOfTasksUpdate();
    }

    @Test
    void checkDifferentTypesOfTasksDelete() {
        super.checkDifferentTypesOfTasksDelete();
    }

    @Test
    void gettingOfPrioritizedTasksList() {
        super.gettingOfPrioritizedTasksList();
    }

    @Test
    void checkTimeCrossingOfNewTaskWithOldOnes() throws IntersectionException {
        super.checkTimeCrossingOfNewTaskWithOldOnes();
    }

    @Test
    void eachSubtaskHasItsEpic() {
        super.eachSubtaskHasItsEpic();
    }

    @Test
    void getHistoryGivesTasksInFifoOrder() {

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

        Collection<Task> history = taskManager.getTasksHistoryFromInMemoryHM();
        int idSimulator = taskOneId;

        for (Task task : history) {
            assertEquals(idSimulator, task.getUin(), "UIN codes of added tasks must be in FIFO order");
            idSimulator++;
        }
    }

    @Test
    void NoDublicatesInHistory() {
        int taskOneId = taskOne.getUin();
        final Task savedTaskOne = taskManager.getTask(taskOneId);
        taskManager.getTask(taskOneId);
        int taskTwoId = taskTwo.getUin();
        final Task savedTaskTwo = taskManager.getTask(taskTwoId);
        taskManager.getTask(taskTwoId);

        int taskThreeId = taskThree.getUin();
        final Task savedTaskThree = taskManager.getTask(taskThreeId);
        taskManager.getTask(taskThreeId);
        int epicOneId = epicOne.getUin();
        final Epic savedEpicOne = taskManager.getEpic(epicOneId);
        taskManager.getEpic(epicOneId);
        int epicTwoId = epicTwo.getUin();
        final Epic savedEpicTwo = taskManager.getEpic(epicTwoId);
        taskManager.getEpic(epicTwoId);
        int subtaskOneId = subtaskOne.getUin();
        final Subtask savedSubtaskOne = taskManager.getSubtask(subtaskOneId);
        taskManager.getSubtask(subtaskOneId);
        int subtaskTwoId = subtaskTwo.getUin();
        final Subtask savedSubtaskTwo = taskManager.getSubtask(subtaskTwoId);
        taskManager.getSubtask(subtaskTwoId);
        int subtaskThreeId = subtaskThree.getUin();
        final Subtask savedSubtaskThree = taskManager.getSubtask(subtaskThreeId);
        taskManager.getSubtask(subtaskThreeId);

        Collection<Task> history = taskManager.getTasksHistoryFromInMemoryHM();

        Set<Task> tasks = Set.of(savedTaskOne, savedTaskTwo, savedTaskThree, savedEpicOne, savedEpicTwo,
                savedSubtaskOne, savedSubtaskTwo, savedSubtaskThree);

        assertEquals(tasks.size(), history.size());
    }

    @Test
    void EmptyHistoryDoNotThrowException() {
        Collection<Task> history = taskManager.getTasksHistoryFromInMemoryHM();
        assertEquals(0, history.size());
    }

    @Test
    void DeleteTasksFromHistory() {
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

        List tasks = new LinkedList(taskManager.getInMemoryHistoryManager().getHistory());

        tasks.remove(savedTaskOne);
        taskManager.getInMemoryHistoryManager().remove(taskOneId);

        assertEquals(tasks, taskManager.getInMemoryHistoryManager().getHistory().stream().toList());

        tasks.remove(savedEpicTwo);
        taskManager.getInMemoryHistoryManager().remove(epicTwoId);

        assertEquals(tasks, taskManager.getInMemoryHistoryManager().getHistory().stream().toList());

        tasks.remove(savedSubtaskThree);
        taskManager.getInMemoryHistoryManager().remove(subtaskThreeId);

        assertEquals(tasks, taskManager.getInMemoryHistoryManager().getHistory().stream().toList());
    }
}
