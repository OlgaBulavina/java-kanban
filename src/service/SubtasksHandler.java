package service;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.ExceptionHandler;
import exception.IntersectionException;
import model.Endpoint;
import model.Epic;
import model.Subtask;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

class SubtasksHandler extends BaseHttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final String path = "SUBTASKS";

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    ExceptionHandler exceptionHandler = new ExceptionHandler(gson);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {

            String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            String request = exchange.getRequestURI().getPath();
            String[] requestArray = request.split("/");

            Endpoint endpoint = getEndpoint(request, exchange.getRequestMethod(), body);
            Subtask newSubtask = gson.fromJson(body, Subtask.class);
            System.out.println(newSubtask);
            System.out.println(body);

            switch (endpoint) {
                case UPDATE_SUBTASK: {
                    try {
                        int epicId = Integer.parseInt(requestArray[2]);

                        int subtaskId = newSubtask.getUin();

                        List<Epic> epicList = taskManager.showAllEpics();

                        if (epicList.contains(taskManager.getEpic(epicId)) &&
                                taskManager.getSubtask(subtaskId).getName() != null) {
                            taskManager.updateSubtask(subtaskId, newSubtask, newSubtask.getStatus());
                            sendOkNoDataToSendBack(exchange, "Subtask was successfully updated.");
                        } else {
                            sendNotFound(exchange, "Subtask/Epic ID wasn't found, Subtask cannot be updated.");
                        }
                    } catch (NumberFormatException | IntersectionException | IOException e) {
                        exceptionHandler.handleException(exchange, e);
                    }
                    break;
                }
                case POST_SUBTASK: {
                    try {
                        int epicId = Integer.parseInt(requestArray[2]);

                        List<Epic> epicList = taskManager.showAllEpics();

                        if (epicList.contains(taskManager.getEpic(epicId))) {
                            taskManager.createSubtask(epicId, newSubtask);
                            sendOkNoDataToSendBack(exchange, "Subtask was posted successfully.");
                        } else {
                            sendWasMistakeInRequest(exchange, "Epic ID for current Subtask is incorrect.");
                        }
                    } catch (NumberFormatException | IntersectionException | IOException e) {
                        exceptionHandler.handleException(exchange, e);
                    }
                    break;
                }
                case GET_SUBTASK: {
                    try {
                        int subtaskId = Integer.parseInt(requestArray[2]);
                        if (taskManager.showAllSubtasks().contains(taskManager.getSubtask(subtaskId)) &&
                                taskManager.getSubtask(subtaskId).getName() != null) {
                            String json = gson.toJson(taskManager.getSubtask(subtaskId));
                            sendText(exchange, json);
                        } else {
                            sendNotFound(exchange, "Subtask wasn't found.");
                        }
                    } catch (NumberFormatException | IOException e) {
                        exceptionHandler.handleException(exchange, e);
                    }
                    break;
                }
                case GET_ALL_SUBTASKS: {
                    List<Subtask> subtasks = taskManager.showAllSubtasks();
                    if (subtasks.isEmpty()) {
                        sendOkNoDataToSendBack(exchange, "Subtasks list is empty, nothing to return.");
                    } else {
                        String json = gson.toJson(subtasks);
                        sendText(exchange, json);
                    }
                    break;
                }
                case DELETE_SUBTASK: {
                    try {
                        int subtaskId = Integer.parseInt(requestArray[2]);
                        if (taskManager.showAllSubtasks().contains(taskManager.getSubtask(subtaskId)) &&
                                taskManager.getSubtask(subtaskId).getName() != null) {
                            taskManager.deleteSubtask(subtaskId);
                            sendText(exchange, "Subtask was successfully deleted.");
                        } else {
                            sendWasMistakeInRequest(exchange, "Subtask was not found for delete.");
                        }
                    } catch (NumberFormatException | IOException e) {
                        exceptionHandler.handleException(exchange, e);
                    }
                    break;
                }
                case DELETE_ALL_SUBTASKS: {
                    taskManager.deleteAllSubtasksForAllEpics();
                    sendText(exchange, "All subtasks were successfully deleted.");
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

    private Endpoint getEndpoint(String requestPath, String requestMethod, String body) {
        String[] requestArray = requestPath.split("/");
        Endpoint finalEndpoint = Endpoint.UNKNOWN;

        switch (requestMethod) {
            case "POST":
                if (checkPathLengthAndFirstPathElementFilling(path, 3, requestPath)) {
                    Subtask currentSubtask = gson.fromJson(body, Subtask.class);
                    if (taskManager.getSubtask(currentSubtask.getUin()).getName() != null) {
                        finalEndpoint = Endpoint.POST_SUBTASK;
                    } else finalEndpoint = Endpoint.UPDATE_SUBTASK;
                }
                break;
            case "GET":
                if (checkPathLengthAndFirstPathElementFilling(path, 3, requestPath)) {
                    finalEndpoint = Endpoint.GET_SUBTASK;
                } else if (checkPathLengthAndFirstPathElementFilling(path, 2, requestPath)) {
                    finalEndpoint = Endpoint.GET_ALL_SUBTASKS;
                }
                break;
            case "DELETE":
                if (checkPathLengthAndFirstPathElementFilling(path, 3, requestPath)) {
                    finalEndpoint = Endpoint.DELETE_SUBTASK;
                } else if (checkPathLengthAndFirstPathElementFilling(path, 2, requestPath)) {
                    finalEndpoint = Endpoint.DELETE_ALL_SUBTASKS;
                }
                break;
            default:
                finalEndpoint = Endpoint.UNKNOWN;
                break;
        }
        return finalEndpoint;
    }

}
