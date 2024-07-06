package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.io.File.createTempFile;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {
    FileBackedTaskManager fileBackedTaskManager;

    @Test
    void checkLoadingInFile() throws IOException {
        File directory = new File("C:\\Users\\olyab\\IdeaProjects\\java-kanban\\");
        File file = createTempFile("backup", ".csv", directory);
        fileBackedTaskManager = new FileBackedTaskManager(new InMemoryHistoryManager());
        fileBackedTaskManager.storageFile = file;
        Task taskOne = new Task("Test addNewTaskOne", "Test addNewTaskOne description");
        Task taskTwo = new Task("Test addNewTaskTwo", "Test addNewTaskTwo description");
        Task taskThree = new Task("Test addNewTaskThree", "Test addNewTaskThree description");

        Epic epicOne = new Epic("Test addNewEpicOne", "Test addNewEpicOne description");
        Epic epicTwo = new Epic("Test addNewEpicTwo", "Test addNewEpicTwo description");
        Subtask subtaskOne = new Subtask("Test addNewSubtaskOne", "Test addNewSubtaskOne description");
        Subtask subtaskTwo = new Subtask("Test addNewSubtaskTwo", "Test addNewSubtaskTwo description");
        Subtask subtaskThree = new Subtask("Test addNewSubtaskThree",
                "Test addNewSubtaskThree description");

        fileBackedTaskManager.createTask(taskOne);
        fileBackedTaskManager.createTask(taskTwo);
        fileBackedTaskManager.createTask(taskThree);

        fileBackedTaskManager.createEpic(epicOne);
        fileBackedTaskManager.createEpic(epicTwo);

        fileBackedTaskManager.createSubtask(epicOne.getUin(), subtaskOne);
        fileBackedTaskManager.createSubtask(epicOne.getUin(), subtaskTwo);
        fileBackedTaskManager.createSubtask(epicTwo.getUin(), subtaskThree);

        final Map<Integer, Task> taskMap = fileBackedTaskManager.getTaskStorage();
        final Map<Integer, Epic> epicMap = fileBackedTaskManager.getEpicStorage();
        final Map<Integer, HashMap<Integer, Subtask>> subtaskMap = fileBackedTaskManager.getSubtaskStorage();

        StringBuilder sb1 = new StringBuilder();
        sb1.append(fileBackedTaskManager.heading);

        for (Task task : taskMap.values()) {
            sb1.append(task.toString() + "\n");
        }
        for (Epic epic : epicMap.values()) {
            sb1.append(epic.toString() + "\n");
        }
        for (HashMap<Integer, Subtask> innerHashMap : subtaskMap.values()) {
            for (Subtask subtask : innerHashMap.values()) {
                sb1.append(subtask.toString() + "\n");
            }
        }

        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder sb2 = new StringBuilder();

        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            sb2.append(line + "\n");
        }

        assertEquals(sb1.toString(), sb2.toString());

        bufferedReader.close();
        fileReader.close();
        file.deleteOnExit();
    }

    @Test
    void checkLoadingFromFile() throws IOException {
        File file = new File("C:\\Users\\olyab\\IdeaProjects\\java-kanban\\testStorage.csv");

        fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        StringBuilder sbFromMemory = new StringBuilder();
        sbFromMemory.append(fileBackedTaskManager.heading);
        ArrayList tasks = fileBackedTaskManager.showAllTasks();
        for (Object task : tasks) {
            sbFromMemory.append(task.toString() + "\n");
        }
        ArrayList epics = fileBackedTaskManager.showAllEpics();
        for (Object epic : epics) {
            sbFromMemory.append(epic.toString() + "\n");
        }
        ArrayList subtasks = fileBackedTaskManager.showAllSubtasks();
        for (Object subtask : subtasks) {
            sbFromMemory.append(subtask.toString() + "\n");
        }

        FileReader fileReader = new FileReader(file);
        BufferedReader br = new BufferedReader(fileReader);
        StringBuilder sbFromFile = new StringBuilder();
        while (br.ready()) {
            sbFromFile.append(br.readLine() + "\n");
        }
        br.close();
        fileReader.close();

        assertEquals(sbFromFile.toString(), sbFromMemory.toString());
    }

    @Test
    void checkLoadingAndSavingOfEmptyFile() throws IOException {
        File directory = new File("C:\\Users\\olyab\\IdeaProjects\\java-kanban\\");
        File file = createTempFile("backup", ".csv", directory);
        fileBackedTaskManager = new FileBackedTaskManager(new InMemoryHistoryManager());
        fileBackedTaskManager.storageFile = file;

        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        StringBuilder sb = new StringBuilder();

        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            sb.append(line);
        }

        assertEquals("", sb.toString());

        bufferedReader.close();
        fileReader.close();
        file.deleteOnExit();
    }

    @Test
    void checkUinIsBiggestUinAfterLoading() throws IOException {
        File file = new File("C:\\Users\\olyab\\IdeaProjects\\java-kanban\\testStorage.csv");

        fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        int taskCounter = fileBackedTaskManager.showAllSubtasks().size()
                + fileBackedTaskManager.showAllTasks().size() + fileBackedTaskManager.showAllEpics().size();

        assertEquals(taskCounter, fileBackedTaskManager.uin,
                "ID задачи должен быть равен наибольшему ID в списке задач из файла");
    }

    @Test
    void checkLoadingOfChangesInFileAfterTasksDelete() throws IOException {
        File directory = new File("C:\\Users\\olyab\\IdeaProjects\\java-kanban\\");
        File file = createTempFile("backup", ".csv", directory);
        fileBackedTaskManager = new FileBackedTaskManager(new InMemoryHistoryManager());
        fileBackedTaskManager.storageFile = file;
        Task taskOne = new Task("Test addNewTaskOne", "Test addNewTaskOne description");
        Task taskTwo = new Task("Test addNewTaskTwo", "Test addNewTaskTwo description");
        Task taskThree = new Task("Test addNewTaskThree", "Test addNewTaskThree description");

        Epic epicOne = new Epic("Test addNewEpicOne", "Test addNewEpicOne description");
        Epic epicTwo = new Epic("Test addNewEpicTwo", "Test addNewEpicTwo description");
        Subtask subtaskOne = new Subtask("Test addNewSubtaskOne", "Test addNewSubtaskOne description");
        Subtask subtaskTwo = new Subtask("Test addNewSubtaskTwo", "Test addNewSubtaskTwo description");
        Subtask subtaskThree = new Subtask("Test addNewSubtaskThree",
                "Test addNewSubtaskThree description");

        fileBackedTaskManager.createTask(taskOne);
        fileBackedTaskManager.createTask(taskTwo);
        fileBackedTaskManager.createTask(taskThree);

        fileBackedTaskManager.createEpic(epicOne);
        fileBackedTaskManager.createEpic(epicTwo);

        fileBackedTaskManager.createSubtask(epicOne.getUin(), subtaskOne);
        fileBackedTaskManager.createSubtask(epicOne.getUin(), subtaskTwo);
        fileBackedTaskManager.createSubtask(epicTwo.getUin(), subtaskThree);

        fileBackedTaskManager.deleteTask(taskOne.getUin());
        fileBackedTaskManager.deleteTask(taskTwo.getUin());
        fileBackedTaskManager.deleteEpic(epicTwo.getUin());
        fileBackedTaskManager.deleteSubtask(subtaskOne.getUin());

        StringBuilder sbFromMemory = new StringBuilder();
        sbFromMemory.append(fileBackedTaskManager.heading);
        ArrayList tasks = fileBackedTaskManager.showAllTasks();
        for (Object task : tasks) {
            sbFromMemory.append(task.toString() + "\n");
        }
        ArrayList epics = fileBackedTaskManager.showAllEpics();
        for (Object epic : epics) {
            sbFromMemory.append(epic.toString() + "\n");
        }
        ArrayList subtasks = fileBackedTaskManager.showAllSubtasks();
        for (Object subtask : subtasks) {
            sbFromMemory.append(subtask.toString() + "\n");
        }

        FileReader fileReader = new FileReader(file);
        BufferedReader br = new BufferedReader(fileReader);
        StringBuilder sbFromFile = new StringBuilder();
        while (br.ready()) {
            sbFromFile.append(br.readLine() + "\n");
        }
        br.close();
        fileReader.close();

        assertEquals(sbFromFile.toString(), sbFromMemory.toString());
        file.deleteOnExit();
    }

    @Test
    void checkLoadingOfChangesInFileAfterTasksUpdate() throws IOException {
        File directory = new File("C:\\Users\\olyab\\IdeaProjects\\java-kanban\\");
        File file = createTempFile("backup", ".csv", directory);
        fileBackedTaskManager = new FileBackedTaskManager(new InMemoryHistoryManager());
        fileBackedTaskManager.storageFile = file;
        Task taskOne = new Task("Test addNewTaskOne", "Test addNewTaskOne description");
        Task taskTwo = new Task("Test addNewTaskTwo", "Test addNewTaskTwo description");
        Task taskThree = new Task("Test addNewTaskThree", "Test addNewTaskThree description");

        Epic epicOne = new Epic("Test addNewEpicOne", "Test addNewEpicOne description");
        Epic epicTwo = new Epic("Test addNewEpicTwo", "Test addNewEpicTwo description");
        Subtask subtaskOne = new Subtask("Test addNewSubtaskOne", "Test addNewSubtaskOne description");
        Subtask subtaskTwo = new Subtask("Test addNewSubtaskTwo", "Test addNewSubtaskTwo description");
        Subtask subtaskThree = new Subtask("Test addNewSubtaskThree",
                "Test addNewSubtaskThree description");

        fileBackedTaskManager.createTask(taskOne);
        fileBackedTaskManager.createTask(taskTwo);
        fileBackedTaskManager.createTask(taskThree);

        fileBackedTaskManager.createEpic(epicOne);
        fileBackedTaskManager.createEpic(epicTwo);

        fileBackedTaskManager.createSubtask(epicOne.getUin(), subtaskOne);
        fileBackedTaskManager.createSubtask(epicOne.getUin(), subtaskTwo);
        fileBackedTaskManager.createSubtask(epicTwo.getUin(), subtaskThree);

        fileBackedTaskManager.updateTask(taskOne.getUin(), new Task("Test addUpdatedTaskOne",
                "Test addUpdatedTaskOne description", Status.IN_PROGRESS));
        fileBackedTaskManager.updateEpic(epicOne.getUin(), new Epic("Test addUpdatedEpicOne",
                "Test addUpdatedEpicOne description"));
        fileBackedTaskManager.updateSubtask(subtaskOne.getUin(), new Subtask("Test addUpdatedSubtaskOne",
                "Test addUpdatedSubtaskOne description"), Status.IN_PROGRESS);

        StringBuilder sbFromMemory = new StringBuilder();
        sbFromMemory.append(fileBackedTaskManager.heading);
        ArrayList tasks = fileBackedTaskManager.showAllTasks();
        for (Object task : tasks) {
            sbFromMemory.append(task.toString() + "\n");
        }
        ArrayList epics = fileBackedTaskManager.showAllEpics();
        for (Object epic : epics) {
            sbFromMemory.append(epic.toString() + "\n");
        }
        ArrayList subtasks = fileBackedTaskManager.showAllSubtasks();
        for (Object subtask : subtasks) {
            sbFromMemory.append(subtask.toString() + "\n");
        }

        FileReader fileReader = new FileReader(file);
        BufferedReader br = new BufferedReader(fileReader);
        StringBuilder sbFromFile = new StringBuilder();
        while (br.ready()) {
            sbFromFile.append(br.readLine() + "\n");
        }
        br.close();
        fileReader.close();

        assertEquals(sbFromFile.toString(), sbFromMemory.toString());
        file.deleteOnExit();
    }
}
