package managerTest;

import logic.FileBackedTasksManager;
import logic.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest {
    FileBackedTasksManager taskManager;
    FileBackedTasksManager taskManager1;
    File tempFile;

    @BeforeEach
    public void beforeEach() throws Exception {
        // Создаем временный файл
        tempFile = File.createTempFile("save_tasks", ".txt");
        tempFile.deleteOnExit(); // Удаляем файл при завершении работы приложения
        taskManager = new FileBackedTasksManager(tempFile);
    }

    @Test
    void saveTest() throws IOException {
        // Пустой список задач.
        assertDoesNotThrow(() -> taskManager.save(), "Сохранение менеджера с пустым списком задач не должно вызывать исключений!");

        // Эпик без подзадач.
        Epic epic = new Epic(100, "Эпик 1", "Описание эпика");
        taskManager.epicCreator(epic);
        taskManager.save();
        taskManager1 = FileBackedTasksManager.loadFromFile(tempFile);
        assertEquals(1, taskManager1.getEpics().size(), "Количество задач менеджера после восстановления не совпало!");
        assertEquals(0, taskManager1.getSubtasks().size(), "Количество задач менеджера после восстановления не совпало!");
        assertEquals(0, taskManager1.getTasks().size(), "Количество задач менеджера после восстановления не совпало!");

        // Пустой список истории.
        assertEquals(0, taskManager1.history().size(), "Количество задач в истории обращения после восстановления не совпало!");
    }

    // Тестирование метода загрузки списка задач из файла
    @Test
    void loadFromFileTest() throws IOException {
        // Эпик без подзадач.
        Epic epic1 = new Epic(100, "Эпик 1", "Пустой эпик");
        taskManager.epicCreator(epic1);
        taskManager.save();

        taskManager1 = FileBackedTasksManager.loadFromFile(tempFile);
        assertEquals(1, taskManager1.getEpics().size(), "Количество задач менеджера после восстановления не совпало!");

        // Пустой список истории.
        assertEquals(0, taskManager1.history().size(), "Количество задач в истории обращения после восстановления не совпало!");

        // Со стандартным поведением.
        Epic epic2 = new Epic(200, "Эпик 2", "Эпик с подзадачами");
        taskManager.epicCreator(epic2);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epic2);
        taskManager.subtaskCreator(subtask1); // 1

        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW, epic2);
        taskManager.subtaskCreator(subtask2); // 2

        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        taskManager.taskCreator(task1); // 3

        taskManager.getTaskById(3); // Добавление истории

        taskManager1 = FileBackedTasksManager.loadFromFile(tempFile);
        assertEquals(taskManager1.getTaskById(3).getTitle(), task1.getTitle());
        assertEquals(taskManager1.getTaskById(3).getDescription(), task1.getDescription());
        assertEquals(taskManager1.getTaskById(3).getId(), task1.getId());
        assertEquals(1, taskManager1.getTasks().size(), "Количество задач менеджера после восстановления не совпало!");
        assertEquals(2, taskManager1.getSubtasks().size(), "Количество задач менеджера после восстановления не совпало!");
        assertEquals(2, taskManager1.getEpics().size(), "Количество задач менеджера после восстановления не совпало!");
        assertEquals(1, taskManager1.history().size(), "Количество задач в истории обращения после восстановления не совпало!");
    }

    @Test
    public void loadFromFileAnotherTest() throws IOException {
        assertEquals(0, taskManager.history().size());
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);
        taskManager.taskCreator(task);
        taskManager.save(); // Добавляем сохранение перед загрузкой
        taskManager1 = FileBackedTasksManager.loadFromFile(tempFile);
        assertEquals(taskManager1.getTaskById(1).getTitle(), task.getTitle());
        assertEquals(taskManager1.getTaskById(1).getDescription(), task.getDescription());
        assertEquals(taskManager1.getTaskById(1).getId(), task.getId());
    }

    @Test
    public void testingFileEpicWithoutSubtasks() throws IOException {
        Epic epic = new Epic(100, "Эпик", "Описание");
        taskManager.epicCreator(epic);
        taskManager.save(); // Добавляем сохранение перед загрузкой
        taskManager1 = FileBackedTasksManager.loadFromFile(tempFile);

        // Пустой список истории.
        assertEquals(0, taskManager1.history().size());
        assertEquals(taskManager1.getEpicById(100).getTitle(), epic.getTitle());
        assertEquals(taskManager1.getEpicById(100).getDescription(), epic.getDescription());
        assertEquals(taskManager1.getEpicById(100).getSubtaskIdList().size(), 0);
    }
}
