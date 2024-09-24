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

    Task task;

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

    @Test
    void remove() {
        // Пустая история задач.
        assertDoesNotThrow(() -> historyManager.remove(1), "Удаление из пустой истории не должно вызывать исключений!");

        // Удаление из истории: начало, середина, конец.
        task = new Task(1, "Задача 1", "Задача для удаления из начала истории");
        historyManager.addToHistory(task);

        task = new Task(2, "Задача 2", "Промежуточная задача для тестирования удаления");
        historyManager.addToHistory(task);

        task = new Task(3, "Задача 3", "Задача для удаления из середины истории");
        historyManager.addToHistory(task);

        task = new Task(4, "Задача 3", "Задача для удаления с конца истории");
        historyManager.addToHistory(task);

        historyManager.remove(1);
        assertEquals(3, historyManager.getHistory().size(), "Задача в начале истории не была удалена!");

        historyManager.remove(3);
        assertEquals(2, historyManager.getHistory().size(), "Задача из середины истории не была удалена!");

        historyManager.remove(4);
        assertEquals(1, historyManager.getHistory().size(), "Задача в конце истории не была удалена!");
    }
}