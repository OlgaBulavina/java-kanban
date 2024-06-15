package service;

import model.Node;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    //private LinkedList<Task> listOfRecalledTasks = new LinkedList<>();
    private HashMap<Integer, Node<Task>> savedTasksHistory = new HashMap<>();
    private Node firstNode;
    private Node lastNode;

    @Override
    public void add(Task task) { //TODO Если какую-либо задачу посещали несколько раз, то в истории должен остаться только её последний просмотр. Предыдущий должен быть удалён за O(1)
        if (savedTasksHistory.size() == 0) {
            Node node = new Node(task, null, null);
            firstNode = node;
            lastNode = node;
            savedTasksHistory.put(task.getUin(), node);
        } else if (savedTasksHistory.containsKey(task.getUin())) {
            final Node oldNode = savedTasksHistory.get(task.getUin());
            removeNode(oldNode);
            Node newNode = linkLast(task);
            savedTasksHistory.replace(task.getUin(), newNode);
        } else {
            Node node = linkLast(task);
            savedTasksHistory.put(task.getUin(), node);
        }
    }

    @Override
    public void remove(int uin) {
        //TODO дописать метод, добавить его вызов при удалении задач, чтоб при удалении таска удалялась и история просмотров
        if (savedTasksHistory.containsKey(uin)) {
            Node node = savedTasksHistory.get(uin);
            removeNode(node);
            savedTasksHistory.remove(uin);
        }
    }

    @Override
    public ArrayList<Task> getHistory() { //TODO должна перекладывать задачи из связного списка в ArrayList для формирования ответа.
        ArrayList<Task> listOfRecalledTasks = new ArrayList<>();
        getTasks(listOfRecalledTasks);
        return listOfRecalledTasks;
    }


    private Node linkLast(Task task) {
        //TODO будет добавлять задачу в конец этого списка
        final Node oldLastNode = lastNode;
        Node node;
        if (oldLastNode == null) {
            node = new Node(task, null, null);
            lastNode = node;
            firstNode = node;
        } else {
            node = new Node(task, oldLastNode, null);
            oldLastNode.setNext(node);
            lastNode = node;
        }
        return node;
    }

    private void getTasks(ArrayList nodesList) {
        //TODO собирать все задачи из созданного LinkedList в обычный ArrayList
        Node node = firstNode;
        if (node == null) return;
        else {
            while (node != null) {
                nodesList.add(node.getItem());
                node = node.getNext();
            }
        }
    }

    private void removeNode(Node node) {
        Node previous = node.getPrevious();
        Node next = node.getNext();
        if (previous == null && next != null) {
            next.setPrevious(null);
            firstNode = node.getNext();
        } else if (previous != null && next == null) {
            previous.setNext(null);
            lastNode = node.getPrevious();
        } else if (previous == null && next == null) {
            firstNode = null;
            lastNode = null;
        } else {
            previous.setNext(next);
            next.setPrevious(previous);
        }
        node = null;
    }
}
