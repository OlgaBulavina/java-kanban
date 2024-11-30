package service;

import com.google.gson.Gson;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PrioritizedHandlerTest {

    TaskManager taskManager;
    HttpTaskServer taskServer;
    Gson gson = HttpTaskServer.getGson();

    public PrioritizedHandlerTest() {
    }

    @BeforeEach
    void setUp() throws IOException {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        taskServer = new HttpTaskServer(taskManager);
        taskManager.getTasksHistoryFromInMemoryHM().clear();
        InMemoryTaskManager.uin = 0;

        taskServer.start();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
    }

    @Test
    public void testGetPrioritizedList() throws IOException, InterruptedException {
        Epic epicOne = new Epic("Test addNew EpicOne", "Test addNew EpicOne description");
        String epicOneJson = gson.toJson(epicOne);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicOneJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        Subtask subtaskOne = new Subtask(Duration.ofMinutes(10),
                LocalDateTime.now().plus(110, ChronoUnit.MINUTES), "Test addNewSubtaskOne",
                "Test addNewSubtaskOne description");
        String subtaskOneJson = gson.toJson(subtaskOne);

        Subtask subtaskTwo = new Subtask(Duration.ofMinutes(30),
                LocalDateTime.now().plus(160, ChronoUnit.MINUTES), "Test addNewSubtaskTwo",
                "Test addNewSubtaskTwo description");
        String subtaskTwoJson = gson.toJson(subtaskTwo);

        URI urlTwo = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest requestTwo = HttpRequest.newBuilder().uri(urlTwo).POST(HttpRequest.BodyPublishers
                .ofString(subtaskOneJson)).build();
        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestThree = HttpRequest.newBuilder().uri(urlTwo).POST(HttpRequest.BodyPublishers
                .ofString(subtaskTwoJson)).build();
        HttpResponse<String> responseThree = client.send(requestThree, HttpResponse.BodyHandlers.ofString());


        Task taskOne = new Task(Duration.ofMinutes(10), LocalDateTime.now(), "Test addNewTaskOne",
                "Test addNewTaskOne description");
        String taskOneJson = gson.toJson(taskOne);

        URI urlThree = URI.create("http://localhost:8080/tasks");
        HttpRequest requestFour = HttpRequest.newBuilder().uri(urlThree)
                .POST(HttpRequest.BodyPublishers.ofString(taskOneJson)).build();
        HttpResponse<String> responseFour = client.send(requestFour, HttpResponse.BodyHandlers.ofString());

        Task taskTwo = new Task(Duration.ofMinutes(20), LocalDateTime.now().plus(20, ChronoUnit.MINUTES),
                "Test addNewTaskTwo", "Test addNewTaskTwo description");
        String taskTwoJson = gson.toJson(taskTwo);

        HttpRequest requestFive = HttpRequest.newBuilder().uri(urlThree)
                .POST(HttpRequest.BodyPublishers.ofString(taskTwoJson)).build();
        HttpResponse<String> responseFive = client.send(requestFive, HttpResponse.BodyHandlers.ofString());


        URI urlPrioritized = URI.create("http://localhost:8080/prioritized");
        HttpRequest requestForPrioritized = HttpRequest.newBuilder().uri(urlPrioritized).GET().build();
        HttpResponse<String> responsePrioritized = client
                .send(requestForPrioritized, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responsePrioritized.statusCode());

        Set<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertNotNull(prioritizedTasks, "Задачи не возвращаются");
        assertEquals(4, prioritizedTasks.size(), "Некорректное количество задач");

    }
}