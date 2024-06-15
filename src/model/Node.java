package model;

public class Node<T extends Task> {
    T item;
    Node previous;
    Node next;

    public Node(T item, Node previous, Node next) {
        this.item = item;
        this.previous = previous;
        this.next = next;
    }

    public Node getPrevious() {
        return previous;
    }

    public void setPrevious(Node previous) {
        this.previous = previous;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public T getItem() {
        return item;
    }
}
