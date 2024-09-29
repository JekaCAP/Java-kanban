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
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SubTaskHandlerTest {
    TaskManager manager = new InMemoryTaskManager();
    ;
    HttpTaskServer taskServer;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();

    @BeforeEach
    public void setUp() throws IOException {
        taskServer = new HttpTaskServer(manager);
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
    public void testAddSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "TestEpic 1", "Testing subtask");
        Subtask subtask = new Subtask("Test 1", "Testing subtask 1", epic, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.epicCreator(epic);
        manager.subtaskCreator(subtask);

        String taskJson = gson.toJson(subtask.toString());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode());

            List<Subtask> tasksFromManager = (List<Subtask>) manager.getSubtasks().values();

            assertNotNull(tasksFromManager, "Сабтаск не возвращается");
            assertEquals(1, tasksFromManager.size(), "Некорректное количество сабтасков");
            assertEquals("Test 1", tasksFromManager.get(0).getTitle(), "Некорректное имя сабтаска");
        } catch (IOException | InterruptedException e) {
            System.out.println("Исключение: " + e.getMessage());
        }
    }

    @Test
    public void testGetSubTasks() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "TestEpic 1", "Testing subtask");
        manager.epicCreator(epic);
        manager.subtaskCreator(new Subtask("Test 1", "Testing subtask 1", epic, LocalDateTime.now(), Duration.ofMinutes(5)));
        manager.subtaskCreator(new Subtask("Test 2", "Testing subtask 2", epic, LocalDateTime.now().plus(Duration.ofMinutes(10)), Duration.ofMinutes(5)));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET()
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

        } catch (IOException | InterruptedException e) {
            System.out.println("Исключение: " + e.getMessage());
        }
    }

    @Test
    public void testGetSubTaskById() throws IOException, InterruptedException {
        Epic epic = new Epic(0, "Test Epic", "Testing epic 1");
        Subtask subTask = new Subtask("Test Epic", "Testing epic 1", epic);
        manager.epicCreator(epic);
        manager.subtaskCreator(subTask);


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subTask.getId()))
                .GET()
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            JsonElement responseBody = JsonParser.parseString(response.body());
            assertNotNull(responseBody, "Сабтаск не возвращается");
            assertEquals(subTask.getTitle(), responseBody.getAsJsonObject().get("name").getAsString(), "Некорректное имя сабтаска");
        } catch (IOException e) {
            System.out.println("Исключение: " + e.getMessage());
        }
    }

    @Test
    public void testUpdateSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "TestEpic 1", "Testing subtask");
        Subtask subtask = new Subtask("Test 1", "Testing subtask 1", epic, LocalDateTime.now(), Duration.ofMinutes(5));
        manager.epicCreator(epic);
        manager.subtaskCreator(subtask);
        subtask.setTitle("New name");
        String taskJson = gson.toJson(subtask.toString());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtask.getId()))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode());

            List<Subtask> tasksFromManager = (List<Subtask>) manager.getSubtasks().values();

            assertNotNull(tasksFromManager, "Сабтаск не возвращается");
            assertEquals(1, tasksFromManager.size(), "Некорректное количество сабтасков");
            assertEquals("New name", tasksFromManager.get(0).getTitle(), "Некорректное имя сабтаска");
        } catch (IOException | InterruptedException e) {
            System.out.println("Исключение: " + e.getMessage());
        }
    }

    @Test
    public void testDeleteSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic(0, "Test Epic", "Testing epic 1");
        Subtask subTask = new Subtask("Test Epic", "Testing epic 1", epic);
        manager.epicCreator(epic);
        manager.subtaskCreator(subTask);


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subTask.getId()))
                .DELETE()
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            Subtask subTaskFromManager = manager.getSubtaskById(subTask.getId());
            assertNull(subTaskFromManager, "Сабтаск не удален");
        } catch (IOException | InterruptedException e) {
            System.out.println("Исключение: " + e.getMessage());
        }
    }
}
