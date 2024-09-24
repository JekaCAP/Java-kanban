package modelTest;

import logic.Managers;
import logic.TaskManager;
import logic.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import tasks.*;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicTest {

    TaskManager taskManager;
    Epic epic;
    Subtask subtask1;
    Subtask subtask2;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void testAddSubtaskToItself() {
        epic = new Epic(1, "Epic", "Описание");
        taskManager.epicCreator(epic);

        subtask1 = new Subtask("Подзадача", "Описание", epic);
        subtask2 = new Subtask("Подзадача", "Описание", epic);

        taskManager.subtaskCreator(subtask1);
        taskManager.subtaskCreator(subtask2);

        assertEquals(List.of(1, 2), epic.getSubtaskIdList(), "epic содержит неверные id подзадач");

        taskManager.deleteSubtask(2);

        assertEquals(List.of(1), epic.getSubtaskIdList(), "В эпике не должно быть неактуальных id подзадач");
    }


    @Test
    public void testingForEpicAllSubtasksWithStatusNew() {
        epic = new Epic(100, "Эпик 1", "Описание эпика 1");
        taskManager.epicCreator(epic);
        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW, epic,
                LocalDateTime.of(2022, 8, 6, 8, 8), Duration.ofMinutes(10));
        subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", TaskStatus.NEW, epic,
                LocalDateTime.of(2022, 8, 8, 8, 8), Duration.ofMinutes(25));
        taskManager.subtaskCreator(subtask1);
        taskManager.subtaskCreator(subtask2);
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void testingForEpicAllSubtasksWithStatusDone() {
        epic = new Epic(100, "Эпик 1", "Описание эпика");
        taskManager.epicCreator(epic);
        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.DONE, epic);
        subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", TaskStatus.DONE, epic);
        taskManager.subtaskCreator(subtask1);
        taskManager.subtaskCreator(subtask2);
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    public void testingForEpicSubtasksWithStatusNewAndDone() {
        epic = new Epic(100, "Эпик 1", "Описание эпика");
        taskManager.epicCreator(epic);
        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.NEW, epic);
        subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", TaskStatus.DONE, epic);
        taskManager.subtaskCreator(subtask1);
        taskManager.subtaskCreator(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void testingForEpicSubtasksWithStatusInProgress() {
        epic = new Epic(100, "Эпик 1", "Описание эпика");
        taskManager.epicCreator(epic);
        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи", TaskStatus.IN_PROGRESS, epic);
        subtask2 = new Subtask("Подзадача 2", "Описание подзадачи", TaskStatus.IN_PROGRESS, epic);
        taskManager.subtaskCreator(subtask1);
        taskManager.subtaskCreator(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

}