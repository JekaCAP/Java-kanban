package logic;

import exceptions.*;
import tasks.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    //Класс для второй реализации менеджера, автосохранение в файл
    private final File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    //Метод для проверки работы менеджера
    public static void main(String[] args) {
        FileBackedTasksManager manager = new FileBackedTasksManager(new File("data/data.csv"));
        FileBackedTasksManager manager1;

        //Заведение нескольких разных задач, эпиков и подзадач.
        Task task1 = new Task(10, "Задача №1", "Описание задачи 1");
        manager.taskCreator(task1);
        Task task2 = new Task(20, "Задача №2", "Описание задачи 2");
        manager.taskCreator(task2);

        Epic epic1 = new Epic(1000, "Эпик №1", "С тремя подзадачами"); //1000
        manager.epicCreator(epic1);

        Subtask subtask1 = new Subtask("Подзадача № 1", "Описание подзадачи 1", epic1); // 1
        manager.subtaskCreator(subtask1);
        Subtask subtask2 = new Subtask("Подзадача № 2", "Описание подзадачи 2", epic1); // 2
        manager.subtaskCreator(subtask2);
        Subtask subtask3 = new Subtask("Подзадача № 3", "Описание подзадачи 3", epic1); // 3
        manager.subtaskCreator(subtask3);

        Epic epic2 = new Epic(2000, "Эпик №2", "Без подзадач"); //1000
        manager.epicCreator(epic2);

        //Вывод списка задач
        System.out.println("Всего создано задач - " + (manager.getTasks().size() + manager.getSubtasks().size() + manager.getEpics().size()));

        //Запрос некоторых задач, чтобы заполнилась история просмотра.
        System.out.println("\n----------Обращение к задачам (10,20,100,1,2,3,200):");
        manager.getTaskById(10);
        manager.getTaskById(20);
        manager.getEpicById(1000);
        manager.getSubtaskById(1);
        manager.getSubtaskById(2);
        manager.getSubtaskById(3);
        manager.getEpicById(2000);

        //Просмотр истории обращения к задачам
        System.out.println("\nСписок обращений к задачам:");
        for (Task taskFor : manager.history()) {
            System.out.println(taskFor);
        }

        System.out.println("\n----------Создание второго менеджера на основе файла первого экземпляра.");

        // Создание нового FileBackedTasksManager менеджера из этого же файла.
        manager1 = loadFromFile(Paths.get("data/data.csv").toFile()); //toPath

        // Вывод списка задач
        System.out.println("Всего создано задач - " + (manager1.getTasks().size() + manager1.getSubtasks().size() + manager1.getEpics().size()));
        System.out.println("\nСписок обращений к задачам после загрузки из файла:");
        for (Task taskFor : manager1.history()) {
            System.out.println("#" + taskFor.getId() + " - " + taskFor.getTitle() + " " + taskFor.getDescription() + " (" + taskFor.getStatus() + ")");
        }


    }

    public void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,title,status,description,epic\n");
            HashMap<Integer, Task> allTasks = new HashMap<>();

            allTasks.putAll(super.getTasks());
            allTasks.putAll(super.getEpics());
            allTasks.putAll(super.getSubtasks());

            for (Task task : allTasks.values()) {
                writer.write(String.format("%s\n", task.toString()));
            }
            writer.write("\n");
            writer.write(toString(this.historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи файла.");
        }
    }

    @Override
    public void taskCreator(Task task) {
        super.taskCreator(task);
        save();
    }

    @Override
    public void epicCreator(Epic epic) {
        super.epicCreator(epic);
        save();
    }

    @Override
    public void subtaskCreator(Subtask subtask) {
        super.subtaskCreator(subtask);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }


    @Override
    public List<Task> history() {
        return super.history();
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        try {
            String data = Files.readString(Path.of(file.getAbsolutePath()));
            String[] lines = data.split("\n");
            boolean isTitle = true;
            boolean itsTask = true;
            int maxId = 0;

            for (String line : lines) {
                if (isTitle) {
                    isTitle = false;
                    continue;
                }
                if (line.isEmpty() || line.equals("\r")) {
                    itsTask = false;
                    continue;
                }
                if (itsTask) {
                    String taskTypeString = line.split(",")[1];

                    // Проверка на "null" или пустую строку для подзадач
                    TaskType taskType;
                    if ("null".equals(taskTypeString) || taskTypeString.isEmpty()) {
                        taskType = TaskType.SUBTASK; // Если тип "null" или пустой, то это подзадача
                    } else {
                        taskType = TaskType.valueOf(taskTypeString); // Определяем тип задачи
                    }

                    setTask(taskType, line, maxId, fileBackedTasksManager);
                } else {
                    List<Integer> ids = fromString(line);
                    for (Integer taskId : ids) {
                        fileBackedTasksManager.historyManager.addToHistory(getTaskAllKind(taskId, fileBackedTasksManager));
                    }
                }
            }
            fileBackedTasksManager.idGenerator = maxId;
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла.");
        }

        return fileBackedTasksManager;
    }


    private static Task getTaskAllKind(int id, InMemoryTaskManager inMemoryTaskManager) {
        Task task = inMemoryTaskManager.getTasks().get(id);
        if (!(task == null)) {
            return task;
        }
        Task epic = inMemoryTaskManager.getEpics().get(id);
        if (!(epic == null)) {
            return epic;
        }
        Task subtask = inMemoryTaskManager.getSubtasks().get(id);
        if (!(subtask == null)) {
            return subtask;
        }
        return null;
    }

    private static String toString(HistoryManager manager) {
        List<String> s = new ArrayList<>();
        for (Task task : manager.getHistory()) {
            s.add(String.valueOf(task.getId()));
        }
        return String.join(",", s);
    }

    private static List<Integer> fromString(String value) {
        String[] idsString = value.split(",");
        List<Integer> tasksId = new ArrayList<>();
        for (String idString : idsString) {
            tasksId.add(Integer.valueOf(idString));
        }
        return tasksId;
    }

    private static Task fromString(String value, TaskType taskType, FileBackedTasksManager fileBackedTasksManager) {
        // Отладочный вывод перед разбором строки
        System.out.println("Обработка строки: " + value);

        String[] dataOfTask = value.split(",");
        if (dataOfTask.length < 5) {
            throw new ManagerSaveException("Неверный формат строки: " + value);
        }

        int id = Integer.parseInt(dataOfTask[0]);
        String title = dataOfTask[2];
        String description = dataOfTask[4];

        switch (taskType) {
            case TASK:
                return new Task(id, title, description);
            case EPIC:
                return new Epic(id, title, description);
            case SUBTASK:
                // Для подзадач нужно получить ID эпика
                String epicIdString = dataOfTask[5].trim();
                if (epicIdString.isEmpty()) {
                    throw new ManagerSaveException("Подзадача не содержит ссылки на эпик: " + value);
                }
                return new Subtask(id, title, description,
                        fileBackedTasksManager.epicHashMap.get(Integer.valueOf(epicIdString)));
            default:
                return null;
        }
    }


    private static Integer setTask(TaskType taskType, String line, int maxId, FileBackedTasksManager fileBackedTasksManager) {
        switch (taskType) {
            case EPIC:
                Epic epic = (Epic) fromString(line, TaskType.EPIC, fileBackedTasksManager);
                if (epic != null) {
                    int id = epic.getId();
                    if (id > maxId) {
                        maxId = id;
                    }
                    fileBackedTasksManager.epicHashMap.put(id, epic);
                }
                break;
            case SUBTASK:
                Subtask subtask = (Subtask) fromString(line, TaskType.SUBTASK, fileBackedTasksManager);
                if (subtask != null) {
                    int id = subtask.getId();
                    if (id > maxId) {
                        maxId = id;
                    }
                    fileBackedTasksManager.subtaskHashMap.put(id, subtask);
                }
                break;
            case TASK:
                Task task = fromString(line, TaskType.TASK, fileBackedTasksManager);
                if (task != null) {
                    int id = task.getId();
                    if (id > maxId) {
                        maxId = id;
                    }
                    fileBackedTasksManager.taskHashMap.put(id, task);
                }
                break;
        }
        return maxId;
    }
}