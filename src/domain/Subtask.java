package domain;

import java.util.Objects;

public class Subtask extends Task {
    private int epicId; // TODO Можно упаковать в Integer для явного null (иначе 0 "похож" на валидный айдишник).

    public Subtask(String title, int epicId) {
        super(title);
        this.epicId = epicId;
    }

    private Subtask(Task task) { // конструктор для метода clone()
        super(task);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Subtask other = (Subtask) o;
        return Objects.equals(title, other.title)
                && Objects.equals(description, other.description)
                && Objects.equals(id, other.id)
                && Objects.equals(status, other.status)
                && Objects.equals(epicId, other.epicId);
    }

    @Override // Добавлено поле.
    public int hashCode() {
        int result = super.hashCode();

        result = 31 * result + Integer.hashCode(epicId);
        return result;
    }

    @Override
    public String toString() {
        String result = super.toString().replaceFirst("Task", "Subtask");

        result = result.substring(0, result.length() - 1); // Удалить '}'
        if (epicId != 0) {
            result += ", epicId=" + epicId;
        } else {
            result += ", epicId=undefined";
        }
        return result + "}";
    }

    @Override // Добавлено поле.
    public Subtask clone() {
        Subtask aClone = new Subtask(super.clone()); // FYI: без конструктора не получался upcast.
        aClone.epicId = this.epicId;
        return aClone;
    }

    public int getEpicId() {
        return epicId;
    }
}
