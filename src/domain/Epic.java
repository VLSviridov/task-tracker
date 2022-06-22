package domain;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String title) {
        super(title);
    }

    private Epic(Task task) { // конструктор для метода clone()
        super(task);
    }

    public ArrayList<Integer> getSubtaskIds() { // геттер поля, которое является объектом - возвращает новый объект.
        return new ArrayList<>(subtaskIds);
    }

    public ArrayList<Integer> getBROKENSubtaskIds() { // TODO УДАЛИТЬ. Создан для демонстрации.
        return subtaskIds;
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public void removeSubtaskId(int id) {
        subtaskIds.remove(id);
    }

    public void deleteSubtaskIds() {
        subtaskIds.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Epic other = (Epic) o;
        return Objects.equals(title, other.title)
                && Objects.equals(description, other.description)
                && Objects.equals(id, other.id)
                && Objects.equals(status, other.status)
                && Objects.equals(subtaskIds, other.subtaskIds);
    }

    @Override // Добавлено поле
    public int hashCode() {
        int result = super.hashCode();

        result *= 31;
        if (subtaskIds != null) {
            result += subtaskIds.hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        String result = super.toString().replaceFirst("Task", "Epic");

        result = result.substring(0, result.length() - 1); // Удалить '}'
        if (subtaskIds != null) {
            result += ", subtaskIds=" + subtaskIds;
        } else {
            result += ", subtaskIds=null";
        }
        return result + "}";
    }

    @Override // Добавлено поле.
    public Epic clone() {
        Epic aClone = new Epic(super.clone());// FYI: без конструктора не получился upcast //... = (Epic) super.clone();
        aClone.subtaskIds = this.subtaskIds;
        return aClone;
    }
}
