package tasks;

import logic.TaskStatus;
import logic.TaskType;

public class Task {
    protected String title;
    protected String description;
    protected Integer id;
    protected TaskStatus status;
    protected TaskType taskType;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Task(String title, String description, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.taskType = TaskType.TASK;
    }

    public Task(Integer id, String title, String description, TaskStatus status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.taskType = TaskType.TASK;
    }

    public Task(Integer id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.taskType = TaskType.TASK;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ID задачи Task=\"" + id
                + "\", Название задачи=\"" + title
                + "\", Описание=\"" + description
                + "\", Статус=\"" + status + "\""
                + '}'
                + "\n";
    }

    public String toStringFromFile() {
        return String.format("%s,%s,%s,%s,%s,%s", id, taskType, title, status, description, "");
    }

}