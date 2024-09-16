package modelTest;

import logic.InMemoryTaskManager;
import logic.TaskManager;
import tasks.*;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicTest {

    private TaskManager taskManager = new InMemoryTaskManager();

    @Test
    void testAddSubtaskToItself() {
        Epic epic = new Epic("Epic", "Описание");
        taskManager.epicCreator(epic);

        Subtask subtask1 = new Subtask("Подзадача", "Описание", epic);
        Subtask subtask2 = new Subtask("Подзадача", "Описание", epic);

        taskManager.subtaskCreator(subtask1);
        taskManager.subtaskCreator(subtask2);

        assertEquals(List.of(2, 3), epic.getSubtaskIdList(), "epic содержит неверные id подзадач");

        taskManager.deleteSubtask(3);

        assertEquals(List.of(2), epic.getSubtaskIdList(), "В эпике не должно быть неактуальных id подзадач");


    }
}