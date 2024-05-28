package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static service.Managers.getDefault;
import static service.Managers.getDefaultHistory;

class ManagersTest {
    Object taskManager = getDefault();
    Object historyManager = getDefaultHistory();

    @Test
    void testingTMClass() {
        assertEquals(taskManager.getClass(), InMemoryTaskManager.class, "классы не равны");
    }

    @Test
    void testingHMClass() {
        assertEquals(historyManager.getClass(), InMemoryHistoryManager.class, "классы не равны");
    }

}