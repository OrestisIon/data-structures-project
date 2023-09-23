package uk.ac.warwick.cs126.structures;


import java.util.Comparator;

public class MyHashMap<K extends Comparable<K>,V> implements IMap<K,V> {

    protected KeyValuePairLinkedList[] table;
    public int location;
    private KeyValuePair<K,V> ptr;
    public MyHashMap() {
        /* for very simple hashing, primes reduce collisions */
        this(19);
    }

    public MyHashMap(int size) {
        table = new KeyValuePairLinkedList[size];
        initTable();
    }

    // INCOMPLETE.
    public int find(K key) {
        //returns the number of comparisons required to find element using Linear Search.
        V value=get(key);

        return table[location].counter;
    }
    protected void initTable() {
        for(int i = 0; i < table.length; i++) {
            table[i] = new KeyValuePairLinkedList<>();
        }
    }

    protected int hash(K key) {
        int code;
        try {
             code= Math.abs(key.hashCode());
        }
        catch (NullPointerException e){
            System.out.println("Key is null, thus the hash is 0");
            return 0;
        }
        return code;
    }


    public void add(K key, V value) {
        if(key!=null) {
            int hash_code = hash(key);
            int location = hash_code % table.length;
            //System.out.println("Adding " + value + " under key " + key + " at location " + location);
            table[location].add(key, value);
        }
        else{
            System.out.println("key entered is null. Cannot add to HashMap");
        }
    }

    public V get(K key) {
        int hash_code = hash(key);
        if(hash_code==0) //if the Key is not in the Hashmap
            return null;
        location = hash_code % table.length;
        ptr = table[location].get(key);
        if (ptr != null)
            return ptr.getValue();
        return null;
    }
}
