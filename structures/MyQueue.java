package uk.ac.warwick.cs126.structures;

public class MyQueue<E> implements IQueue<E> {

    private ListElement<E> head;
    private ListElement<E> tail;

    public MyQueue() {
        head = null;
        tail = null;
    }

    public boolean isEmpty() {
        return (head == null);
    }

    public E dequeue() {
        if (isEmpty()) {
            return null;
        }

        ListElement<E> tmp = head;
        head = tmp.getNext();

        if (head == null) {
            tail = null;
        }

        return tmp.getValue();
    }

    public void enqueue(E value) {
        ListElement<E> tmp = new ListElement<>(value);

        if (isEmpty()) {
            tail = head = tmp;
        } else {
            tail.setNext(tmp);
            tail = tmp;
        }
    }
    public String toString() {
        // Returns a string representation of this queue.
        ListElement<E> ptr = head;
        System.out.print("[ ");
        if(!isEmpty()) {
            while (ptr != tail) {
                System.out.print(ptr.getValue());
                System.out.print(", ");
                ptr = ptr.getNext();
            }
            System.out.print(ptr.getValue());
        }
        System.out.println(" ]");
        return "";
    }
    public void modifyHead(E value){
        ListElement<E> temp = head;
        head = new ListElement<>(value);
        head.setNext(temp.getNext());
    }
}
