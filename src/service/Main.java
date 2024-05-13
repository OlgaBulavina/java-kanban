package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

public class Main {


    public static void main(String[] args) {
        ObjectManager objectManager = new ObjectManager();
        Task taskOne = new Task("taskOne name", "taskOne description");
        ObjectManager.createTask(taskOne);
        System.out.println(taskOne);
        System.out.println("uin taskOne = " + ObjectManager.getUin(taskOne));
        System.out.println("хэшмэп с тасками: " + ObjectManager.taskStorage);
        System.out.println("хэшмэп с эпиками (пока нет ни одного эпика): " + ObjectManager.epicStorage);
        System.out.println("----");



        Epic epicOne = new Epic("epicOne name", "epicOne description");
        ObjectManager.createEpic(epicOne);
        System.out.println(epicOne);
        System.out.println("uin taskOne = " + ObjectManager.getUin(taskOne));
        System.out.println("uin epicOne = " + ObjectManager.getUin(epicOne));
        System.out.println("хэшмэп с тасками: " + ObjectManager.taskStorage);
        System.out.println("хэшмэп с эпиками (без сабтасков): " + ObjectManager.epicStorage);
        System.out.println("----");

        Subtask subtaskOne = new Subtask("subtaskOne", "subtaskOne description");
        ObjectManager.createSubtask(objectManager.getUin(epicOne), subtaskOne);
        Subtask subtaskTwo = new Subtask("subtaskTwo", "subtaskTwo description");
        ObjectManager.createSubtask(objectManager.getUin(epicOne), subtaskTwo);
        System.out.println(subtaskOne.equals(subtaskTwo));
        System.out.println("uin taskOne = " + ObjectManager.getUin(taskOne));
        System.out.println("uin epicOne = " + ObjectManager.getUin(epicOne));
        System.out.println("uin subtaskOne = " + ObjectManager.getUin(subtaskOne));
        System.out.println("uin subtaskTwo = " + ObjectManager.getUin(subtaskTwo));
        System.out.println("хэшмэп с эпиками (2 сабтаска): " + ObjectManager.epicStorage);
        System.out.println("печать эпика с 2 сабтасками через getEpic: " +
                objectManager.getEpic(objectManager.getUin(epicOne)));
        System.out.println("----");

        System.out.println(objectManager.getTask(1));
        System.out.println("----");
        System.out.println(objectManager.getEpic(2));
        System.out.println("----");
        System.out.println(objectManager.getTask(1));
        System.out.println(objectManager.getEpic(2));
        System.out.println("----");
        System.out.println("проверка метода showEpicSubtasks");
        System.out.println(objectManager.showEpicSubtasks(objectManager.getUin(epicOne)));
        System.out.println("----");
        Task taskOneUpdated = new Task("taskOneUpdated name", "taskOneUpdated description",
                Status.IN_PROGRESS);
        objectManager.updateTask(1, taskOneUpdated);
        System.out.println(taskOne);

        System.out.println();
        Epic epicOneUpdated = new Epic("epicOneUpdated name", "epicOneUpdated description");
        objectManager.updateEpic(objectManager.getUin(epicOne), epicOneUpdated);
        System.out.println(epicOne);
        System.out.println("----");

        System.out.println("удаление таски:");
        System.out.println("хэшмэп с тасками до удаления: " + ObjectManager.taskStorage);
        objectManager.deleteTask(ObjectManager.getUin(taskOne));
        System.out.println(taskOne);
        System.out.println("хэшмэп с тасками после удаления: " + ObjectManager.taskStorage);
        System.out.println("----");

        System.out.println("epicStorage c уин кодом удаленного эпика: "
                + ObjectManager.epicStorage.get(objectManager.getUin(epicOne)));
        System.out.println("----");
        System.out.println("вывод всех тасков: ");
        Task taskThree = new Task("taskThree name", "taskThree description");
        ObjectManager.createTask(taskThree);
        Task taskFour = new Task("taskFour name", "taskFour description");
        ObjectManager.createTask(taskFour);
        System.out.println("Изменение статуса для taskFour:");
        objectManager.showAllTasks();
        System.out.println("----");
        System.out.println("вывод всех эпиков: ");
        Epic epicTwo = new Epic("epicTwo name", "epicTwo description");
        ObjectManager.createEpic(epicTwo);
        Subtask subtaskOneEpicTwo = new Subtask("subtaskOneEpicTwo", "subtaskOneEpicTwo description");
        ObjectManager.createSubtask(objectManager.getUin(epicTwo), subtaskOneEpicTwo);
        Subtask subtaskTwoEpicTwo = new Subtask("subtaskTwoEpicTwo", "subtaskTwoEpicTwo description");
        ObjectManager.createSubtask(objectManager.getUin(epicTwo), subtaskTwoEpicTwo);
        Subtask subtaskThreeEpicTwo = new Subtask("subtaskThreeEpicTwo",
                "subtaskThreeEpicTwo description");
        ObjectManager.createSubtask(objectManager.getUin(epicTwo), subtaskThreeEpicTwo);
        subtaskOneEpicTwo.setStatus(Status.NEW);
        subtaskTwoEpicTwo.setStatus(Status.NEW);
        subtaskThreeEpicTwo.setStatus(Status.IN_PROGRESS);
        objectManager.updateEpic(ObjectManager.getUin(epicTwo), epicTwo);
        System.out.println("вывод информации об epicTwo после смены статусов сабтасков: ");
        System.out.println(objectManager.getEpic(ObjectManager.getUin(epicTwo)));

        Epic epicThree = new Epic("epicThree name", "epicThree description");
        ObjectManager.createEpic(epicThree);
        Subtask subtaskOneEpicThree = new Subtask("subtaskOneEpicThree",
                "subtaskOneEpicThree description");
        ObjectManager.createSubtask(objectManager.getUin(epicThree), subtaskOneEpicThree);
        System.out.println("showAllEpics:");
        objectManager.showAllEpics();


        System.out.println("----");
        System.out.println("вывод всех тасков до очистки всего списка: ");
        objectManager.showAllTasks();

        System.out.println("----");
        System.out.println("вывод сабтаска по уин самого же сабтаска: ");
        System.out.println(objectManager.getSubtask(objectManager.getUin(subtaskOne)));

        System.out.println("----");
        System.out.println("обновление сабтаска с помощью метода updateSubtask:");
        System.out.println("до обновления сабтаска: ");
        System.out.println(objectManager.getEpic(ObjectManager.getUin(epicTwo)));
        Subtask subtaskNewSubtaskThreeEpicTwo = new Subtask("subtaskNewSubtaskThreeEpicTwo",
                "subtaskNewSubtaskThreeEpicTwo description");
        objectManager.updateSubtask(ObjectManager.getUin(subtaskThreeEpicTwo), subtaskNewSubtaskThreeEpicTwo,
                Status.NEW);
        System.out.println("после обновления сабтаска: ");
        System.out.println(objectManager.getEpic(ObjectManager.getUin(epicTwo)));
        System.out.println("----");
        System.out.println("тест метода удаления сабтаска deleteSubtask, вывод эпика после удаления:");
        objectManager.deleteSubtask(ObjectManager.getUin(subtaskOneEpicTwo));
        System.out.println(objectManager.getEpic(ObjectManager.getUin(epicTwo)));

        System.out.println("----");
        System.out.println("тест удаления объектов:");
        System.out.println("вывод коллекции всех тасков:");
        System.out.println(objectManager.taskStorage.toString());
        System.out.println("вывод коллекции всех эпиков:");
        System.out.println(objectManager.epicStorage.toString());
        System.out.println("вывод коллекции всех сабтасков:");
        System.out.println(objectManager.subtaskStorage.toString());
        System.out.println("тест удаления коллекции сабтасков для определенного эпика:");
        objectManager.deleteAllSubtasksForOneEpic(ObjectManager.getUin(epicTwo));
        System.out.println(objectManager.getEpic(ObjectManager.getUin(epicTwo)));
        System.out.println("тест удаления определенного эпика:");
        objectManager.deleteEpic(ObjectManager.getUin(epicTwo));
        System.out.println(objectManager.getEpic(ObjectManager.getUin(epicTwo)));
        System.out.println("----");
        objectManager.deleteAllTasks();
        objectManager.deleteAllEpics();
        objectManager.deleteAllSubtasksForAllEpics();

        System.out.println("вывод всех тасков после очистки коллекции: ");
        objectManager.showAllTasks();
        System.out.println("вывод всех эпиков после очистки коллекции: ");
        objectManager.showAllEpics();
        System.out.println("вывод всех сабтасков после очистки коллекции: ");
        System.out.println(objectManager.subtaskStorage.toString());



    }
}
