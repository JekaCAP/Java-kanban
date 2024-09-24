package tasks;

import logic.TaskManager;
import logic.TaskStatus;
import logic.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIdList;

    private LocalDateTime startTime;

    private TaskManager taskManager;


    public Epic(String title, String description, LocalDateTime startTime, TaskManager taskManager) {
        super(title, description, null);
        subtaskIdList = new ArrayList<>();
        this.taskType = TaskType.EPIC;
        this.startTime = startTime;
        this.taskManager = taskManager;
        this.duration = Duration.between(startTime, getEndTime());
    }

    public Epic(int id, String title, String description, LocalDateTime startTime, TaskManager taskManager) {
        super(id, title, description, null);
        subtaskIdList = new ArrayList<>();
        this.taskType = TaskType.EPIC;
        this.startTime = startTime;
        this.taskManager = taskManager;
        this.duration = Duration.between(startTime, getEndTime());
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

    @Override
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
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

    public LocalDateTime getEndTime() {
        List<Subtask> subTasks = new ArrayList<>();
        for (int id : subtaskIdList) {
            Subtask subTask = taskManager.getSubtaskById(id);
            subTasks.add(subTask);
        }
        if (subTasks.isEmpty()) {
            return startTime.plus(Duration.ZERO);
        }

        Duration totalDuration = Duration.ZERO;
        for (Subtask st : subTasks) {
            totalDuration = totalDuration.plus(st.getDuration());
        }
        return startTime.plus(totalDuration);
    }

}