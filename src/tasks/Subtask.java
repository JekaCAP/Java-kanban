package tasks;

import logic.TaskStatus;
import logic.TaskType;

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

    public Subtask(int id, String title, String description, TaskStatus status, Epic epic) {
        super(id, title, description, status);
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
        return "ID подзадачи Subtask=\"" + id
                + "\", Название подзадачи=\"" + title
                + "\", Описание=\"" + description
                + "\", Статус=\"" + status + "\""
                + "\n";
    }

    @Override
    public String toStringFromFile() {
        return String.format("%s,%s,%s,%s,%s,%s", id, taskType, title, status, description, epic.getId());
    }

}