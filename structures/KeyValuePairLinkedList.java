package uk.ac.warwick.cs126.structures;
import uk.ac.warwick.cs126.util.myCompare;

import java.util.Comparator;

public class KeyValuePairLinkedList<K extends Comparable<K>,V> {

    protected ListElement<KeyValuePair<K,V>> head;
    protected int size;
    public int counter;
    public ListElement<KeyValuePair<K,V>> temp;
    public ListElement<KeyValuePair<K,V>> temp2= new ListElement<>(null);
    public KeyValuePairLinkedList() {
        head=new ListElement<>(null);
        size = 0;
    }

    public void add(K key, V value) {
        this.add(new KeyValuePair<K,V>(key,value));
    }

    public void add(KeyValuePair<K,V> kvp) {
        ListElement<KeyValuePair<K,V>> new_element = new ListElement<>(kvp);
        new_element.setNext(head);
        head = new_element;
        size++;
    }

    public int size() {
        return size;
    }

    public ListElement<KeyValuePair<K,V>> getHead() {
        return head;
    }

    public KeyValuePair<K,V> get(K key) {
        counter = 0;
        temp = head;
        try {
            while (counter < size) {
                counter++;
//                temp.getValue()!=null && temp.getValue().getKey()!=null &&
                if (temp.getValue().getKey().compareTo(key)==0){
                    return temp.getValue();
                }
                temp = temp.getNext();
            }
        }
        catch (Exception e) {
           e.printStackTrace();
        }
        return null;
}
}
