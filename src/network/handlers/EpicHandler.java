package network.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import logic.Managers;
import logic.TaskManager;
import network.adapters.DurationTypeAdapter;
import network.adapters.LocalDateTimeTypeAdapter;
import tasks.Epic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class EpicHandler extends BaseHttpHandler {
    public EpicHandler(TaskManager taskManager) {
        super(Managers.getDefault());
    }

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        try {
            switch (method) {
                case "GET":
                    if (pathParts.length == 2 && pathParts[1].equals("epics")) {
                        handleGetEpics(exchange);
                    } else if (pathParts.length == 3 && pathParts[1].equals("epics")) {
                        int id = Integer.parseInt(pathParts[2]);
                        handleGetEpicById(exchange, id);
                    }
                    break;
                case "POST":
                    if (pathParts.length == 2 && pathParts[1].equals("epics") && !method.equals("GET")) {
                        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "UTF-8");
                        BufferedReader br = new BufferedReader(isr);
                        String requestBody = br.lines().collect(Collectors.joining());

                        Epic epic = gson.fromJson(requestBody, Epic.class);
                        handleAddEpic(exchange, epic);
                    }
                    break;
                case "DELETE":
                    if (pathParts.length == 3 && pathParts[1].equals("epics") && !method.equals("POST")) {
                        int id = Integer.parseInt(pathParts[2]);
                        handleDeleteEpic(exchange, id);
                    }
                    break;
            }
        } catch (NoSuchElementException e) {
            sendNotFound(exchange, "Not Found");
        }
    }

    public void handleGetEpics(HttpExchange exchange) throws IOException {
        try {
            System.out.println("Началась обработка /epics запроса от клиента.");
            String response = taskManager.getEpics().values()
                    .stream().map(epic -> epic.toString()).collect(Collectors.joining("\n"));
            exchange.sendResponseHeaders(200, response.getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }


    }

    public void handleGetEpicById(HttpExchange exchange, int id) throws IOException {
        try {
            System.out.println("Началась обработка /epics запроса от клиента.");
            String response = gson.toJson(taskManager.getEpicById(id));
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }

    }

    public void handleAddEpic(HttpExchange exchange, Epic epic) throws IOException {
        try {
            System.out.println("Началась обработка /epics запроса от клиента.");
            taskManager.epicCreator(epic);
            for (Epic existingEpic : taskManager.getEpics().values()) {
                if (taskManager.isCrossingWith(epic, existingEpic)) {
                    sendHasInteractions(exchange);
                    break;
                }
            }
            String response = "Эпик успешно добавлена";
            exchange.sendResponseHeaders(201, response.getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void handleDeleteEpic(HttpExchange exchange, int id) throws IOException {
        try {
            System.out.println("Началась обработка /epics запроса от клиента.");
            taskManager.deleteEpic(id);
            String response = "Эпик успешно удален";
            exchange.sendResponseHeaders(200, response.getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }
}
