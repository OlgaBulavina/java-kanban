package service;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.ExceptionHandler;
import exception.IntersectionException;
import model.Endpoint;
import model.Task;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

class TasksHandler extends BaseHttpHandler {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final String path = "TASKS";

    public TasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    ExceptionHandler exceptionHandler = new ExceptionHandler(gson);


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            String request = exchange.getRequestURI().getPath();
            String[] requestArray = request.split("/");

            Endpoint endpoint = getEndpoint(request, exchange.getRequestMethod());

            switch (endpoint) {
                case UPDATE_TASK: {
                    try {
                        int id = Integer.parseInt(requestArray[2]);

                        if (taskManager.getTaskStorage().containsKey(id)) {
                            Task newTask = gson.fromJson(body, Task.class);
                            taskManager.updateTask(id, newTask);
                            sendOkNoDataToSendBack(exchange, "Task was successfully updated.");
                        } else {
                            sendNotFound(exchange, "Task wasn't found, cannot be updated.");
                        }
                    } catch (NumberFormatException | IOException | IntersectionException e) {
                        exceptionHandler.handleException(exchange, e);
                    }
                    break;
                }
                case POST_TASK: {
                    Task newTask = gson.fromJson(body, Task.class);
                    try {
                        taskManager.createTask(newTask);
                        sendText(exchange, "The task was posted successfully.");
                    } catch (IntersectionException exception) {
                        sendHasInteractions(exchange, exception.getMessage());
                    }
                    break;
                }
                case GET_TASK: {
                    try {
                        int id = Integer.parseInt(requestArray[2]);
                        if (taskManager.getTaskStorage().containsKey(id)) {
                            Task task = taskManager.getTask(id);
                            String json = gson.toJson(task);
                            sendText(exchange, json);
                        } else {
                            sendNotFound(exchange, "Task was not found.");
                        }
                    } catch (NumberFormatException e) {
                        exceptionHandler.handleException(exchange, e);
                    }
                    break;
                }
                case GET_ALL_TASKS: {
                    List<Task> tasks = taskManager.showAllTasks();
                    if (tasks.isEmpty()) {
                        sendOkNoDataToSendBack(exchange, "Tasks list is empty, nothing to return.");
                    } else {
                        String json = gson.toJson(tasks);
                        sendText(exchange, json);
                    }
                    break;
                }
                case DELETE_TASK: {
                    try {
                        int id = Integer.parseInt(requestArray[2]);
                        if (taskManager.getTaskStorage().containsKey(id)) {
                            taskManager.deleteTask(id);
                            sendText(exchange, "Task was successfully deleted.");
                        } else {
                            sendWasMistakeInRequest(exchange, "Task was not found for delete.");
                        }
                    } catch (NumberFormatException e) {
                        exceptionHandler.handleException(exchange, e);
                    }
                    break;
                }
                case DELETE_ALL_TASKS: {
                    taskManager.deleteAllTasks();
                    sendText(exchange, "All tasks were successfully deleted.");
                    break;
                }
                case UNKNOWN: {
                    sendNotFound(exchange, "Bad request.");
                    break;
                }
                default:
                    sendNotFound(exchange, "Bad request.");
            }
        } catch (Exception someOtherException) {
            exceptionHandler.handleException(exchange, someOtherException);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] requestArray = requestPath.split("/");
        Endpoint finalEndpoint = Endpoint.UNKNOWN;

        switch (requestMethod) {
            case "POST":
                if (checkPathLengthAndFirstPathElementFilling(path, 3, requestPath)) {
                    finalEndpoint = Endpoint.UPDATE_TASK;
                } else if (checkPathLengthAndFirstPathElementFilling(path, 2, requestPath)) {
                    finalEndpoint = Endpoint.POST_TASK;
                }
                break;
            case "GET":
                if (checkPathLengthAndFirstPathElementFilling(path, 3, requestPath)) {
                    finalEndpoint = Endpoint.GET_TASK;
                } else if (checkPathLengthAndFirstPathElementFilling(path, 2, requestPath)) {
                    finalEndpoint = Endpoint.GET_ALL_TASKS;
                }
                break;
            case "DELETE":
                if (checkPathLengthAndFirstPathElementFilling(path, 3, requestPath)) {
                    finalEndpoint = Endpoint.DELETE_TASK;
                } else if (checkPathLengthAndFirstPathElementFilling(path, 2, requestPath)) {
                    finalEndpoint = Endpoint.DELETE_ALL_TASKS;
                }
                break;
            default:
                finalEndpoint = Endpoint.UNKNOWN;
                break;
        }
        return finalEndpoint;
    }
}
