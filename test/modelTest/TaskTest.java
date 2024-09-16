package modelTest;

import logic.InMemoryTaskManager;
import logic.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import tasks.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TaskTest {

    private TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void testEqualityById() {
        Task task1 = new Task(1, "Название", "Описание");
        taskManager.taskCreator(task1);
        Task task2 = new Task(1, "Другое название", "Другое Описание");
        taskManager.taskCreator(task2);
        Task task3 = new Task(2, "Название", "Описание");
        taskManager.taskCreator(task3);

        assertNotEquals(task1, task3, "Задачи с разными идентификаторами не должны быть равны");
    }

    @Test
    void testEqualityByType() {
        Task task1 = new Task(1, "Название", "Описание");
        Epic epic1 = new Epic(1, "Название", "Описание");
        Subtask subtask1 = new Subtask(1, "Название", "Описание", epic1);

        assertNotEquals(task1, epic1, "Задача и Epic не должны быть равны");
        assertNotEquals(task1, subtask1, "Задача и подзадача не должны быть равны");
    }


}