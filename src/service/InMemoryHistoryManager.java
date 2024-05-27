package service;

import model.Task;

import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    private static LinkedList<Task> listOfRecalledTasks = new LinkedList<>();

    @Override
    public void add(Task task) {
        listOfRecalledTasks.add(task);
    }

    @Override
    public LinkedList<Task> getHistory() {
        return listOfRecalledTasks;
    }

    static void updateListOfRecalledTasks(Task task) {
        final int MAX_LIST_SIZE = 10;
        listOfRecalledTasks.add(task);
        if (listOfRecalledTasks.size() > MAX_LIST_SIZE) {
            listOfRecalledTasks.removeFirst();
        }
    }
}
