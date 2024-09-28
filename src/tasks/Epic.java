package tasks;

import logic.InMemoryTaskManager;
import logic.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIdList;

    private LocalDateTime startTime;

    public Epic(Integer id, String title, String description) {
        super(id, title, description, null);
        subtaskIdList = new ArrayList<>();
        this.taskType = TaskType.EPIC;
    }

    @Override
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime(InMemoryTaskManager taskManager) {
        // Вызов метода из TaskManager
        return taskManager.calculateEpicEndTime(this);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public ArrayList<Integer> getSubtaskIdList() {
        return subtaskIdList;
    }

    public void setSubtaskIdList(ArrayList<Integer> subtaskIdList) {
        this.subtaskIdList = subtaskIdList;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return getId() + "," +
                getTaskType() + "," +
                getTitle() + "," +
                getStatus() + "," +
                getDescription() + ",," +
                getStartTime() + "," +
                (getDuration() == Duration.ZERO ? "" : getDuration());

    }

}