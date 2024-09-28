package network.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import logic.InMemoryTaskManager;
import logic.TaskManager;
import network.HttpTaskServer;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryHandlerTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        manager.deleteEpicList();
        manager.deleteSubtaskList();
        manager.deleteTaskList();
        taskServer.start();
        gson = new Gson();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task(1, "Task 1", "Description 1");
        manager.taskCreator(task1);
        manager.getTaskById(task1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            JsonElement history = JsonParser.parseString(response.body());
            assertEquals(1, manager.history().size(), "Некорректная история");
        } catch (IOException e) {
            System.out.println("Исключение: " + e.getMessage());
        }
    }
}