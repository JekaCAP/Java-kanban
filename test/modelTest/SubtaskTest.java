package modelTest;

import logic.InMemoryTaskManager;
import logic.TaskManager;

import logic.TaskStatus;
import tasks.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskTest {

    private TaskManager taskManager = new InMemoryTaskManager();
    Epic epic;
    Subtask subtask;

    @Test
    public void subtaskCreatorTest() {
        epic = new Epic("Эпик 1", "Описание эпика");
        taskManager.epicCreator(epic);
        subtask = new Subtask("Подзадача 1", "Описание подзадачи", epic);
        taskManager.subtaskCreator(subtask);

        assertEquals(taskManager.getSubtaskById(2).getTitle(), subtask.getTitle());
        assertEquals(taskManager.getSubtaskById(2).getDescription(), subtask.getDescription());
        assertEquals(taskManager.getSubtaskById(2).getStatus(), TaskStatus.NEW);
        assertEquals("Подзадача 1", subtask.getTitle());
        assertEquals("Описание подзадачи", subtask.getDescription());
        assertEquals(1, taskManager.getSubtasks().size(), "Количество задач в менеджере после добавления новой задачи не верно!");
    }
}