package tasks;

import logic.TaskStatus;
import logic.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String title, String description, Epic epic) {
        super(title, description);
        this.epic = epic;
    }

    public Subtask(String title, String description, TaskStatus status, Epic epic) {
        super(title, description, status);
        this.epic = epic;
        this.taskType = TaskType.SUBTASK;
    }


    public Subtask(int id, String title, String description, Epic epic) {
        super(id, title, description);
        this.epic = epic;
        this.taskType = TaskType.SUBTASK;
    }


    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public String toString() {
        return getId() + "," +
                getTaskType() + "," +
                getTitle() + "," +
                getStatus() + "," +
                getDescription() + "," + getEpic().getId();
    }

}