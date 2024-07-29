
package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());

        Task taskOne = new Task("taskOne name", "taskOne description");
        taskManager.createTask(taskOne);
        System.out.println(taskOne);
        System.out.println("uin taskOne = " + taskManager.getUin(taskOne));
        System.out.println("хэшмэп с тасками: " + taskManager.getTaskStorage());
        System.out.println("хэшмэп с эпиками (пока нет ни одного эпика): " + taskManager.getEpicStorage());
        System.out.println("----");

        System.out.println();
        System.out.println("----");
        System.out.println("проверка метода getDefaultHistory");
        System.out.println(Managers.getDefaultHistory());
        System.out.println("----");


        Epic epicOne = new Epic("epicOne name", "epicOne description");
        taskManager.createEpic(epicOne);
        System.out.println(epicOne);
        System.out.println("uin taskOne = " + taskManager.getUin(taskOne));
        System.out.println("uin epicOne = " + taskManager.getUin(epicOne));
        System.out.println("хэшмэп с тасками: " + taskManager.getTaskStorage());
        System.out.println("хэшмэп с эпиками (без сабтасков): " + taskManager.getEpicStorage());
        System.out.println("----");

        Subtask subtaskOne = new Subtask("subtaskOne", "subtaskOne description");
        taskManager.createSubtask(taskManager.getUin(epicOne), subtaskOne);
        Subtask subtaskTwo = new Subtask("subtaskTwo", "subtaskTwo description");
        taskManager.createSubtask(taskManager.getUin(epicOne), subtaskTwo);
        System.out.println(subtaskOne.equals(subtaskTwo));
        System.out.println("uin taskOne = " + taskManager.getUin(taskOne));
        System.out.println("uin epicOne = " + taskManager.getUin(epicOne));
        System.out.println("uin subtaskOne = " + taskManager.getUin(subtaskOne));
        System.out.println("uin subtaskTwo = " + taskManager.getUin(subtaskTwo));
        System.out.println("хэшмэп с эпиками (2 сабтаска): " + taskManager.getEpicStorage());
        System.out.println("печать эпика с 2 сабтасками через getEpic: " +
                taskManager.getEpic(taskManager.getUin(epicOne)));
        System.out.println("----");

        System.out.println(taskManager.getTask(1));
        System.out.println("----");
        System.out.println(taskManager.getEpic(2));
        System.out.println("----");
        System.out.println(taskManager.getTask(1));
        System.out.println(taskManager.getEpic(2));
        System.out.println("----");
        System.out.println("проверка метода showEpicSubtasks");
        System.out.println(taskManager.showEpicSubtasks(taskManager.getUin(epicOne)));
        System.out.println("----");
        Task taskOneUpdated = new Task("taskOneUpdated name", "taskOneUpdated description",
                Status.IN_PROGRESS);
        taskManager.updateTask(1, taskOneUpdated);
        System.out.println(taskOne);

        System.out.println();
        Epic epicOneUpdated = new Epic("epicOneUpdated name", "epicOneUpdated description");
        taskManager.updateEpic(taskManager.getUin(epicOne), epicOneUpdated);
        System.out.println(epicOne);
        System.out.println("----");

        System.out.println("удаление таски:");
        System.out.println("хэшмэп с тасками до удаления: " + taskManager.getTaskStorage());
        taskManager.deleteTask(taskManager.getUin(taskOne));
        System.out.println(taskOne);
        System.out.println("хэшмэп с тасками после удаления: " + taskManager.getTaskStorage());
        System.out.println("----");

        System.out.println("epicStorage c уин кодом удаленного эпика: "
                + taskManager.getEpicStorage().get(taskManager.getUin(epicOne)));
        System.out.println("----");
        System.out.println("вывод всех тасков: ");
        Task taskThree = new Task("taskThree name", "taskThree description");
        taskManager.createTask(taskThree);
        Task taskFour = new Task("taskFour name", "taskFour description");
        taskManager.createTask(taskFour);
        System.out.println("Изменение статуса для taskFour:");
        taskManager.showAllTasks();
        System.out.println("----");
        System.out.println("вывод всех эпиков: ");
        Epic epicTwo = new Epic("epicTwo name", "epicTwo description");
        taskManager.createEpic(epicTwo);
        Subtask subtaskOneEpicTwo = new Subtask("subtaskOneEpicTwo", "subtaskOneEpicTwo description");
        taskManager.createSubtask(taskManager.getUin(epicTwo), subtaskOneEpicTwo);
        Subtask subtaskTwoEpicTwo = new Subtask("subtaskTwoEpicTwo", "subtaskTwoEpicTwo description");
        taskManager.createSubtask(taskManager.getUin(epicTwo), subtaskTwoEpicTwo);
        Subtask subtaskThreeEpicTwo = new Subtask("subtaskThreeEpicTwo",
                "subtaskThreeEpicTwo description");
        taskManager.createSubtask(taskManager.getUin(epicTwo), subtaskThreeEpicTwo);
        subtaskOneEpicTwo.setStatus(Status.IN_PROGRESS);
        subtaskTwoEpicTwo.setStatus(Status.NEW);
        subtaskThreeEpicTwo.setStatus(Status.IN_PROGRESS);
        taskManager.updateEpic(taskManager.getUin(epicTwo), epicTwo);
        System.out.println("вывод информации об epicTwo после смены статусов сабтасков: ");
        System.out.println(taskManager.getEpic(taskManager.getUin(epicTwo)));

        Epic epicThree = new Epic("epicThree name", "epicThree description");
        taskManager.createEpic(epicThree);
        Subtask subtaskOneEpicThree = new Subtask("subtaskOneEpicThree",
                "subtaskOneEpicThree description");
        taskManager.createSubtask(taskManager.getUin(epicThree), subtaskOneEpicThree);
        System.out.println("showAllEpics:");
        taskManager.showAllEpics();


        System.out.println("----");
        System.out.println("вывод всех тасков до очистки всего списка: ");
        taskManager.showAllTasks();

        System.out.println("----");
        System.out.println("вывод сабтаска по уин самого же сабтаска: ");
        System.out.println(taskManager.getSubtask(taskManager.getUin(subtaskOne)));

        System.out.println("----");
        System.out.println("обновление сабтаска с помощью метода updateSubtask:");
        System.out.println("до обновления сабтаска: ");
        System.out.println(taskManager.getEpic(taskManager.getUin(epicTwo)));
        Subtask subtaskNewSubtaskThreeEpicTwo = new Subtask("subtaskNewSubtaskThreeEpicTwo",
                "subtaskNewSubtaskThreeEpicTwo description");
        taskManager.updateSubtask(taskManager.getUin(subtaskThreeEpicTwo), subtaskNewSubtaskThreeEpicTwo,
                Status.NEW);
        System.out.println("после обновления сабтаска: ");
        System.out.println(taskManager.getEpic(taskManager.getUin(epicTwo)));
        System.out.println("----");
        System.out.println("тест метода удаления сабтаска deleteSubtask, вывод эпика после удаления:");
        taskManager.deleteSubtask(taskManager.getUin(subtaskOneEpicTwo));
        System.out.println(taskManager.getEpic(taskManager.getUin(epicTwo)));

        System.out.println();
        System.out.println("----");
        System.out.println("проверка метода getHistory");
        System.out.println(taskManager.getInMemoryHistoryManager().getHistory());
        System.out.println("----");

        System.out.println();
        System.out.println("ПРОВЕРКА МЕТОДОМ printAllTasks");
        printAllTasks(taskManager);

        System.out.println("----");
        System.out.println("тест удаления объектов:");
        System.out.println("вывод коллекции всех тасков:");
        System.out.println(taskManager.getTaskStorage().toString());
        System.out.println("вывод коллекции всех эпиков:");
        System.out.println(taskManager.getEpicStorage().toString());
        System.out.println("вывод коллекции всех сабтасков:");
        System.out.println(taskManager.getSubtaskStorage().toString());
        System.out.println("тест удаления коллекции сабтасков для определенного эпика:");
        taskManager.deleteAllSubtasksForOneEpic(taskManager.getUin(epicTwo));
        System.out.println(taskManager.getEpic(taskManager.getUin(epicTwo)));
        taskManager.deleteEpic(taskManager.getUin(epicTwo));
        System.out.println("----");
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubtasksForAllEpics();
        taskManager.deleteAllEpics();


        System.out.println("вывод всех тасков после очистки коллекции: ");
        taskManager.showAllTasks();
        System.out.println("вывод всех эпиков после очистки коллекции: ");
        taskManager.showAllEpics();
        System.out.println("вывод всех сабтасков после очистки коллекции: ");
        System.out.println(taskManager.getSubtaskStorage().toString());

        System.out.println();
        System.out.println("----");
        System.out.println("проверка метода getHistory");
        System.out.println(taskManager.getInMemoryHistoryManager().getHistory());
        System.out.println("----");

        System.out.println();
        System.out.println("ПРОВЕРКА МЕТОДОМ printAllTasks");
        printAllTasks(taskManager);


    }


    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        manager.showAllTasks();

        System.out.println("Эпики:");
        manager.showAllEpics();

        System.out.println("Подзадачи по УИН номерам эпиков:");
        for (Epic epic : manager.getEpicStorage().values()) {
            System.out.println("1. Эпик '" + epic.getName() + "' с УИН " + epic.getUin() + ".\nЕго подзадачи:");
            System.out.println(manager.showEpicSubtasks(epic.getUin()));
        }

        System.out.println("История:");
        Managers.getDefaultHistory();
    }
}


