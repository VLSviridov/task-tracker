// 1. Открыт к критике. Спасибо!)

// 2. Дополнительные улучшения проекта интересны (наставник говорил, что, как развитие проекта, и для упражнения, можно
// сделать эпики и сабстаски immutable. Мы будем проходить immutable и mutators далее, или пора погружаться
// самостоятельно?) Заранее спасибо!

/* FYI: Чтобы разобраться в роли клонов (и копий объектов) в методах, создал по тесту на каждый метод, где предполагал
использовать клон или копию объекта. Каждый тест пытается создать баг в случае НЕиспользования копии или клона. Из
тестов узнал, что стандартные enums - immutable (благодаря чему, в setStatus() и getStatus() невозможно посеять баг).*/

import domain.*;
import manager.*;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Manager m = new Manager();
        testTasks(m); // Тестирование Task по ТЗ - успешно.
        System.out.println();
        testEpicsAndSubtasks(m); // Тестирование Epic & Subtask по ТЗ - успешно.
        System.out.println();
        // Тестирования клонов:
        testCloneInSetStatus(); // Клон стандартного enum оказался не нужным.
        System.out.println();
        testCloneInGetStatus(); // Клон стандартного enum оказался не нужным.
        System.out.println();
        testCloneInGetSubtaskIds(); // Геттер объекта (ArrayList) обязан вернуть новый объект (иначе - баг).
        System.out.println();
        testCloneInGetTasksList(m); // Создаёт и возвращает локальный список, поэтому заведомо работает корректно.
        System.out.println();
        testCloneInGetTaskById(m); // Геттер объекта (Task) обязан вернуть новый объект (иначе - баг).
        System.out.println();
        testCloneInAdd(m); // Клон для аргумента сеттера объекта (Task) - необходим.
        System.out.println();
        testCloneInRewrite(m); // Клон для аргумента сеттера объекта (Task) - необходим.
        System.out.println();
        testCloneInGetEpicSubtasksList(m); // Создаёт и возвращает локальный список, поэтому работает корректно.
    }

    public static void testTasks(Manager m) {
        // 1. Добавить два простых таска
        Task t1 = new Task("1-я задача (1-й простой таск)");
        Task t2 = new Task("2-я задача (2-й простой таск)");

        m.add(t1); // Копия t1 добавлена в ХэшМап менеджера, и получила id = currentId.
        t1.setId(m.getCurrentId()); // Локальному оригиналу t1 (в Main) тоже запомним id = currentId.
        m.add(t2); // Копия t2 добавлена в ХэшМап менеджера, и получила id = currentId.
        t2.setId(m.getCurrentId()); // Локальному оригиналу t2 (в Main) тоже запомним id = currentId.
        // 2. Распечатать список Task из менеджера.
        for (Task t : m.getTasksList())
            System.out.println(t);
        // 3. Изменить статусы созданных Task.
        t1.setStatus(Task.Status.IN_PROGRESS); // Локальным оригиналам меняем статус.
        t2.setStatus(Task.Status.DONE);
        m.rewrite(t1); // И переписываем копии в менеджере.
        m.rewrite(t2);
        // 4. Проверить, что статусы изменились.
        System.out.println("Новый статус t1: " + m.getTaskById(t1.getId()).getStatus());
        System.out.println("Новый статус t2: " + m.getTaskById(t2.getId()).getStatus());
        // 5. Удалить один Task
        m.removeTaskById(t1.getId());
        for (Task t : m.getTasksList())
            System.out.println(t);
    }

    public static void testEpicsAndSubtasks(Manager m) {
        // 1. Добавить два эпика: один с двумя сабстасками, другой без сабтасков.
        Epic e1 = new Epic("3-я задача (1-й эпик)");
        Epic e2 = new Epic("4-я задача (2-й эпик)");

        m.add(e1); // Копия e1 добавлена в ХэшМап менеджера, и получила id = currentId.
        e1.setId(m.getCurrentId()); // Локальному оригиналу e1 (в Main) тоже запомним id = currentId.
        m.add(e2); // Копия e2 добавлена в ХэшМап менеджера, и получила id = currentId.
        e2.setId(m.getCurrentId()); // Локальному оригиналу e2 (в Main) тоже запомним id = currentId.

        Subtask s1 = new Subtask("5-я задача (1-й сабтаск 1-го эпика)", e1.getId()); // Epic id локального сабтаска
                                                                                     // определён при создании.
        Subtask s2 = new Subtask("6-я задача (2-й сабтаск 1-го эпика)", e1.getId());

        m.add(s1); // Копия s1 добавлена в ХэшМап менеджера, и получила id = currentId. Эпик в менеджере обновлён.
        s1.setId(m.getCurrentId()); // Локальному оригиналу s1 (в Main) тоже запомним id = currentId.
        m.add(s2); // Копия s2 добавлена в ХэшМап менеджера, и получила id = currentId.  Эпик в менеджере обновлён.
        s2.setId(m.getCurrentId()); // Локальному оригиналу s2 (в Main) тоже запомним id = currentId.

        // 2. Распечатать списки Epic и Subtask из менеджера.
        for (Epic e : m.getEpicsList())
            System.out.println(e);
        for (Subtask s : m.getSubtasksList())
            System.out.println(s);
        // 3А. Изменить статусы созданных Epic (нелегальная операция).
        e1.setStatus(Epic.Status.DONE); // Локальным эпикам меняем статус.
        e2.setStatus(Epic.Status.DONE);
        m.rewrite(e1); // И переписываем копии эпиков в менеджере.
        m.rewrite(e2);
        // 4А. Проверить, что статусы НЕ изменились (нелегальная операция).
        System.out.println("Новый статус e1 (после попытки изменить произвольно): " +
                m.getEpicById(e1.getId()).getStatus());
        System.out.println("Новый статус e2 (после попытки изменить произвольно): " +
                m.getEpicById(e2.getId()).getStatus());
        // 3Б. Изменить статусы созданных Subtask
        s1.setStatus(Subtask.Status.IN_PROGRESS); // Локальным сабтаскам меняем статус.
        s2.setStatus(Subtask.Status.DONE);
        m.rewrite(s1); // И переписываем копии сабтасков в менеджере.
        m.rewrite(s2);
        // 4Б. Проверить, что статусы Subtask изменились, а статус Epic рассчитался по статусам подзадач.
        System.out.println("Новый статус e1: " + m.getEpicById(e1.getId()).getStatus());
        System.out.println("Новый статус s1: " + m.getSubtaskById(s1.getId()).getStatus());
        System.out.println("Новый статус s2: " + m.getSubtaskById(s2.getId()).getStatus());
        // 5. Удалить один Epic
        m.removeEpicById(e2.getId());
        for (Epic e : m.getEpicsList())
            System.out.println(e);
    }

    // Ниже - тестирование клонов.
    // Проверяю, что будет, если не использовать клон или копию в определённом методе: сею баг в программу.
    // 1. Методы класса Task (и наследников):
    public static void testCloneInSetStatus() {
        Task t = new Task("Новая задача");
        Task.Status x = Task.Status.IN_PROGRESS;

        t.setStatus(x); // IN_PROGRESS установлен
        x = Task.Status.DONE; // Пытаюсь посеять баг: изменить статус t без сеттера.
        System.out.println("IN_PROGRESS = " + t.getStatus()); // Работает корректно без клона.
    }

    public static void testCloneInGetStatus() {
        Task t = new Task("Новая задача");

        t.setStatus(Task.Status.IN_PROGRESS); // Работает корректно без клона.

        Task.Status bug = t.getStatus();
        bug = Task.Status.DONE; // Пытаюсь посеять баг
        System.out.println("IN_PROGRESS = " + t.getStatus());
    }

    // 2. Методы класса Epic:
    public static void testCloneInGetSubtaskIds() {
        Epic e = new Epic("Новая задача");

        e.addSubtaskId(5);
        // TODO тест завязан на id предыдущих тестов. Не стал развязывать - полагаю удалить тест в следующей итерации.
        e.addSubtaskId(6);

        ArrayList<Integer> bug = e.getSubtaskIds(); // Работает корректно только с возвратом новой копии.
        bug.add(10); // Сеем баг
        System.out.println("[5, 6] = " + e.getSubtaskIds());
        // Код метода без возврата копии (для создания бага) - ниже.
        /* return subtaskIds;*/
        // Напечатает: [5, 6] = [5, 6, 10]
    }

    // 3. Методы класса Manager:
    public static void testCloneInGetTasksList(Manager m) { //getEpicsList() и getSubtasksList() аналогично.
        ArrayList<Task> localTaskList = m.getTasksList(); // Создаёт и возвращает новый лист,поэтому работает корректно.
        Task bug = new Task("Баг");

        localTaskList.add(bug); // Сеем баг.
        for (Task t : m.getTasksList())
            System.out.println(t); // Лишних тасков не добавлено.
    }

    public static void testCloneInGetTaskById(Manager m) { //getEpicById(int) и getSubtaskById(int) аналогично.
        Task x = m.getTaskById(2); // Работает корректно только с клоном.  // TODO тест завязан аналогично TODO выше

        x.setTitle("БАГ!!!");
        System.out.println(m.getTaskById(2));
        // Код метода без возврата копии (для создания бага) - ниже.
        /* return allTasks.get(id);*/
        // Напечатает: Task{title='БАГ!!!', description=null, id=2, status=DONE}
    }

    public static void testCloneInAdd(Manager m) {
        Task t = new Task("Новая задача для add");

        m.add(t); // Работает корректно только с клоном.

        int id = m.getCurrentId();

        t.setTitle("Баг");
        System.out.println(m.getTaskById(id));
        // Код метода без возврата копии (для создания бага) - ниже.
        /* newTask.setId(++currentId);
        allTasks.put(newTask.getId(), newTask);*/
        // Напечатает: Task{title='Баг', description=null, id=7, status=NEW}
    }

    public static void testCloneInRewrite(Manager m) {
        Task t = new Task("Новая задача для rewrite");

        m.add(t);

        int id = m.getCurrentId();

        t.setId(id); // Привязать id к локальной копии для легального rewrite.

        t.setTitle("Легальный rewrite через Manager");
        m.rewrite(t); // Работает корректно только с клоном.
        System.out.println(m.getTaskById(id));

        t.setTitle("Rewrite задней пяткой");
        System.out.println(m.getTaskById(id));
        // Код метода без возврата копии (для создания бага) - ниже.
        /* allTasks.put(updatedTask.getId(), updatedTask);*/
        // Напечатает:
        //  Task{title='Легальный rewrite через Manager', description=null, id=8, status=NEW}
        //  Task{title='Rewrite задней пяткой', description=null, id=8, status=NEW}
    }

    public static void testCloneInGetEpicSubtasksList(Manager m) {
        // Добавление тасков аналогично testEpicsAndSubtasks(Manager).
        Epic e1 = new Epic("Новый эпик");

        m.add(e1);

        int id = m.getCurrentId();
        e1.setId(id);

        Subtask s1 = new Subtask("Новый сабстаск - 1", id);
        Subtask s2 = new Subtask("Новый сабстаск - 2", id);

        m.add(s1);
        s1.setId(m.getCurrentId());
        m.add(s2);
        s2.setId(m.getCurrentId());

        System.out.println("Было:");
        for (Subtask s : m.getEpicSubtasksList(m.getEpicById(id)))
            System.out.println(s);

        ArrayList<Subtask> corruptList = m.getEpicSubtasksList(m.getEpicById(id));  // Создаёт и возвращает новый лист,
                                                                                    // поэтому работает корректно.
        Subtask bug = new Subtask("Баг", id);

        corruptList.add(bug);
        System.out.println("Стало:");
        for (Subtask s : m.getEpicSubtasksList(m.getEpicById(id)))
            System.out.println(s);
    }
}