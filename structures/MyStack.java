package uk.ac.warwick.cs126.structures;

public class MyStack<E> implements IStack<E> {
    private DoublyLinkedList<E> list;
    public MyStack(){
        list=new DoublyLinkedList<>();
    }
    // INCOMPLETE.
    public void push(E val) {
        //  TODO: implement pushing
        list.addToTail(val);
    }

    // INCOMPLETE.
    public E pop() {
        //  TODO: implement popping
        return list.removeFromTail();
    }

    // INCOMPLETE
    public boolean isEmpty() {
        //  TODO: check whether list is empty
        return list.isEmpty();
    }
}

