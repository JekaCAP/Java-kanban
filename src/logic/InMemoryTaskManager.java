package logic;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int idGenerator = 0;
    protected HashMap<Integer, Task> taskHashMap = new HashMap<>();
    protected HashMap<Integer, Epic> epicHashMap = new HashMap<>();
    protected HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();
    protected HistoryManager historyManager = new InMemoryHistoryManager();

    protected Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));


    @Override
    public void taskCreator(Task task) {
        checkId(task);
        taskHashMap.put(task.getId(), task);
        if (task.getStartTime() != null) {
            if (prioritizedTasks.stream().anyMatch(prioritizedTask -> isCrossingWith(prioritizedTask, task))) {
                System.out.println("Задача пересекается с другой.");
            } else {
                prioritizedTasks.add(task);
            }
        }
    }


    @Override
    public void subtaskCreator(Subtask subtask) {
        checkId(subtask);
        subtaskHashMap.put(subtask.getId(), subtask);
        Epic epic = epicHashMap.get(subtask.getEpic().getId());
        if (epic != null) {
            epic.getSubtaskIdList().add(subtask.getId());
            updateEpic(epic);
        }
        if (subtask.getStartTime() != null) {
            if (prioritizedTasks.stream().anyMatch(prioritizedTask -> isCrossingWith(prioritizedTask, subtask))) {
                System.out.println("Подзадача пересекается с другой.");
            } else {
                prioritizedTasks.add(subtask);
            }
        }
    }

    @Override
    public void epicCreator(Epic epic) {
        checkId(epic);
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


    public void deleteTaskList() {
        taskHashMap.values().forEach(task -> historyManager.remove(task.getId()));
        taskHashMap.clear();
    }


    public void deleteSubtaskList() {
        Set<Epic> epicsForStatusUpdate = subtaskHashMap.values().stream()
                .map(Subtask::getEpic)
                .peek(epic -> epic.setSubtaskIdList(new ArrayList<>()))
                .collect(Collectors.toSet());

        subtaskHashMap.values().forEach(subtask -> historyManager.remove(subtask.getId()));
        subtaskHashMap.clear();

        epicsForStatusUpdate.forEach(epic -> epic.setStatus(TaskStatus.NEW));
    }

    public void deleteEpicList() {
        epicHashMap.values().forEach(epic -> historyManager.remove(epic.getId()));
        epicHashMap.clear();

        subtaskHashMap.values().forEach(subtask -> historyManager.remove(subtask.getId()));
        subtaskHashMap.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task idTask = taskHashMap.get(id);
        historyManager.addToHistory(idTask);
        return idTask;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask idSub = subtaskHashMap.get(id);
        historyManager.addToHistory(idSub);
        return idSub;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic idEpic = epicHashMap.get(id);
        historyManager.addToHistory(idEpic);
        return idEpic;
    }

    @Override
    public void deleteTask(int id) {
        Optional.ofNullable(taskHashMap.get(id)).ifPresent(task -> {
            System.out.println("Задача с id# " + id + " удалена." + System.lineSeparator());
            taskHashMap.remove(id);
            historyManager.remove(id);
        });
    }

    @Override
    public void deleteSubtask(int id) {
        if (subtaskHashMap.containsKey(id)) {
            System.out.println("Подзадача с id# " + id + " удалена." + System.lineSeparator());

            Epic epic = subtaskHashMap.get(id).getEpic();

            epic.getSubtaskIdList().removeIf(subtaskId -> subtaskId.equals(id));

            calcEpicStatus(epic);

            historyManager.remove(id);
            subtaskHashMap.remove(id);
        }
    }

    @Override
    public void deleteEpic(int id) {
        if (epicHashMap.containsKey(id)) {
            System.out.println("Эпик с id# " + id + " удален." + System.lineSeparator());

            Epic epic = epicHashMap.get(id);

            epic.getSubtaskIdList().stream()
                    .peek(subtaskId -> {
                        subtaskHashMap.remove(subtaskId);
                        historyManager.remove(subtaskId);
                    })
                    .collect(Collectors.toList());

            epicHashMap.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void updateTask(Task task) {
        taskHashMap.put(task.getId(), task);
        if (task.getStartTime() != null) {
            if (prioritizedTasks.stream().anyMatch(prioritizedTask -> isCrossingWith(prioritizedTask, task))) {
                System.out.println("Задача пересекается с другой.");
            } else {
                prioritizedTasks.add(task);
            }
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtaskHashMap.put(subtask.getId(), subtask);
        Epic epic = subtask.getEpic();
        calcEpicStatus(epic);
        if (subtask.getStartTime() != null) {
            if (prioritizedTasks.stream().anyMatch(prioritizedTask -> isCrossingWith(prioritizedTask, subtask))) {
                System.out.println("Подзадача пересекается с другой.");
            } else {
                prioritizedTasks.add(subtask);
            }
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
        List<TaskStatus> statuses = epic.getSubtaskIdList().stream()
                .map(subtaskId -> subtaskHashMap.get(subtaskId).getStatus())
                .toList();

        boolean allTaskIsNew = statuses.stream().allMatch(status -> status == TaskStatus.NEW);
        boolean allTaskIsDone = statuses.stream().allMatch(status -> status == TaskStatus.DONE);

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

    @Override
    public Set<Task> getPrioritizedTasks() {
        return new TreeSet<>(prioritizedTasks);
    }

    private boolean isCrossingWith(Task task1, Task task2) {
        return isCrossing(task1, task2);
    }

    private boolean isCrossing(Task task1, Task task2) {
        return task1.getEndTime().isAfter(task2.getStartTime()) && task1.getStartTime().isBefore(task2.getEndTime());
    }

    public LocalDateTime calculateEpicEndTime(Epic epic) {
        List<Subtask> subTasks = new ArrayList<>();
        for (int id : epic.getSubtaskIdList()) {
            Subtask subTask = getSubtaskById(id);
            subTasks.add(subTask);
        }
        if (subTasks.isEmpty()) {
            return epic.getStartTime().plus(Duration.ZERO);
        }

        Duration totalDuration = Duration.ZERO;
        for (Subtask st : subTasks) {
            totalDuration = totalDuration.plus(st.getDuration());
        }
        return epic.getStartTime().plus(totalDuration);
    }
}

