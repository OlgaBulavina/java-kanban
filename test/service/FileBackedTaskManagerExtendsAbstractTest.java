package service;

import model.ManagerReadException;
import model.ManagerSaveException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileBackedTaskManagerExtendsAbstractTest extends AbstractTaskManagerTest<FileBackedTaskManager> {

    protected FileBackedTaskManager makeTaskManager() {
        return new FileBackedTaskManager(new InMemoryHistoryManager());
    }

    @BeforeEach
    @Override
    void setUp() throws IOException {
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
    void checkDifferentTypesOfTasksUpdate() {
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
    void checkTimeCrossingOfNewTaskWithOldOnes() {
        super.checkTimeCrossingOfNewTaskWithOldOnes();
    }

    @Test
    void eachSubtaskHasItsEpic() {
        super.eachSubtaskHasItsEpic();
    }

    @Test
    void FBTMThrowsExceptionIfFileNotFoundWhileReading() {
        assertThrows(ManagerReadException.class, () -> {
            taskManager.loadFromFile(new File(""));
        });
    }

    @Test
    void FBTMThrowsExceptionIfFileNotFoundWhileWriting() {
        taskManager.storageFile = new File("");
        assertThrows(ManagerSaveException.class, () -> {
            taskManager.getSubtask(subtaskOne.getUin());
            taskManager.save();
        });
    }
}
