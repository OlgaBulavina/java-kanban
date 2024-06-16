package service;

import model.Task;

import java.util.Collection;

public interface HistoryManager {
    void add(Task task);

    void remove(int uin);

    Collection<Task> getHistory();
}
