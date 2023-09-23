package uk.ac.warwick.cs126.structures;
/**
 * An interface for a generic queue
 */
public interface IQueue<E> {

    // Add an element to the (end of) queue.
    public void enqueue(E element);

    // Remove and return element from the (start of) queue.
    public E dequeue();

    // Returns true when the queue has no elements, false otherwise.
    public boolean isEmpty();

}
