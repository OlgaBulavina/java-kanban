package service;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;


public class HttpTaskServer {


    private static final int PORT = 8080;
    private HttpServer httpServer;
    protected TaskManager taskManager;
    Gson gson;


    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        this.httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        initHandlers();
    }

    private void initHandlers() {
        httpServer.createContext("/tasks", new TasksHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager, gson));
        httpServer.createContext("/epics", new EpicsHandler(taskManager, gson));
        httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
    }

    public static void main(String[] args) throws IOException {

        TaskManager taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());

        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);

//        httpTaskServer.start();
//        httpTaskServer.stop();
    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP-server started.");
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("HTTP-server stopped.");
    }

    static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter());
        return gsonBuilder.create();
    }
}

