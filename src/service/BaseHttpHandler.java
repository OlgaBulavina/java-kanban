package service;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    protected TaskManager taskManager;
    protected Gson gson;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public abstract void handle(HttpExchange exchange) throws IOException;

    protected void sendText(HttpExchange h, String text) throws IOException {
        try (h) {
            byte[] resp = text.getBytes(DEFAULT_CHARSET);
            h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            h.sendResponseHeaders(200, 0);
            try (OutputStream os = h.getResponseBody()) {
                os.write(resp);
            }
        }
    }

    protected void sendOkNoDataToSendBack(HttpExchange h, String text) throws IOException {
        try (h) {
            byte[] resp = text.getBytes(DEFAULT_CHARSET);
            h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            h.sendResponseHeaders(201, 0);
            try (OutputStream os = h.getResponseBody()) {
                os.write(resp);
            }
        }
    }

    protected void sendNotFound(HttpExchange h, String text) throws IOException {
        try (h) {
            byte[] resp = text.getBytes(DEFAULT_CHARSET);
            h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            h.sendResponseHeaders(404, 0);
            try (OutputStream os = h.getResponseBody()) {
                os.write(resp);
            }
        }
    }

    protected void sendHasInteractions(HttpExchange h, String text) throws IOException {
        try (h) {
            byte[] resp = text.getBytes(DEFAULT_CHARSET);
            h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            h.sendResponseHeaders(406, 0);
            try (OutputStream os = h.getResponseBody()) {
                os.write(resp);
            }
        }
    }

    protected void sendWasMistakeInRequest(HttpExchange h, String text) throws IOException {
        try (h) {
            byte[] resp = text.getBytes(DEFAULT_CHARSET);
            h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            h.sendResponseHeaders(500, 0);
            try (OutputStream os = h.getResponseBody()) {
                os.write(resp);
            }
        }
    }
}