/*
Спасибо за ваше ревью и марк-апы! Супер.

1. Выполнены обновления по марк-апам:
    1.1. Manager & Epic: заменил "erase" на "delete".
    1.2. Manager: реализовал поиск getEpicSubtasksList() по int id (было по Epic epic).
    1.3. Status (NEW): вынес enum в отдельный класс Status. Код стал гораздо чище, спасибо =)
    1.4. Task, Subtask, Epic: переместил геттеры и сеттеры выше переопределённых методов.


2. Выполнены обновления самостоятельно:
    2.1. [codestyle] Manager: также переименовал списки "allTasks" в "tasks" (аналогично "epics" и "subtasks").
    Причина: написание "all" больше усложняло названия методов и читаемость кода, чем несло пользы, как мне кажется.
    Слово "все" вытекает по смыслу. В названиях полей всегда смотрю на название класса. Поэтому список из "Задач",
    являющийся полем Менеджера задач, под названием "задачи" - уже довольно очевидно, что это 'ВСЕ' задачи,
    а не какие-то рандомные задачи.
    2.2. [codestyle] Manager: убрал излишнюю подпись "...ById()" в методах с параметром "int id" (self-explanitory).
    2.3. [delete] Main: удалил тесты клонов/копий. (Тесты прогнали, выводы сделал; далее нет необходимости держать их)


3. НЕ выполнены обновления по марк-апам. Либо я их неправильно/не полностью понял, либо они ведут к багам, которые
демонстрирую в тестах в классе Main:
    3.1. Epic, getSubtaskIds(): "геттер должен возвращать существующее поле - subtaskIds".
    Комментарий: ЕСЛИ я правильно прочёл, то вы советуете передавать поле subtaskIds через геттер напрямую.
    Добавил такую реализацию геттера в Epic: getBROKENSubtaskIds().
    Воспроизвёл баг, который появится в программе с таким геттером, в Main: testCopyInGetSubtaskIds().
    Нарушается принцип инкапсуляции: изменение поля становится доступно не из сеттера, а откуда угодно.
        Вывод: если делать такой геттер, то нужно "помнить", что у нас заложен баг в программе в данном геттере.
        Рассчитывать на это - кажется плохой практикой.
            "+" предлагаемой реализации: сохранение байтов памяти на новый объект и миллисекунд на конструктор.
            "+" текущей реализации: мы получаем абсолютно идентичный полю список (в моменте). И не получаем баг.
        Можно ли попросить оставить текущую реализацию?
        Зачем передавать само поле?
    Возможно я не так понял маркап - в таком случае, просьба раскрыть его, либо скорректировать getBROKENSubtaskIds().

    3.2. Task, Epic, Subtask, Manager: "использование и переопределение clone() излишне."
    clone() использовался в Manager:
    - getTask(int),
    - getEpic(int),
    - getSubtask(int),
    - add(Task),
    - add(Subtask),
    - add(Epic),
    - update(Task),
    - update(Subtask),
    - update(Epic).
        А.
        Если предполагается, что копия объекта __вообще__ не нужна, и мы просто убираем ".clone()" из методов выше,
        то ситуация аналогична 3.1: заложим баги и вроде как без надобности (возможно я всего не знаю, может программа
        будет развиваться, и клоны начнут мешать. Значит можно решить эту проблему, когда она появится?).
        Тесты, по которым воспроизвёл баги, в Main:
        - testCloneInGetTask(Manager),
        - testCloneInAdd(Manager),
        - testCloneInUpdate(Manager).
        Б.
        Если предполагается __заменить__ метод clone() на идентичный код, то содержание метода clone() будет многократно
        копировано в методы выше. Например, вместо:
            public Epic getEpic(int id) {
                return epics.get(id).clone();
            }
        Будет:
            public Epic getEpic(int id) {
                Epic thisEpic = epics.get(id);
                return new Epic(thisEpic.getTitle(), thisEpic.getDescription(), id, thisEpic.getStatus(),
                thisEpic.getSubtaskIds());
            }
        По-моему, это и читается, и воспринимается сложнее! Уход от абстракций. Плюс copy-paste.
     Возможно этот маркап я тоже неверно понял - в таком случае, просьба раскрыть его.


     P.S. Я доступен в Slack, можно созвониться, если так быстрее/удобнее объяснить. Буду рад!

     Спасибо,
     С уважением, Виктор Свиридов
*/

import domain.*;
import manager.*;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Manager m = new Manager();

        System.out.println("Тестирования по ТЗ:\n");
        testTasks(m); // Тестирование Task - успешно.
        System.out.println();
        testEpicsAndSubtasks(m); // Тестирование Epic & Subtask - успешно.
        System.out.println("\n\n\n\n\n\n\n\nДемонстрация ошибок при внедрении маркапов (в одной из их интерпретаций):");
        System.out.println("\n3.1.");
        testCopyInGetSubtaskIds();
        System.out.println("\n3.2.");
        testCloneInGetTask(m);
        System.out.println('\n');
        testCloneInAdd(m);
        System.out.println('\n');
        testCloneInRewrite(m);
        System.out.println('\n');
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
        t1.setStatus(Status.IN_PROGRESS); // Локальным оригиналам меняем статус.
        t2.setStatus(Status.DONE);
        m.update(t1); // И переписываем копии в менеджере.
        m.update(t2);
        // 4. Проверить, что статусы изменились.
        System.out.println("Новый статус t1: " + m.getTask(t1.getId()).getStatus());
        System.out.println("Новый статус t2: " + m.getTask(t2.getId()).getStatus());
        // 5. Удалить один Task
        m.deleteTask(t1.getId());
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
        e1.setStatus(Status.DONE); // Локальным эпикам меняем статус.
        e2.setStatus(Status.DONE);
        m.update(e1); // И переписываем копии эпиков в менеджере.
        m.update(e2);
        // 4А. Проверить, что статусы НЕ изменились (нелегальная операция).
        System.out.println("Новый статус e1 (после попытки изменить произвольно): " +
                m.getEpic(e1.getId()).getStatus());
        System.out.println("Новый статус e2 (после попытки изменить произвольно): " +
                m.getEpic(e2.getId()).getStatus());
        // 3Б. Изменить статусы созданных Subtask
        s1.setStatus(Status.IN_PROGRESS); // Локальным сабтаскам меняем статус.
        s2.setStatus(Status.DONE);
        m.update(s1); // И переписываем копии сабтасков в менеджере.
        m.update(s2);
        // 4Б. Проверить, что статусы Subtask изменились, а статус Epic рассчитался по статусам подзадач.
        System.out.println("Новый статус e1: " + m.getEpic(e1.getId()).getStatus());
        System.out.println("Новый статус s1: " + m.getSubtask(s1.getId()).getStatus());
        System.out.println("Новый статус s2: " + m.getSubtask(s2.getId()).getStatus());
        // 5. Удалить один Epic
        m.deleteEpic(e2.getId());
        for (Epic e : m.getEpicsList())
            System.out.println(e);
    }

    // Ниже - демонстрация ошибки при возврате геттером объекта-поля:
    public static void testCopyInGetSubtaskIds() {
        Epic e = new Epic("");

        e.addSubtaskId(5); // Привязали Новый эпик к id сабтаску 5.

        ArrayList<Integer> noBug = e.getSubtaskIds();

        noBug.add(10); // Пытаемся сеять баг.
        System.out.println("Возврат копии объекта в геттере - отлично, баг никак не внести. " +
                "[5] = " + e.getSubtaskIds()); // Напечатает... [5] = [5]

        ArrayList<Integer> bug = e.getBROKENSubtaskIds();

        bug.add(10); // Сеем баг - ДОСТУП К ЛЮБОМУ ПРОИЗВОЛЬНОМУ ИЗМЕНЕНИЮ ПОЛЯ БЕЗ СЕТТЕРА
        System.out.println("Возврат объекта в геттере - нарушена инкапсуляция. Баг внесён успешно. " +
                "[5, 6] = " + e.getBROKENSubtaskIds()); // Напечатает: ... [5] = [5, 10]
        // Читать такое поле тоже становится можно без геттера: (просто это мЕньшая беда)
        e.addSubtaskId(6);
        System.out.println("Читать поле тоже можем без геттера, если вдруг его не было. Внесли [6] в поле? Да: " +
                "[5, 10, 6] = " + bug); // ДОСТУП К ПОЛУЧЕНИЮ ПОЛЯ БЕЗ ГЕТТЕРА
        // Вывод: геттер getBROKENSubtaskIds() нарушил принцип инкапсуляции при возврате поля-объекта.
        // Доступ к полю появился в обход сеттера и геттера.
    }

    public static void testCloneInGetTask(Manager m) { //getEpic(int) и getSubtask(int) аналогично.
        Task noBug = m.getTask(2);                     // TODO тест завязан аналогично TODO выше

        noBug.setTitle("Клон не даст подступиться к филдам оригинального объекта");
        System.out.println(m.getTask(2).getTitle()); // Работает корректно с клоном: название объекта в менеджере не изменено.

        Task bug = m.getBROKENTask(2);

        bug.setTitle("Без клона можем внести изменения в поля объекта в обход сеттеров");
        System.out.println(m.getBROKENTask(2).getTitle()); // Изменили название в строке выше, без сеттера.
    }

    public static void testCloneInAdd(Manager m) {
        Task noBug = new Task("Новая задача для add");

        m.add(noBug);
        int id1 = m.getCurrentId();

        noBug.setTitle("Клон не даст подступиться к филдам оригинального объекта");
        System.out.println(m.getTask(id1).getTitle()); // Работает корректно с клоном: название объекта в менеджере не изменено.

        Task bug = new Task("Новая задача для add");

        m.addBROKEN(bug);

        int id2 = m.getCurrentId();

        bug.setTitle("Без клона можем внести изменения в поля объекта в обход сеттеров");
        System.out.println(m.getTask(id2).getTitle()); // Изменили название в строке выше, без сеттера.
    }

    public static void testCloneInRewrite(Manager m) {
        Task noBug = new Task("Новая задача для update");

        m.add(noBug);

        int id1 = m.getCurrentId();

        noBug.setId(id1); // Привязать id к локальной копии для легального update.

        noBug.setTitle("Легальный update через Manager");
        m.update(noBug); // Работает корректно только с клоном.
        noBug.setTitle("Клон не даст подступиться к филдам оригинального объекта");
        System.out.println(m.getTask(id1).getTitle());

        Task bug = new Task("Новая задача для update");

        m.add(bug);

        int id2 = m.getCurrentId();

        bug.setId(id2); // Привязать id к локальной копии для легального update.

        bug.setTitle("Легальный update через Manager");
        m.updateBROKEN(bug); // Работает корректно только с клоном.
        bug.setTitle("Без клона можем внести изменения в поля объекта в обход сеттеров");
        System.out.println(m.getTask(id2).getTitle());
    }
}