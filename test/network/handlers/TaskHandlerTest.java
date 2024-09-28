package network.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import logic.InMemoryTaskManager;
import logic.TaskManager;
import network.HttpTaskServer;
import network.adapters.DurationTypeAdapter;
import network.adapters.LocalDateTimeTypeAdapter;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskHandlerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();

    @BeforeEach
    public void setUp() throws IOException {
        manager.deleteTaskList();
        manager.deleteSubtaskList();
        manager.deleteEpicList();
        taskServer.start();

    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1", LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task.toString());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode());

            List<Task> tasksFromManager = (List<Task>) manager.getTasks().values();

            assertNotNull(tasksFromManager, "Задачи не возвращаются");
            assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
            assertEquals("Test 1", tasksFromManager.get(0).getTitle(), "Некорректное имя задачи");
        } catch (IOException | InterruptedException e) {
            System.out.println("Исключение: " + e.getMessage());
        }
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        manager.taskCreator(new Task("Test 1", "Testing task 1", LocalDateTime.now(), Duration.ofMinutes(5)));
        manager.taskCreator(new Task("Test 2", "Testing task 2", LocalDateTime.now().plus(Duration.ofMinutes(10)), Duration.ofMinutes(5)));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

        } catch (IOException e) {
            System.out.println("Исключение: " + e.getMessage());
        }
    }

    @Test
    public void testUpdateTask() {
        Task task = new Task(1, "Test ", "Testing task 1");
        manager.taskCreator(task);
        task.setTitle("New name");
        String taskJson = gson.toJson(task.toString());


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + task.getId()))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            JsonElement responseBody = JsonParser.parseString(response.body());
            assertNotNull(responseBody, "Задача не возвращается");
            assertEquals("New name", responseBody.getAsJsonObject().get("name").getAsString(), "Некорректное имя задачи");
        } catch (IOException | InterruptedException e) {
            System.out.println("Исключение: " + e.getMessage());
        }
    }

    @Test
    public void testGetSubTaskById() throws IOException, InterruptedException {
        Task task = new Task(1, "Test", "Testing task 1");
        manager.taskCreator(task);


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + task.getId()))
                .GET()
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            JsonElement responseBody = JsonParser.parseString(response.body());
            assertNotNull(responseBody, "Задача не возвращается");
            assertEquals(task.getTitle(), responseBody.getAsJsonObject().get("name").getAsString(), "Некорректное имя задачи");
        } catch (IOException e) {
            System.out.println("Исключение: " + e.getMessage());
        }
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1", LocalDateTime.now(), Duration.ofMinutes(5));
        manager.taskCreator(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + task.getId()))
                .DELETE()
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            assertEquals(0, manager.getTasks().size(), "Задача не удалена");

        } catch (IOException | InterruptedException e) {
            System.out.println("Исключение: " + e.getMessage());
        }
    }
}
