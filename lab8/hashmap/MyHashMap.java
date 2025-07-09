package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author syx
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    @Override
    public void clear() {
        size = 0;
        buckets = createTable(this.initialSize);
        this.keySet = new HashSet<>();
    }

    @Override
    public boolean containsKey(K key) {
        return keySet.contains(key);
    }

    @Override
    public V get(K key) {
        if (!containsKey(key)) {
            return null;
        } else {
            int hashCode = Math.floorMod(key.hashCode(), buckets.length);
            Iterator<Node> iterator = buckets[hashCode].iterator();
            while (iterator.hasNext()) {
                Node next = iterator.next();
                if (key.equals(next.key)) {
                    return next.value;
                }
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        int hashCode = Math.floorMod(key.hashCode(), buckets.length);
        if (containsKey(key)) {
            Iterator<Node> iterator = buckets[hashCode].iterator();
            while (iterator.hasNext()) {
                Node next = iterator.next();
                if (next.key.equals(key)) {
                    next.value = value;
                    return;
                }
            }
        } else {
            if ((double)size / buckets.length > maxLoad) {
                grow();
            }
            keySet.add(key);
            size++;
            buckets[Math.floorMod(key.hashCode(), buckets.length)].add(createNode(key, value));
        }
    }
    private void grow() {
        int originalLength = buckets.length;
        Collection<Node>[] newBuckets = createTable(originalLength * 2);
        for (int i = 0; i < buckets.length; i++) {
            Iterator<Node> iterator = buckets[i].iterator();
            while (iterator.hasNext()) {
                Node next = iterator.next();
                newBuckets[Math.floorMod(next.hashCode(), newBuckets.length)].add(next);
            }
        }
        buckets = newBuckets;
    }

    @Override
    public Set<K> keySet() {
        return keySet;
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        return keySet.iterator();
    }

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int initialSize;
    private double maxLoad;
    private int size;
    private HashSet<K> keySet;
    /** Constructors */
    public MyHashMap() {
        this(16, 0.75);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, 0.75);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.initialSize = initialSize;
        this.maxLoad = maxLoad;
        this.size = 0;
        this.keySet = new HashSet<>();
        this.buckets = createTable(initialSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }
        return table;
    }


}
