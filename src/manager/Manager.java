package manager;

import domain.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private int currentId = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    // ТЗ 2.1. Получить список всех задач, для каждого типа:
    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasksList() {
        return new ArrayList<>(subtasks.values());
    }

    // ТЗ 2.2. Удалить все задачи, для каждого типа:
    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpics() {
        subtasks.clear();
        epics.clear();
    }

    public void deleteSubtasks() {
        subtasks.clear();
        // И обновить эпики:
        for (Epic epic : epics.values()) {
            epic.deleteSubtaskIds();
            epic.setStatus(Status.NEW);
        }
    }

    // TODO УДАЛИТЬ три метода ниже, созданные для демонстрации.
    public Task getBROKENTask(int id) {
        return tasks.get(id);
    }

    public void addBROKEN(Task newTask) {
        newTask.setId(++currentId);
        tasks.put(newTask.getId(), newTask);
    }

    public void updateBROKEN(Task updatedTask) {
        tasks.put(updatedTask.getId(), updatedTask);
    }

    // ТЗ 2.3. Получить задачу по идентификатору:
    public Task getTask(int id) {
        return tasks.get(id).clone();
    }

    public Epic getEpic(int id) {
        recalculateEpicStatus(id);  //Актуализировать статус Epic.
        return epics.get(id).clone();
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id).clone();
    }

    // ТЗ 2.4. "Создать" (добавить) задачу:
    public void add(Task newTask) {
        Task aClone = newTask.clone();

        aClone.setId(++currentId);
        tasks.put(aClone.getId(), aClone);
    }

    public void add(Epic newEpic) {
        Epic aClone = newEpic.clone();

        aClone.setId(++currentId);
        epics.put(aClone.getId(), aClone);
    }

    public void add(Subtask newSubtask) {
        if (epics.get(newSubtask.getEpicId()) == null) {
            System.out.println("Ошибка: привязанного эпика не существует. Сабтаск не был добавлен.");
            return;
        }

        Subtask aClone = newSubtask.clone();

        aClone.setId(++currentId);
        subtasks.put(aClone.getId(), aClone);
        epics.get(aClone.getEpicId()).addSubtaskId(aClone.getId());  // Добавить идентификатор сабтаска в Epic.
        recalculateEpicStatus(aClone.getEpicId());                   // Обновить статус Epic.
    }

    // ТЗ 2.5. Обновить задачу:
    public void update(Task updatedTask) {
        Task aClone = updatedTask.clone();
        tasks.put(aClone.getId(), aClone);
    }

    public void update(Epic updatedEpic) {
        Epic aClone = updatedEpic.clone();
        epics.put(aClone.getId(), aClone);
    }

    public void update(Subtask updatedSubtask) {
        Subtask aClone = updatedSubtask.clone();

        subtasks.put(aClone.getId(), aClone);
        recalculateEpicStatus(aClone.getEpicId()); // Обновить статус Epic.
    }

    // ТЗ 2.6. Удалить по идентификатору:
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteEpic(int id) {
        for (Integer subId : getEpic(id).getSubtaskIds()) {
            deleteSubtask(subId);
        }
        epics.remove(id);
    }

    public void deleteSubtask(int id) {
        int epicId = getSubtask(id).getEpicId();
        subtasks.remove(id);
        getEpic(epicId).removeSubtaskId(id);   // Убрать идентификатор из Epic.
        recalculateEpicStatus(epicId);         // Обновить статус Epic.
    }

    // ТЗ 3.1. Получить список всех подзадач эпика.
    public ArrayList<Subtask> getEpicSubtasksList(int id) {
        ArrayList<Subtask> subtasks = new ArrayList<>();

        if (epics.get(id).getSubtaskIds() != null && epics.get(id).getSubtaskIds().size() != 0) {
            for (int subtaskId : epics.get(id).getSubtaskIds()) {
                subtasks.add(getSubtask(subtaskId));
            }
        }
        return subtasks;
    }

    public int getCurrentId() {
        return currentId;
    }

    private void recalculateEpicStatus(int id) {
        if (epics.get(id).getSubtaskIds() == null || epics.get(id).getSubtaskIds().size() == 0) {
            epics.get(id).setStatus(Status.NEW);
        } else if (areEpicSubtaskStatusesIdentical(id)) {
            Status first = getSubtask(epics.get(id).getSubtaskIds().get(0)).getStatus(); //Статус первого сабтаска эпика
            epics.get(id).setStatus(first);
        } else {
            epics.get(id).setStatus(Status.IN_PROGRESS);
        }
    }

    private boolean areEpicSubtaskStatusesIdentical(int id) {
        boolean areIdentical = true;
        Status first = null;
        int index = -1;

        for (int subtaskId : epics.get(id).getSubtaskIds()) { // Итерирование по сабтаскам один раз до первого
            // несовпадения, без формирования полного списка сабтасков
            index++;
            if (index == 0) {
                first = subtasks.get(subtaskId).getStatus(); // Эталонный статус
            } else {
                if (!subtasks.get(subtaskId).getStatus().equals(first)) {
                    areIdentical = false;
                    break;
                }
            }
        }
        return areIdentical;
    }
}
