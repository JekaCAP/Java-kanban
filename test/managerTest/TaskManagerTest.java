package managerTest;

import logic.*;
import tasks.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void testAddTask() {
        Task task = new Task(1, "Задача 1", "Описание 1");
        taskManager.taskCreator(task);
        Task retrievedTask = taskManager.getTaskById(1);
        assertNotNull(retrievedTask);
        assertEquals("Задача 1", retrievedTask.getTitle());
    }

    @Test
    public void testUpdateTask() {
        Task task = new Task(1, "Задача 1", "Описание 1");
        taskManager.taskCreator(task);
        task.setTitle("Updated Задача 1");
        taskManager.updateTask(task);
        Task updatedTask = taskManager.getTaskById(1);
        assertEquals("Updated Задача 1", updatedTask.getTitle());
    }

    @Test
    public void testRemoveTaskById() {
        Task task = new Task(1, "Задача 1", "Описание 1");
        assertEquals(0, taskManager.getTasks().size(), "Задач еще не должно быть");
        taskManager.taskCreator(task);
        assertEquals(1, taskManager.getTasks().size(), "Количество задач больше чем 1");
        taskManager.deleteTask(task.getId());
        assertEquals(0, taskManager.getTasks().size(), "Задач не должно быть после удаления");
    }

    @Test
    public void testAddEpic() {
        Epic epic = new Epic(1, "Задача 1", "Epic Описание 1");
        taskManager.epicCreator(epic);
        Epic retrievedEpic = taskManager.getEpicById(1);
        assertNotNull(retrievedEpic);
        assertEquals("Задача 1", retrievedEpic.getTitle());
    }

    @Test
    public void testAddSubtaskToEpic() {
        Epic epic = new Epic(1, "Задача 1", "Epic Описание 1");
        taskManager.epicCreator(epic);
        Subtask subtask = new Subtask(1, "Подзадача 1", "Подзадача Описание 1", epic);
        taskManager.subtaskCreator(subtask);
        Epic retrievedEpic = taskManager.getEpicById(1);
        assertEquals(1, retrievedEpic.getSubtaskIdList().size());
    }

    @Test
    public void testRemoveSubtaskById() {
        Epic epic = new Epic(100, "Задача 1", "Epic Описание 1");
        taskManager.epicCreator(epic);
        Subtask subtask = new Subtask(1, "Подзадача 1", "Подзадача Описание 1", epic);
        taskManager.subtaskCreator(subtask);
        assertEquals(1, taskManager.getSubtasks().size());

        assertEquals(subtask, taskManager.getSubtaskById(1));
        taskManager.deleteSubtask(1);
        assertEquals(0, taskManager.getSubtasks().size());

    }

    @Test
    public void testHistory() {
        Task task1 = new Task(1, "Задача 1", "Описание 1");
        Task task2 = new Task(2, "Задача 2", "Описание 2");

        taskManager.taskCreator(task1);
        taskManager.taskCreator(task2);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(1);

        assertEquals(List.of(task2, task1), taskManager.history());
    }
}
