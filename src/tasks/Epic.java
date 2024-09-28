package tasks;

import logic.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIdList;
    private LocalDateTime endTime;

    public Epic(Integer id, String title, String description) {
        super(id, title, description);
        subtaskIdList = new ArrayList<>();
        this.taskType = TaskType.EPIC;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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