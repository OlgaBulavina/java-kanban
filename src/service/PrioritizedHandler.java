package service;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.ExceptionHandler;
import model.Endpoint;
import model.Task;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Set;

class PrioritizedHandler extends BaseHttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private String path = "PRIORITIZED";

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    ExceptionHandler exceptionHandler = new ExceptionHandler(gson);

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {
            Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

            switch (endpoint) {
                case GET_PRIORITIZED: {
                    Set<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                    String json = gson.toJson(prioritizedTasks);
                    sendText(exchange, json);
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
        Endpoint finalEndpoint;

        switch (requestMethod) {
            case "GET":
                if (requestArray[1].trim().toUpperCase().equals(path) && requestArray.length == 2) {
                    finalEndpoint = Endpoint.GET_PRIORITIZED;
                    break;
                }
            default: {
                finalEndpoint = Endpoint.UNKNOWN;
                break;
            }
        }
        return finalEndpoint;
    }
}
