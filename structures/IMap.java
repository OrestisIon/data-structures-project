package uk.ac.warwick.cs126.structures;

public interface IMap<K,V> {

    // Adds a mapping from key to value to the map
    public void add(K key, V value);

    // Finds the respective value that is mapped to from key
    public V get(K key);

}
