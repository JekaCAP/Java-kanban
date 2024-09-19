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

        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);

        assertEquals(2, historyManager.getHistory().size());

        historyManager.remove(1);
        assertNull(historyManager.getHistory().stream().filter(t -> t.getId() == 1).findFirst().orElse(null));
    }

    @Test
    public void testAddMultipleTimes() {
        Task task = new Task(1, "Задача 1", "Описание 1");

        historyManager.addToHistory(task);
        historyManager.addToHistory(task);
        historyManager.addToHistory(task);

        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    public void testOrderOfTasks() {
        Task task1 = new Task(1, "Задача 1", "Описание 1");
        Task task2 = new Task(2, "Задача 2", "Описание 2");
        Task task3 = new Task(3, "Задача 3", "Описание 3");

        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(task3);

        assertEquals(List.of(task1, task2, task3), historyManager.getHistory());

        historyManager.addToHistory(task1);
        assertEquals(List.of(task2, task3, task1), historyManager.getHistory());
    }
}