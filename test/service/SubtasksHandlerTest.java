package service;

import com.google.gson.Gson;
import model.Epic;
import model.Subtask;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubtasksHandlerTest {
    TaskManager taskManager;
    HttpTaskServer taskServer;
    Gson gson = HttpTaskServer.getGson();

    public SubtasksHandlerTest() throws IOException {
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
    public void testAddAndGetSubtasks() throws IOException, InterruptedException {
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

        assertEquals(201, responseTwo.statusCode());

        HttpRequest requestThree = HttpRequest.newBuilder().uri(urlTwo).POST(HttpRequest.BodyPublishers
                .ofString(subtaskTwoJson)).build();
        HttpResponse<String> responseThree = client.send(requestThree, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responseThree.statusCode());

        URI urlThree = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest requestFour = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> responseFour = client.send(requestFour, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseFour.statusCode());

        List<Subtask> subtasks = taskManager.showEpicSubtasks(1);

        assertNotNull(subtasks, "Задачи не возвращаются");
        assertEquals(2, subtasks.size(), "Некорректное количество задач");


        URI urlFour = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest requestFive = HttpRequest.newBuilder().uri(urlFour).GET().build();
        HttpResponse<String> responseFive = client.send(requestFive, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseFive.statusCode());

        URI urlFive = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest requestSix = HttpRequest.newBuilder().uri(urlFive).GET().build();
        HttpResponse<String> responseSix = client.send(requestSix, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseSix.statusCode());

        assertEquals("Test addNewSubtaskOne", gson.fromJson(responseFive.body(), Subtask.class).getName(),
                "Некорректное имя задачи");
        assertEquals("Test addNewSubtaskTwo", gson.fromJson(responseSix.body(), Subtask.class).getName(),
                "Некорректное имя задачи");
    }

    @Test
    public void testDeleteSubtaskById() throws IOException, InterruptedException {
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

        URI urlTwo = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest requestTwo = HttpRequest.newBuilder().uri(urlTwo).POST(HttpRequest.BodyPublishers
                .ofString(subtaskOneJson)).build();
        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());

        URI urlThree = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest requestThree = HttpRequest.newBuilder().uri(urlThree).DELETE().build();
        HttpResponse<String> responseThree = client.send(requestThree, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseThree.statusCode());

        List<Subtask> subtasks = taskManager.showEpicSubtasks(1);

        assertTrue(subtasks.isEmpty(), "Есть задачи в списке");
        assertEquals(0, subtasks.size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteSubtaskWrongId() throws IOException, InterruptedException {
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

        URI urlTwo = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest requestTwo = HttpRequest.newBuilder().uri(urlTwo).POST(HttpRequest.BodyPublishers
                .ofString(subtaskOneJson)).build();
        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());

        URI urlThree = URI.create("http://localhost:8080/subtasks/77");
        HttpRequest requestThree = HttpRequest.newBuilder().uri(urlThree).DELETE().build();
        HttpResponse<String> responseThree = client.send(requestThree, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, responseThree.statusCode());

        List<Subtask> subtasks = taskManager.showEpicSubtasks(1);

        assertEquals(1, subtasks.size(), "Некорректное количество задач");
    }

    @Test
    public void testAddIntersectedSubtask() throws IOException, InterruptedException {
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

        Subtask subtaskTwo = new Subtask(Duration.ofMinutes(10),
                LocalDateTime.now().plus(110, ChronoUnit.MINUTES), "Test addNewSubtaskTwo",
                "Test addNewSubtaskTwo description");
        String subtaskTwoJson = gson.toJson(subtaskTwo);

        URI urlTwo = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest requestTwo = HttpRequest.newBuilder().uri(urlTwo).POST(HttpRequest.BodyPublishers
                .ofString(subtaskOneJson)).build();
        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestThree = HttpRequest.newBuilder().uri(urlTwo).POST(HttpRequest.BodyPublishers
                .ofString(subtaskTwoJson)).build();
        HttpResponse<String> responseThree = client.send(requestThree, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, responseThree.statusCode());

        List<Subtask> subtasks = taskManager.showEpicSubtasks(1);

        assertNotNull(subtasks, "Задачи не возвращаются");
        assertEquals(1, subtasks.size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteAllSubtasks() throws IOException, InterruptedException {
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

        URI urlTwo = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest requestTwo = HttpRequest.newBuilder().uri(urlTwo).POST(HttpRequest.BodyPublishers
                .ofString(subtaskOneJson)).build();
        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());

        URI urlThree = URI.create("http://localhost:8080/subtasks/");
        HttpRequest requestThree = HttpRequest.newBuilder().uri(urlThree).DELETE().build();
        HttpResponse<String> responseThree = client.send(requestThree, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseThree.statusCode());

        List<Subtask> subtasks = taskManager.showEpicSubtasks(1);

        assertTrue(subtasks.isEmpty(), "Есть задачи в списке");
        assertEquals(0, subtasks.size(), "Некорректное количество задач");
    }
}