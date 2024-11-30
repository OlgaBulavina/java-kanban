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
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HistoryHandlerTest {
    TaskManager taskManager;
    HttpTaskServer taskServer;
    Gson gson = HttpTaskServer.getGson();

    public HistoryHandlerTest() {
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
    public void testGetHistoryList() throws IOException, InterruptedException {
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


        URI urlSubtaskOne = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest requestGetSubtaskOne = HttpRequest.newBuilder().uri(urlSubtaskOne).GET().build();
        HttpResponse<String> responseGetSubtaskOne = client
                .send(requestGetSubtaskOne, HttpResponse.BodyHandlers.ofString());
        URI urlSubtaskTwo = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest requestGetSubtaskTwo = HttpRequest.newBuilder().uri(urlSubtaskTwo).GET().build();
        HttpResponse<String> responseGetSubtaskTwo = client
                .send(requestGetSubtaskOne, HttpResponse.BodyHandlers.ofString());

        URI urlEpicOne = URI.create("http://localhost:8080/epics/1");
        HttpRequest requestGetEpic = HttpRequest.newBuilder().uri(urlEpicOne).GET().build();
        HttpResponse<String> responseGetEpic = client.send(requestGetEpic, HttpResponse.BodyHandlers.ofString());

        URI urlTaskOne = URI.create("http://localhost:8080/tasks/4");
        HttpRequest requestGetTaskOne = HttpRequest.newBuilder().uri(urlTaskOne).GET().build();
        HttpResponse<String> responseGetTaskOne = client.send(requestGetTaskOne, HttpResponse.BodyHandlers.ofString());
        URI urlTaskTwo = URI.create("http://localhost:8080/tasks/5");
        HttpRequest requestGetTaskTwo = HttpRequest.newBuilder().uri(urlTaskTwo).GET().build();
        HttpResponse<String> responseGetTaskTwo = client.send(requestGetTaskTwo, HttpResponse.BodyHandlers.ofString());


        URI urlHistory = URI.create("http://localhost:8080/history");
        HttpRequest requestHistory = HttpRequest.newBuilder().uri(urlHistory).GET().build();
        HttpResponse<String> responseHistory = client.send(requestHistory, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseHistory.statusCode());

        Collection<Task> TasksHistory = taskManager.getTasksHistoryFromInMemoryHM();

        System.out.println(TasksHistory);
        System.out.println(responseHistory.body());

        assertNotNull(TasksHistory, "Задачи не возвращаются");
        assertEquals(5, TasksHistory.size(), "Некорректное количество вызванных задач");
    }
}