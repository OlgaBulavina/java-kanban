package exception;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;


public class ExceptionHandler {

    final Gson gson;

    public ExceptionHandler(Gson gson) {
        this.gson = gson;
    }

    private void handleException(HttpExchange exchange, JsonIOException exception) throws IOException {
        exception.printStackTrace();
        sendText(exchange, 400, gson.toJson(exception.getMessage()));
    }

    private void handleException(HttpExchange exchange, JsonSyntaxException exception) throws IOException {
        exception.printStackTrace();
        sendText(exchange, 400, gson.toJson(exception.getMessage()));
    }

    private void handleException(HttpExchange exchange, NumberFormatException exception) throws IOException {
        exception.printStackTrace();
        sendText(exchange, 404, gson.toJson(exception.getMessage()));
    }

    private void handleException(HttpExchange exchange, NotFoundException exception) throws IOException {
        exception.printStackTrace();
        sendText(exchange, 404, gson.toJson(exception.getMessage()));

    }

    private void handleException(HttpExchange exchange, IntersectionException exception) throws IOException {
        exception.printStackTrace();
        sendText(exchange, 406, gson.toJson(exception.getMessage()));
    }

    public void handleException(HttpExchange exchange, Exception exception) {

        try {
            if (exception instanceof JsonSyntaxException) {
                handleException(exchange, (JsonSyntaxException) exception);
                return;
            }
            if (exception instanceof NotFoundException) {
                handleException(exchange, (NotFoundException) exception);
                return;
            }
            if (exception instanceof NumberFormatException) {
                handleException(exchange, (NumberFormatException) exception);
                return;
            }
            if (exception instanceof IntersectionException) {
                handleException(exchange, (IntersectionException) exception);
                return;
            }
            if (exception instanceof JsonIOException) {
                handleException(exchange, (JsonIOException) exception);
                return;
            }
            exception.printStackTrace();
            sendText(exchange, 500, gson.toJson(exception.getMessage()));//"HTTP internal error"

        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    private void sendText(HttpExchange exchange, int statusCode, String text) throws IOException {
        try (exchange) {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(statusCode, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(resp);
            }
        }
    }
}
