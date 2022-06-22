// TODO Проблема с setStatus(): на данный момент, возможно кратковременно установить произвольный статус для Epic
//  (потомка Task). Это кажется неизбежным, если НЕ держать Manager в одном пакете с Epic: т.к. setStatus() должен быть
//  доступен из Manager, значит этот сеттер public...
//  В данный момент, эта оплошность замаскирована тем фактом, что получение объекта Epic происходит вместе с
//  синхронизацией его статуса. Возможно, это полумера?
//  Тогда мне надо изменить иерархию пакетов так, чтобы package-private setStatus() был в одном пакете с Manager.

package domain;

import java.util.Objects;

public class Task {
    protected String title;
    protected String description;
    protected int id; // TODO Можно упаковать в Integer для явного null (иначе 0 "похож" на валидный айдишник)
    protected Status status;

    public Task(String title) {
        this.title = title;
        status = Status.NEW;
    }

    Task(Task task) { // конструктор для клонирования наследников
        this.title = task.title;
        this.description = task.description;
        this.id = task.id;
        this.status = task.status;
    }

    private Task(String title, String description, int id, Status status) { // конструктор для метода clone()
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status){
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        Task other = (Task) o;

        return Objects.equals(title, other.title)
                && Objects.equals(description, other.description)
                && Objects.equals(id, other.id)
                && Objects.equals(status, other.status);
    }

    @Override
    public int hashCode() {
        int result = 17;

        if (title != null) {
            result += title.hashCode();
        }
        result *= 31;
        if (description != null) {
            result += description.hashCode();
        }
        result = result * 31 + Integer.hashCode(id);
        result *= 31;
        if (status != null) {
            result += status.hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        String result = "Task{";

        if (title != null) {
            result += "title='" + title;
        } else {
            result += "title=null";
        }
        if (description != null) {
            result += "', description.length=" + description.length();
        } else {
            result += "', description=null";
        }
        if (id != 0) {
            result += ", id=" + id;
        } else {
            result += ", id=undefined";
        }
        if (status != null) {
            result += ", status=" + status;
        } else {
            result += ", status=null";
        }
        return result + "}";
    }

    @Override
    public Task clone() {
        return new Task(this.title, this.description, this.id, this.status);
    }
}
