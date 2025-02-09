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

class EpicsHandlerTest {

    TaskManager taskManager;
    HttpTaskServer taskServer;
    Gson gson = HttpTaskServer.getGson();

    public EpicsHandlerTest() {
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

    private HttpRequest sendPostRequest(URI url, String json) {
        return HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(json)).build();
    }

    private HttpRequest sendGetRequest(URI url) {
        return HttpRequest.newBuilder().uri(url).GET().build();
    }

    private HttpRequest sendDeleteRequest(URI url) {
        return HttpRequest.newBuilder().uri(url).DELETE().build();
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epicOne = new Epic("Test addNew EpicOne", "Test addNew EpicOne description");
        String epicOneJson = gson.toJson(epicOne);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = sendPostRequest(url, epicOneJson);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> epics = taskManager.showAllEpics();

        assertNotNull(epics, "Задачи не возвращаются");
        assertEquals(1, epics.size(), "Некорректное количество задач");
        assertEquals("Test addNew EpicOne", epics.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        Epic epicOne = new Epic("Test addNew EpicOne", "Test addNew EpicOne description");
        String epicOneJson = gson.toJson(epicOne);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = sendPostRequest(url, epicOneJson);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Epic epicTwo = new Epic("Test addNew EpicTwo", "Test addNew EpicTwo description");
        String epicTwoJson = gson.toJson(epicTwo);

        URI urlTwo = URI.create("http://localhost:8080/epics/1");
        HttpRequest requestTwo = sendPostRequest(urlTwo, epicTwoJson);

        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responseTwo.statusCode());

        List<Epic> epics = taskManager.showAllEpics();

        assertNotNull(epics, "Задачи не возвращаются");
        assertEquals(1, epics.size(), "Некорректное количество задач");
        assertEquals("Test addNew EpicTwo", epics.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epicOne = new Epic("Test addNew EpicOne", "Test addNew EpicOne description");
        String epicOneJson = gson.toJson(epicOne);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = sendPostRequest(url, epicOneJson);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        URI urlTwo = URI.create("http://localhost:8080/epics/1");
        HttpRequest requestTwo = sendGetRequest(urlTwo);

        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseTwo.statusCode());

        List<Epic> epics = taskManager.showAllEpics();

        assertNotNull(epics, "Задачи не возвращаются");
        assertEquals(1, epics.size(), "Некорректное количество задач");
        assertEquals("Test addNew EpicOne", epics.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        Epic epicOne = new Epic("Test addNew EpicOne", "Test addNew EpicOne description");
        String epicOneJson = gson.toJson(epicOne);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = sendPostRequest(url, epicOneJson);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Epic epicTwo = new Epic("Test addNew EpicTwo", "Test addNew EpicTwo description");
        String epicTwoJson = gson.toJson(epicTwo);

        HttpRequest requestTwo = sendPostRequest(url, epicTwoJson);
        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());


        HttpRequest requestThree = sendGetRequest(url);
        HttpResponse<String> responseThree = client.send(requestThree, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseThree.statusCode());

        List<Epic> epics = taskManager.showAllEpics();

        assertNotNull(epics, "Задачи не возвращаются");
        assertEquals(2, epics.size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epicOne = new Epic("Test addNew EpicOne", "Test addNew EpicOne description");
        String epicOneJson = gson.toJson(epicOne);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = sendPostRequest(url, epicOneJson);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Epic epicTwo = new Epic("Test addNew EpicTwo", "Test addNew EpicTwo description");
        String epicTwoJson = gson.toJson(epicTwo);

        HttpRequest requestTwo = sendPostRequest(url, epicTwoJson);
        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());

        URI urlTwo = URI.create("http://localhost:8080/epics/1");
        HttpRequest requestThree = sendDeleteRequest(urlTwo);
        HttpResponse<String> responseThree = client.send(requestThree, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseThree.statusCode());

        List<Epic> epics = taskManager.showAllEpics();

        assertNotNull(epics, "Задачи не возвращаются");
        assertEquals(1, epics.size(), "Некорректное количество задач");
        assertEquals("Test addNew EpicTwo", epics.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteEpics() throws IOException, InterruptedException {
        Epic epicOne = new Epic("Test addNew EpicOne", "Test addNew EpicOne description");
        String epicOneJson = gson.toJson(epicOne);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = sendPostRequest(url, epicOneJson);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Epic epicTwo = new Epic("Test addNew EpicTwo", "Test addNew EpicTwo description");
        String epicTwoJson = gson.toJson(epicTwo);

        HttpRequest requestTwo = sendPostRequest(url, epicTwoJson);
        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestThree = sendDeleteRequest(url);
        HttpResponse<String> responseThree = client.send(requestThree, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseThree.statusCode());

        List<Epic> epics = taskManager.showAllEpics();

        assertTrue(epics.isEmpty(), "В списке есть задачи");
        assertEquals(0, epics.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epicOne = new Epic("Test addNew EpicOne", "Test addNew EpicOne description");
        String epicOneJson = gson.toJson(epicOne);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = sendPostRequest(url, epicOneJson);
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
        HttpRequest requestTwo = sendPostRequest(urlTwo, subtaskOneJson);
        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responseTwo.statusCode());

        HttpRequest requestThree = sendPostRequest(urlTwo, subtaskTwoJson);
        HttpResponse<String> responseThree = client.send(requestThree, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responseThree.statusCode());

        URI urlThree = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest requestFour = sendGetRequest(url);
        HttpResponse<String> responseFour = client.send(requestFour, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseFour.statusCode());

        List<Subtask> subtasks = taskManager.showEpicSubtasks(1);

        assertNotNull(subtasks, "Задачи не возвращаются");
        assertEquals(2, subtasks.size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteEpicSubtasks() throws IOException, InterruptedException {
        Epic epicOne = new Epic("Test addNew EpicOne", "Test addNew EpicOne description");
        String epicOneJson = gson.toJson(epicOne);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = sendPostRequest(url, epicOneJson);
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
        HttpRequest requestTwo = sendPostRequest(urlTwo, subtaskOneJson);
        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responseTwo.statusCode());

        HttpRequest requestThree = sendPostRequest(urlTwo, subtaskTwoJson);
        HttpResponse<String> responseThree = client.send(requestThree, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responseThree.statusCode());

        URI urlThree = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest requestFour = sendDeleteRequest(url);
        HttpResponse<String> responseFour = client.send(requestFour, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseFour.statusCode());

        List<Subtask> subtasks = taskManager.showEpicSubtasks(1);

        assertTrue(subtasks.isEmpty(), "Задачи не возвращаются");
        assertEquals(0, subtasks.size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteEpicIncorrectId() throws IOException, InterruptedException {
        Epic epicOne = new Epic("Test addNew EpicOne", "Test addNew EpicOne description");
        String epicOneJson = gson.toJson(epicOne);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = sendPostRequest(url, epicOneJson);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Epic epicTwo = new Epic("Test addNew EpicTwo", "Test addNew EpicTwo description");
        String epicTwoJson = gson.toJson(epicTwo);

        HttpRequest requestTwo = sendPostRequest(url, epicTwoJson);
        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());

        URI urlTwo = URI.create("http://localhost:8080/epics/66");
        HttpRequest requestThree = sendDeleteRequest(urlTwo);
        HttpResponse<String> responseThree = client.send(requestThree, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, responseThree.statusCode());

        List<Epic> epics = taskManager.showAllEpics();

        assertNotNull(epics, "Задачи не возвращаются");
        assertEquals(2, epics.size(), "Некорректное количество задач");
    }
}