package managerTest;

import logic.InMemoryHistoryManager;
import logic.HistoryManager;
import tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void testAddAndRemove() {
        Task task1 = new Task(1, "Задача 1", "Описание 1");
        Task task2 = new Task(2, "Задача 2", "Описание 2");

        historyManager.add(task1);
        historyManager.add(task2);

        assertEquals(2, historyManager.getHistory().size());

        historyManager.remove(1);
        assertNull(historyManager.getHistory().stream().filter(t -> t.getId() == 1).findFirst().orElse(null));
    }

    @Test
    public void testAddMultipleTimes() {
        Task task = new Task(1, "Задача 1", "Описание 1");

        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    public void testOrderOfTasks() {
        Task task1 = new Task(1, "Задача 1", "Описание 1");
        Task task2 = new Task(2, "Задача 2", "Описание 2");
        Task task3 = new Task(3, "Задача 3", "Описание 3");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        assertEquals(List.of(task1, task2, task3), historyManager.getHistory());

        historyManager.add(task1);
        assertEquals(List.of(task2, task3, task1), historyManager.getHistory());
    }
}