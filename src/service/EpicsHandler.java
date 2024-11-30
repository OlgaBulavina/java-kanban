package service;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.ExceptionHandler;
import model.Endpoint;
import model.Epic;
import model.Subtask;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

class EpicsHandler extends BaseHttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final String path = "EPICS";

    public EpicsHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    ExceptionHandler exceptionHandler = new ExceptionHandler(gson);

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try (exchange) {

            String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            String request = exchange.getRequestURI().getPath();
            String[] requestArray = request.split("/");

            Endpoint endpoint = getEndpoint(request, exchange.getRequestMethod());

            switch (endpoint) {
                case UPDATE_EPIC: {
                    try {
                        int id = Integer.parseInt(requestArray[2]);
                        if (taskManager.getEpicStorage().containsKey(id)) {
                            Epic newEpic = gson.fromJson(body, Epic.class);
                            taskManager.updateEpic(id, newEpic);
                            sendOkNoDataToSendBack(exchange, "Epic was successfully updated.");
                        } else {
                            sendNotFound(exchange, "Epic wasn't found, cannot be updated.");
                        }
                    } catch (NumberFormatException | IOException e) {
                        exceptionHandler.handleException(exchange, e);
                    }
                    break;
                }
                case POST_EPIC: {
                    Epic newEpic = gson.fromJson(body, Epic.class);
                    taskManager.createEpic(newEpic);
                    sendOkNoDataToSendBack(exchange, "Epic was posted successfully.");
                    break;
                }
                case GET_EPIC: {
                    try {
                        int id = Integer.parseInt(requestArray[2]);
                        if (taskManager.getEpicStorage().containsKey(id)) {
                            Epic epic = taskManager.getEpic(id);
                            String json = gson.toJson(epic);
                            sendText(exchange, json);
                        } else {
                            sendNotFound(exchange, "Epic was not found.");
                        }
                    } catch (NumberFormatException | IOException e) {
                        exceptionHandler.handleException(exchange, e);
                    }
                    break;
                }
                case GET_ALL_EPICS: {
                    List<Epic> epics = taskManager.showAllEpics();
                    if (epics.isEmpty()) {
                        sendOkNoDataToSendBack(exchange, "Epics list is empty, nothing to return.");
                    } else {
                        String json = gson.toJson(epics);
                        sendText(exchange, json);
                    }
                    break;
                }
                case GET_ALL_SUBTASKS_FOR_ONE_EPIC: {
                    try {
                        int id = Integer.parseInt(requestArray[2]);
                        if (taskManager.getEpicStorage().containsKey(id)) {
                            List<Subtask> subtasks = taskManager.showEpicSubtasks(id);
                            String json = gson.toJson(subtasks);
                            sendText(exchange, json);
                        } else {
                            sendNotFound(exchange, "Epic was not found. Cannot show subtasks.");
                        }
                    } catch (NumberFormatException | IOException e) {
                        exceptionHandler.handleException(exchange, e);
                    }
                    break;
                }
                case DELETE_ALL_SUBTASKS_FOR_ONE_EPIC: {
                    try {
                        int id = Integer.parseInt(requestArray[2]);
                        if (taskManager.getEpicStorage().containsKey(id)) {
                            taskManager.deleteAllSubtasksForOneEpic(id);
                            sendText(exchange, "Subtasks were successfully deleted.");
                        } else {
                            sendWasMistakeInRequest(exchange, "Epic was not found, cannot delete subtasks.");
                        }
                    } catch (NumberFormatException e) {
                        exceptionHandler.handleException(exchange, e);
                    }
                    break;
                }
                case DELETE_EPIC: {
                    try {
                        int id = Integer.parseInt(requestArray[2]);
                        if (taskManager.getEpicStorage().containsKey(id)) {
                            taskManager.deleteEpic(id);
                            sendText(exchange, "Epic was successfully deleted.");
                        } else {
                            sendWasMistakeInRequest(exchange, "Epic was not found, cannot delete.");
                        }
                    } catch (NumberFormatException e) {
                        exceptionHandler.handleException(exchange, e);
                    }
                    break;
                }
                case DELETE_ALL_EPICS: {
                    taskManager.deleteAllEpics();
                    sendText(exchange, "All epics were successfully deleted.");
                    break;
                }
                case UNKNOWN: {
                    sendNotFound(exchange, "Bad request.");
                    break;
                }
                default:
                    sendNotFound(exchange, "Bad request.");
            }
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] requestArray = requestPath.split("/");
        Endpoint finalEndpoint = Endpoint.UNKNOWN;

        switch (requestMethod) {
            case "POST":
                if (requestArray[1].trim().toUpperCase().equals(path) && requestArray.length == 3) {
                    finalEndpoint = Endpoint.UPDATE_EPIC;
                } else if (requestArray[1].trim().toUpperCase().equals(path) && requestArray.length == 2) {
                    finalEndpoint = Endpoint.POST_EPIC;
                }
                break;
            case "GET":
                if (requestArray[1].trim().toUpperCase().equals(path) && requestArray.length == 3) {
                    finalEndpoint = Endpoint.GET_EPIC;
                } else if (requestArray[1].trim().toUpperCase().equals(path) && requestArray.length == 2) {
                    finalEndpoint = Endpoint.GET_ALL_EPICS;
                } else if (requestArray[1].trim().toUpperCase().equals(path) && requestArray.length == 4 &&
                        requestArray[3].trim().toUpperCase().equals("SUBTASKS")) {
                    finalEndpoint = Endpoint.GET_ALL_SUBTASKS_FOR_ONE_EPIC;
                }
                break;
            case "DELETE":
                if (requestArray[1].trim().toUpperCase().equals(path) && requestArray.length == 3) {
                    finalEndpoint = Endpoint.DELETE_EPIC;
                } else if (requestArray[1].trim().toUpperCase().equals(path) && requestArray.length == 4 &&
                        requestArray[3].trim().toUpperCase().equals("SUBTASKS")) {
                    finalEndpoint = Endpoint.DELETE_ALL_SUBTASKS_FOR_ONE_EPIC;
                } else if (requestArray[1].trim().toUpperCase().equals(path) && requestArray.length == 2) {
                    finalEndpoint = Endpoint.DELETE_ALL_EPICS;
                }
                break;
            default:
                finalEndpoint = Endpoint.UNKNOWN;
                break;
        }
        return finalEndpoint;
    }
}
