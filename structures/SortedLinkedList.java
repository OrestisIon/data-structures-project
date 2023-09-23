package uk.ac.warwick.cs126.structures;
import java.util.Comparator;

import static java.lang.Math.*;

public class SortedLinkedList<E> {
        public ListElement<E> head;//protected
        public ListElement<E> tail;//protected
        public Comparator<E> sorter; //can only be assigned once
        protected int size;
        public int counter;
        public int position;
        public ListElement<E> positionafter;
        public int midpos;
        public   ListElement<E> middle;
        public ListElement<E> temp;
        public SortedLinkedList( Comparator<E> sorter) {
            head = null;
            size = 0;
            this.sorter= sorter;
        }
        public void add(E element){
            ListElement<E> new_element=new ListElement<>(element);
            this.add(new_element);
        }
        public void add(ListElement<E> new_element) {
                if (size() > 0) {
                    position = binarySearch(0, size-1, new_element);
                    if(position<0)
                        addToHead(new_element);
                    else if(position>=size)
                        addToTail(new_element);
                    else {
                        new_element.setNext(getNode(position+1));
                        getNode(position).setNext(new_element);
                    }
                }
                else{
                    head=new_element;
                    head.setNext(null);
                }
                size++;

        }
        public void delete(E element){

        }

        public int size() {
            return size;
        }

        public ListElement<E> getHead() {
            return head.getNext();
        }

        public ListElement<E> getNode(int index) {
            temp = head;
            for(int i=1; i<index;i++) //move to the next element until the index is reached
                temp=temp.getNext();
            return temp;
        }
        public E get(int index){
/*            int comp=size-index; //calculating the difference from the end to find which root to take(from riht or from left)
            if(comp<index) {
                temp = tail;
                for (int i = size - 1; i >= 0; i--) { //From the tail go backwards to find node
                    if (i == index)
                        return temp.getValue();
                    temp = temp.getPrev();
                }
            }
          else{
 */         if(index<0 || index>size)
                return null; //if the index is out of bound return null
            temp = head;
                for (int i = 0; i < size; i++) { //From the head move forward to find node
                    if (i == index)
                        return temp.getValue();
                    temp = temp.getNext();
                }
//            }
            return null;
        }

    public  ListElement<E> getNode2(int index){
        int comp=size-index; //calculating the difference from the end to find which root to take(from riht or from left)
        if(comp<index) {
            temp = tail;
            for (int i = size - 1; i >= 0; i--) { //From the tail go backwards to find node
                if (i == index)
                    return temp;
                temp = temp.getPrev();
            }
        }
        else{
            temp = head;
            for (int i = 0; i < size; i++) { //From the head move forward to find node
                if (i == index)
                    return temp;
                temp = temp.getNext();
            }
        }
        return null; //if the index is out of bound return null
    }
    public int getIndexOf(ListElement<E> element){
            counter=0;
            temp = head;
            while(counter<size() && !element.getValue().equals(temp.getValue())){ //search every element of the list while the element is found
                    temp=temp.getNext();
                    counter++;
            }
            if( element.getValue().equals(temp.getValue())) //if the element was found
                return counter; //return its position
            return -1; //else return -1
        }


    // function to insert a node at the beginning
    // of the Singly Linked List
        public int binarySearch(int pos1, int pos2, ListElement<E> new_element) {
                // Find Middle
                //if the element is lager than the tail
                if(pos1>size)
                    return pos1;
                if( pos1>pos2 )//REMAINDER
                    return pos2;
                midpos=(pos1+(pos2-pos1)/2);
                middle=getNode(midpos);
              //  System.out.println("Middle index is: "+midindex+" and middle is now: "+ middle.getValue());
                // If value is present at middle
                if ( sorter.compare(new_element.getValue(), middle.getValue())==0)
                    return midpos;
                    // If value is less than mid
                else if (sorter.compare(new_element.getValue(), middle.getValue())<0)
                {
                    return binarySearch(pos1,midpos-1,new_element);
                }
                // If the value is more than mid.
                else
                    return binarySearch(midpos+1,pos2,new_element);
        }

    public void addToHead(ListElement<E> e) {
            e.setNext(head);
            head=e;
    }

    public void addToTail(ListElement<E> e) {
      getNode(size-1).setNext(e);
      e.setNext(null);
    }

    public E removeFromHead() {
        if (isEmpty()) {
            return null;
        }

        ListElement<E> e = head;

        head = head.getNext();

        if (isEmpty()) {
            tail = null;
        } else {
            head.setPrev(null); // the first element has no predecessors
        }

        return e.getValue();
    }

    public E removeFromTail() {
        if (isEmpty()) {
            return null;
        }

        ListElement<E> e = tail;

        tail = tail.getPrev();

        if (isEmpty()) {
            head = null;
        } else {
            tail.setNext(null); // the last element has no successors
        }

        return e.getValue();
    }

    public boolean isEmpty() {
        return (head == null) || (tail == null);
    }
}
