package logic;

import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected int idGenerator = 0;
    protected HashMap<Integer, Task> taskHashMap = new HashMap<>();
    protected HashMap<Integer, Epic> epicHashMap = new HashMap<>();
    protected HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();
    protected HistoryManager historyManager = new InMemoryHistoryManager();

    @Override
    public void taskCreator(Task task) {
        checkId(task);
        task.setStatus(TaskStatus.NEW);
        taskHashMap.put(task.getId(), task);
    }

    @Override
    public void subtaskCreator(Subtask subtask) {
        checkId(subtask);
        subtask.setStatus(TaskStatus.NEW);
        subtaskHashMap.put(subtask.getId(), subtask);
        subtask.getEpic().getSubtaskIdList().add(subtask.getId());
        calcEpicStatus(subtask.getEpic());
    }

    @Override
    public void epicCreator(Epic epic) {
        checkId(epic);
        epic.setStatus(TaskStatus.NEW);
        epicHashMap.put(epic.getId(), epic);
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        return taskHashMap;
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasks() {
        return subtaskHashMap;
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return epicHashMap;
    }

    @Override
    public void deleteTaskList() {
        taskHashMap.clear();
    }

    @Override
    public void deleteSubtaskList() {
        ArrayList<Epic> epicsForStatusUpdate = new ArrayList<>();
        for (Subtask subtask : subtaskHashMap.values()) {
            subtask.getEpic().setSubtaskIdList(new ArrayList<>());
            if (!epicsForStatusUpdate.contains(subtask.getEpic())) {
                epicsForStatusUpdate.add(subtask.getEpic());
            }
        }
        subtaskHashMap.clear();
        for (Epic epic : epicsForStatusUpdate) {
            epic.setStatus(TaskStatus.NEW);
        }
    }

    @Override
    public void deleteEpicList() {
        epicHashMap.clear();
        subtaskHashMap.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task idTask = taskHashMap.get(id);
        historyManager.add(idTask);
        return idTask;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask idSub = subtaskHashMap.get(id);
        historyManager.add(idSub);
        return idSub;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic idEpic = epicHashMap.get(id);
        historyManager.add(idEpic);
        return idEpic;
    }

    @Override
    public void deleteTask(int id) {
        if (taskHashMap.containsKey(id)) {
            System.out.println("Задача с id# " + id + " удалена." + System.lineSeparator());
            taskHashMap.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        if (subtaskHashMap.containsKey(id)) {
            System.out.println("Подзадача с id# " + id + " удалена." + System.lineSeparator());
            Epic epic = subtaskHashMap.get(id).getEpic();
            epic.getSubtaskIdList().remove((Integer) id);
            calcEpicStatus(epic);
            subtaskHashMap.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteEpic(int id) {
        if (epicHashMap.containsKey(id)) {
            System.out.println("Эпик с id# " + id + " удален." + System.lineSeparator());
            Epic epic = epicHashMap.get(id);
            epicHashMap.remove(id);
            historyManager.remove(id);
            for (Integer subtask : epic.getSubtaskIdList()) {
                subtaskHashMap.remove(subtask);
                historyManager.remove(subtask);
            }
        }
    }

    @Override
    public void updateTask(Task task) {
        if (taskHashMap.containsKey(task.getId())) {
            taskHashMap.put(task.getId(), task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtaskHashMap.containsKey(subtask.getId())) {
            subtaskHashMap.put(subtask.getId(), subtask);
            calcEpicStatus(subtask.getEpic());
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        epic.setSubtaskIdList(epicHashMap.get(epic.getId()).getSubtaskIdList());
        epicHashMap.put(epic.getId(), epic);
        calcEpicStatus(epic);
    }

    @Override
    public List<Task> history() {
        return historyManager.getHistory();
    }

    private void calcEpicStatus(Epic epic) {

        if (epic.getSubtaskIdList().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allTaskIsNew = true;
        boolean allTaskIsDone = true;

        for (Integer epicSubtaskId : epic.getSubtaskIdList()) {
            TaskStatus status = subtaskHashMap.get(epicSubtaskId).getStatus();
            if (!(status == TaskStatus.NEW)) {
                allTaskIsNew = false;
            }
            if (!(status == TaskStatus.DONE)) {
                allTaskIsDone = false;
            }
        }

        if (allTaskIsDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (allTaskIsNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private void checkId(Task task) {
        if (task.getId() == null) {
            task.setId(++idGenerator);
        }
    }

}
