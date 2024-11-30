package service;

import com.google.gson.Gson;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TasksHandlerTest {

    TaskManager taskManager;
    HttpTaskServer taskServer;
    Gson gson = HttpTaskServer.getGson();

    public TasksHandlerTest() {
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
    public void testAddTask() throws IOException, InterruptedException {
        Task taskOne = new Task(Duration.ofMinutes(10), LocalDateTime.now(), "Test addNewTaskOne",
                "Test addNewTaskOne description");
        String taskJson = gson.toJson(taskOne);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasks = taskManager.showAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Некорректное количество задач");
        assertEquals("Test addNewTaskOne", tasks.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task taskOne = new Task(Duration.ofMinutes(10), LocalDateTime.now(), "Test addNewTaskOne",
                "Test addNewTaskOne description");
        String taskOneJson = gson.toJson(taskOne);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskOneJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        Task taskTwo = new Task(Duration.ofMinutes(20), LocalDateTime.now().plus(20, ChronoUnit.MINUTES),
                "Test addNewTaskTwo", "Test addNewTaskTwo description");
        String taskTwoJson = gson.toJson(taskTwo);
        URI urlTwo = URI.create("http://localhost:8080/tasks/1");

        HttpRequest requestTwo = HttpRequest.newBuilder().uri(urlTwo).POST(HttpRequest.BodyPublishers
                .ofString(taskTwoJson)).build();
        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());


        assertEquals(201, responseTwo.statusCode());

        List<Task> tasks = taskManager.showAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Некорректное количество задач");
        assertEquals("Test addNewTaskTwo", tasks.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task taskOne = new Task(Duration.ofMinutes(10), LocalDateTime.now(), "Test addNewTaskOne",
                "Test addNewTaskOne description");
        String taskOneJson = gson.toJson(taskOne);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskOneJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        URI urlTwo = URI.create("http://localhost:8080/tasks/1");
        HttpRequest requestTwo = HttpRequest.newBuilder().uri(urlTwo).GET().build();

        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseTwo.statusCode());

        List<Task> tasks = taskManager.showAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Некорректное количество задач");
        assertEquals("Test addNewTaskOne", gson.fromJson(responseTwo.body(), Task.class).getName(),
                "Некорректное имя задачи");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task taskOne = new Task(Duration.ofMinutes(10), LocalDateTime.now(), "Test addNewTaskOne",
                "Test addNewTaskOne description");
        String taskOneJson = gson.toJson(taskOne);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskOneJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        Task taskTwo = new Task(Duration.ofMinutes(20), LocalDateTime.now().plus(20, ChronoUnit.MINUTES),
                "Test addNewTaskTwo", "Test addNewTaskTwo description");
        String taskTwoJson = gson.toJson(taskTwo);

        HttpRequest requestTwo = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskTwoJson)).build();
        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestThree = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> responseThree = client.send(requestThree, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseThree.statusCode());

        List<Task> tasks = taskManager.showAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(2, tasks.size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task taskOne = new Task(Duration.ofMinutes(10), LocalDateTime.now(), "Test addNewTaskOne",
                "Test addNewTaskOne description");
        String taskOneJson = gson.toJson(taskOne);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskOneJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        Task taskTwo = new Task(Duration.ofMinutes(20), LocalDateTime.now().plus(20, ChronoUnit.MINUTES),
                "Test addNewTaskTwo", "Test addNewTaskTwo description");
        String taskTwoJson = gson.toJson(taskTwo);

        HttpRequest requestTwo = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskTwoJson)).build();
        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());

        URI urlTwo = URI.create("http://localhost:8080/tasks/1");

        HttpRequest requestThree = HttpRequest.newBuilder().uri(urlTwo).DELETE().build();
        HttpResponse<String> responseThree = client.send(requestThree, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseThree.statusCode());

        List<Task> tasks = taskManager.showAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteTasks() throws IOException, InterruptedException {
        Task taskOne = new Task(Duration.ofMinutes(10), LocalDateTime.now(), "Test addNewTaskOne",
                "Test addNewTaskOne description");
        String taskOneJson = gson.toJson(taskOne);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskOneJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        Task taskTwo = new Task(Duration.ofMinutes(20), LocalDateTime.now().plus(20, ChronoUnit.MINUTES),
                "Test addNewTaskTwo", "Test addNewTaskTwo description");
        String taskTwoJson = gson.toJson(taskTwo);

        HttpRequest requestTwo = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskTwoJson)).build();
        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestThree = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> responseThree = client.send(requestThree, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseThree.statusCode());

        List<Task> tasks = taskManager.showAllTasks();

        assertTrue(tasks.isEmpty(), "В списке есть задачи");
        assertEquals(0, tasks.size(), "Некорректное количество задач");
    }

    @Test
    public void testAddIntersectedTask() throws IOException, InterruptedException {
        Task taskOne = new Task(Duration.ofMinutes(10), LocalDateTime.now(), "Test addNewTaskOne",
                "Test addNewTaskOne description");
        String taskJson = gson.toJson(taskOne);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task taskTwo = new Task(Duration.ofMinutes(20), LocalDateTime.now(),
                "Test addNewTaskTwo", "Test addNewTaskTwo description");
        String taskTwoJson = gson.toJson(taskTwo);

        HttpRequest requestTwo = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskTwoJson)).build();
        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());


        assertEquals(406, responseTwo.statusCode());

        List<Task> tasks = taskManager.showAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Некорректное количество задач");
    }

    @Test
    public void testAddTaskNotProperFormat() throws IOException, InterruptedException {
        Task taskOne = new Task(Duration.ofMinutes(10), LocalDateTime.now(), "Test addNewTaskOne",
                "Test addNewTaskOne description");
        String taskJson = gson.toJson(taskOne);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task taskTwo = new Task(Duration.ofMinutes(20), LocalDateTime.now(),
                "Test addNewTaskTwo", "Test addNewTaskTwo description");
        String taskTwoJson = new String("qwerty");

        HttpRequest requestTwo = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskTwoJson)).build();
        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());


        assertEquals(400, responseTwo.statusCode());

        List<Task> tasks = taskManager.showAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetTaskNotProperId() throws IOException, InterruptedException {
        Task taskOne = new Task(Duration.ofMinutes(10), LocalDateTime.now(), "Test addNewTaskOne",
                "Test addNewTaskOne description");
        String taskOneJson = gson.toJson(taskOne);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskOneJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        URI urlTwo = URI.create("http://localhost:8080/tasks/55");
        HttpRequest requestTwo = HttpRequest.newBuilder().uri(urlTwo).GET().build();

        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, responseTwo.statusCode());

        List<Task> tasks = taskManager.showAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Некорректное количество задач");
    }
}