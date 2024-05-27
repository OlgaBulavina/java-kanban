package service;

import model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private static ArrayList<Task> listOfRecalledTasks = new ArrayList<>();

    @Override
    public void add(Task task) {
    }

    @Override
    public ArrayList<Task> getHistory() {
        return listOfRecalledTasks;
    }

    static void updateListOfRecalledTasks(Task task) {
        listOfRecalledTasks.add(task);
        if (listOfRecalledTasks.size() > 10) {
            listOfRecalledTasks.remove(0);
        }
    }
}
