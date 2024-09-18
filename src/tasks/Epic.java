package tasks;

import logic.TaskStatus;
import logic.TaskType;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIdList;

    public Epic(String title, String description) {
        super(title, description, null);
        subtaskIdList = new ArrayList<>();
        this.taskType = TaskType.EPIC;
    }

    public Epic(Integer id, String title, String description) {
        super(id, title, description, null);
        subtaskIdList = new ArrayList<>();
        this.taskType = TaskType.EPIC;
    }

    public Epic(Integer id, String title, TaskStatus status, String description) {
        super(id, title, description, null);
        subtaskIdList = new ArrayList<>();
        this.taskType = TaskType.EPIC;
        this.status = status;

    }

    public ArrayList<Integer> getSubtaskIdList() {
        return subtaskIdList;
    }

    public void setSubtaskIdList(ArrayList<Integer> subtaskIdList) {
        this.subtaskIdList = subtaskIdList;
    }

    @Override
    public String toString() {
        return "ID задачи Epic=\"" + id
                + "\", Название задачи=\"" + title
                + "\", Описание=\"" + description
                + "\", Идентификаторы подзадач в эпике=\"" + subtaskIdList
                + "\", Статус=\"" + status + "\""
                + "\n";
    }

}