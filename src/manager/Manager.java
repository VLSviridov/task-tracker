package manager;

import domain.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private int currentId = 0;
    private HashMap<Integer, Task> allTasks = new HashMap<>();
    private HashMap<Integer, Epic> allEpics = new HashMap<>();
    private HashMap<Integer, Subtask> allSubtasks = new HashMap<>();

    // ТЗ 2.1. Получить список всех задач, для каждого типа:
    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(allTasks.values());
    }

    public ArrayList<Epic> getEpicsList() {
        return new ArrayList<>(allEpics.values());
    }

    public ArrayList<Subtask> getSubtasksList() {
        return new ArrayList<>(allSubtasks.values());
    }

    // ТЗ 2.2. Удалить все задачи, для каждого типа:
    public void eraseAllTasks() {
        allTasks.clear();
    }

    public void eraseAllEpics() {
        allSubtasks.clear();
        allEpics.clear();
    }

    public void eraseAllSubtasks() {
        allSubtasks.clear();
        // И обновить эпики:
        for (Epic epic : allEpics.values()) {
            epic.eraseSubtaskIds();
            epic.setStatus(Epic.Status.NEW);
        }
    }

    // ТЗ 2.3. Получить задачу по идентификатору:
    public Task getTaskById(int id) {
        return allTasks.get(id).clone();
    }

    public Epic getEpicById(int id) {
        recalculateEpicStatus(id);  //Актуализировать статус Epic.
        return allEpics.get(id).clone();
    }

    public Subtask getSubtaskById(int id) {
        return allSubtasks.get(id).clone();
    }

    // ТЗ 2.4. "Создать" (добавить) задачу:
    public void add(Task newTask) {
        Task aClone = newTask.clone();

        aClone.setId(++currentId);
        allTasks.put(aClone.getId(), aClone);
    }

    public void add(Epic newEpic) {
        Epic aClone = newEpic.clone();

        aClone.setId(++currentId);
        allEpics.put(aClone.getId(), aClone);
    }

    public void add(Subtask newSubtask) {
        Subtask aClone = newSubtask.clone();

        aClone.setId(++currentId);
        allSubtasks.put(aClone.getId(), aClone);
        allEpics.get(aClone.getEpicId()).addSubtaskId(aClone.getId());  // Добавить идентификатор сабтаска в Epic.
        recalculateEpicStatus(aClone.getEpicId());                      // Обновить статус Epic.
    }

    // ТЗ 2.5. Обновить задачу:
    public void rewrite(Task updatedTask) {
        Task aClone = updatedTask.clone();
        allTasks.put(aClone.getId(), aClone);
    }

    public void rewrite(Epic updatedEpic) {
        Epic aClone = updatedEpic.clone();
        allEpics.put(aClone.getId(), aClone);
    }

    public void rewrite(Subtask updatedSubtask) {
        Subtask aClone = updatedSubtask.clone();

        allSubtasks.put(aClone.getId(), aClone);
        recalculateEpicStatus(aClone.getEpicId()); // Обновить статус Epic.
    }

    // ТЗ 2.6. Удалить по идентификатору:
    public void removeTaskById(int id) {
        allTasks.remove(id);
    }

    public void removeEpicById(int id) {
        for (Integer subId : getEpicById(id).getSubtaskIds()) {
            removeSubtaskById(subId);
        }
        allEpics.remove(id);
    }

    public void removeSubtaskById(int id) {
        int epicId = getSubtaskById(id).getEpicId();
        allSubtasks.remove(id);
        getEpicById(epicId).removeSubtaskId(id);   // Убрать идентификатор из Epic.
        recalculateEpicStatus(epicId);             // Обновить статус Epic.
    }

    // ТЗ 3.1. Получить список всех подзадач эпика.
    public ArrayList<Subtask> getEpicSubtasksList(Epic epic) { // TODO Вопрос к ТЗ: по Epic или по id? Сделано по Epic.
        ArrayList<Subtask> subtasks = new ArrayList<>();

        if (epic.getSubtaskIds() != null && epic.getSubtaskIds().size() != 0) {
            for (int subtaskId : epic.getSubtaskIds()) {
                subtasks.add(getSubtaskById(subtaskId));
            }
        }
        return subtasks;
    }

    public int getCurrentId() {
        return currentId;
    }

    private void recalculateEpicStatus(int id) {
        if (allEpics.get(id).getSubtaskIds() == null || allEpics.get(id).getSubtaskIds().size() == 0) {
            allEpics.get(id).setStatus(Epic.Status.NEW);
        } else {
            ArrayList<Subtask> subtasks = getEpicSubtasksList(allEpics.get(id));
            ArrayList<Subtask.Status> subtaskStatuses = new ArrayList<>();

            for (Subtask s : subtasks)
                subtaskStatuses.add(s.getStatus());
            if (subtaskStatuses.contains(Subtask.Status.IN_PROGRESS)
                    || subtaskStatuses.contains(Subtask.Status.NEW)
                    && subtaskStatuses.contains(Subtask.Status.DONE)) {
                allEpics.get(id).setStatus(Epic.Status.IN_PROGRESS);
            } else if (subtaskStatuses.contains(Subtask.Status.NEW)) {
                allEpics.get(id).setStatus(Epic.Status.NEW);
            } else {
                allEpics.get(id).setStatus(Epic.Status.DONE);
            }
        }
    }
}
