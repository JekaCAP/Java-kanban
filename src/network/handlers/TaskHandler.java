package network.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import logic.TaskManager;
import network.adapters.DurationTypeAdapter;
import network.adapters.LocalDateTimeTypeAdapter;
import tasks.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class TaskHandler extends BaseHttpHandler {
    public TaskHandler(TaskManager manager) {
        super(manager);
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
                    if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
                        handleGetTasks(exchange);
                    } else if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
                        int id = Integer.parseInt(pathParts[2]);
                        handleGetTaskById(exchange, id);
                    }
                    break;
                case "POST":
                    if (pathParts.length == 2 && pathParts[1].equals("tasks") && !method.equals("GET")) {
                        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "UTF-8");
                        BufferedReader br = new BufferedReader(isr);
                        String requestBody = br.lines().collect(Collectors.joining());

                        Task task = gson.fromJson(requestBody, Task.class);
                        handleAddTask(exchange, task);

                    } else if (pathParts.length == 3 && pathParts[1].equals("tasks") && !method.equals("GET")) {
                        handleUpdateTask(exchange);
                    }
                    break;
                case "DELETE":
                    if (pathParts.length == 3 && pathParts[1].equals("tasks") && !method.equals("GET")) {
                        int id = Integer.parseInt(pathParts[2]);
                        handleDeleteTask(exchange, id);
                    }
                    break;
            }
        } catch (NoSuchElementException e) {
            sendNotFound(exchange, "Not Found");
        }
    }

    public void handleGetTasks(HttpExchange exchange) throws IOException {
        try {
            System.out.println("Началась обработка /tasks запроса от клиента.");
            String response = taskManager.getTasks().values()
                    .stream().map(task -> task.toString()).collect(Collectors.joining("\n"));
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }

    }

    public void handleGetTaskById(HttpExchange exchange, int id) throws IOException {
        try {
            System.out.println("Началась обработка /tasks запроса от клиента.");
            String response = gson.toJson(taskManager.getTaskById(id));
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }

    }

    public void handleAddTask(HttpExchange exchange, Task task) throws IOException {
        try {
            System.out.println("Началась обработка /tasks запроса от клиента.");
            taskManager.taskCreator(task);
            for (Task existingTask : taskManager.getTasks().values()) {
                if (taskManager.isCrossingWith(task, existingTask)) {
                    sendHasInteractions(exchange);
                    break;
                }
            }
            String response = "Задача успешно добавлена";
            exchange.sendResponseHeaders(201, response.getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void handleUpdateTask(HttpExchange exchange) throws IOException {
        try {
            System.out.println("Началась обработка /tasks запроса от клиента.");
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String requestBody = br.lines().collect(Collectors.joining());
            Task task = gson.fromJson(requestBody, Task.class);
            taskManager.updateTask(task);
            String response = "Задача успешно обновлена";
            exchange.sendResponseHeaders(200, response.getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void handleDeleteTask(HttpExchange exchange, int id) throws IOException {
        try {
            System.out.println("Началась обработка /tasks запроса от клиента.");
            taskManager.deleteTask(id);
            String response = "Задача успешно удалена";
            exchange.sendResponseHeaders(200, response.getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }

    }
}
