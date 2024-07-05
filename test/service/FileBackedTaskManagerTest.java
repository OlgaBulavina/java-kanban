package service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.io.File.createTempFile;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {
    FileBackedTaskManager fileBackedTaskManager;

    @Test
    void checkLoadingInFile() throws IOException {
        File directory = new File("C:\\Users\\olyab\\IdeaProjects\\java-kanban\\");
        File file = createTempFile("backup",".csv", directory);
        fileBackedTaskManager = new FileBackedTaskManager(new InMemoryHistoryManager());
        fileBackedTaskManager.storageFile = file;
        Task taskOne = new Task("Test addNewTaskOne", "Test addNewTaskOne description");
        Task taskTwo = new Task("Test addNewTaskTwo", "Test addNewTaskTwo description");
        Task taskThree = new Task("Test addNewTaskThree", "Test addNewTaskThree description");

        Epic epicOne = new Epic("Test addNewEpicOne", "Test addNewEpicOne description");
        Epic epicTwo = new Epic("Test addNewEpicTwo", "Test addNewEpicTwo description");
        Subtask subtaskOne = new Subtask("Test addNewSubtaskOne", "Test addNewSubtaskOne description");
        Subtask subtaskTwo = new Subtask("Test addNewSubtaskTwo", "Test addNewSubtaskTwo description");
        Subtask subtaskThree = new Subtask("Test addNewSubtaskThree", "Test addNewSubtaskThree description");

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
        sb1.append("UIN,TYPE,NAME,DESCRIPTION,STATUS,EPIC_NUMBER(FOR SUBTASK)" + "\n");

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

        while(bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            sb2.append(line + "\n");
        }

        assertEquals(sb1.toString(), sb2.toString());

        bufferedReader.close();
        fileReader.close();
        file.deleteOnExit();
    }

    @Test
    void checkLoadingFromFile() {
        File directory = new File("C:\\Users\\olyab\\IdeaProjects\\java-kanban\\");
        File file = new File(directory + "\\testStorage.csv");

        fileBackedTaskManager = FileBackedTaskManager.loadFromFie(file);

        System.out.println(fileBackedTaskManager.showAllTasks());
        System.out.println(fileBackedTaskManager.showAllEpics());
        System.out.println(fileBackedTaskManager.showAllSubtasks());


    }

    @Test
    void checkLoadingAndSavingOfEmptyFile() throws IOException{
        File directory = new File("C:\\Users\\olyab\\IdeaProjects\\java-kanban\\");
        File file = createTempFile("backup",".csv", directory);
        fileBackedTaskManager = new FileBackedTaskManager(new InMemoryHistoryManager());
        fileBackedTaskManager.storageFile = file;

        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        StringBuilder sb = new StringBuilder();

        while(bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            sb.append(line);
        }

        assertEquals("", sb.toString());

        bufferedReader.close();
        fileReader.close();
        file.deleteOnExit();
    }

    void checkUinIsBiggestUinAfterLoading() {

    }

}
