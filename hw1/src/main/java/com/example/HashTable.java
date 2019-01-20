package com.example;

public class HashTable {
    private int size = 0;
    private int capacity = 37;
    private List[] baskets;

    /**
     * Construct empty hash table
     */
    public HashTable() {
        baskets = new List[capacity];
        for (int i = 0; i < capacity; i++) {
            baskets[i] = new List();
        }
    }

    /**
     * Construct hash table of fixed capacity
     * @param capacity
     */
    private HashTable(int capacity) {
        this.capacity = capacity;
        baskets = new List[capacity];
        for (int i = 0; i < capacity; i++) {
            baskets[i] = new List();
        }
    }

    /**
     * Number of elements that hash table contains
     * @return hash table size
     */
    public int size() {
        return size;
    }

    /**
     * Calculate non negative (a mod b)
     * @param a
     * @param b
     * @return (a % b) >= 0
     */
    private int mod(int a, int b) {
        return ((a % b) + b) % b;
    }

    /**
     * Find a basket for an element
     * @param key
     * @return number from 0 to (capacity - 1)
     */
    private int getHash(String key) {
        return mod(key.hashCode(), capacity);
    }

    /**
     * Check if element with such key is included in hash table
     * @param key
     * @return true iff hash table includes element with such key
     */
    public boolean contains(String key) {
        return baskets[getHash(key)].find(key) != null;
    }

    /**
     * Find element by key
     * @param key
     * @return element with such key
     */
    private Elem getElem(String key) {
        return baskets[getHash(key)].find(key);
    }

    /**
     * Find value of element by key
     * @param key
     * @return value of element with such key
     */
    public String get(String key) {
        Elem e = getElem(key);
        if (e == null) {
            return null;
        }
        return e.getValue();
    }

    /**
     * Check if hash table should be resized and resize if needed
     */
    private void checkSize() {
        if (size < capacity) {
            return;
        }
        capacity *= 2;
        HashTable h = new HashTable(capacity);
        for (List l : baskets) {
            for (Elem elem : l.toArray()) {
                h.put(elem.getKey(), elem.getValue());
            }
        }
        baskets = h.baskets;
    }

    /**
     * Add an element to a hash table. If such a key has already been in a hash table, then value changes.
     * @param key key to add
     * @param value value to add
     * @return previous value if such key has already been in a hash table or null if not
     */
    public String put(String key, String value) {
        Elem e = getElem(key);
        if (e != null) {
            String val = e.getValue();
            e.setValue(value);
            return val;
        }

        checkSize();
        ++size;
        Elem elem = new Elem(key, value);
        baskets[getHash(key)].insert(elem);
        return null;
    }

    /**
     * Delete an element by key
     * @param key
     * @return previous value if such key was in a hash table, null otherwise
     */
    public String remove(String key) {
        Elem e = getElem(key);
        if (e == null) {
            return null;
        }
        --size;
        baskets[getHash(key)].remove(key);
        return e.getValue();
    }

    /**
     * Clear hash table, remove all elements
     */
    public void clear() {
        size = 0;
        for (int i = 0; i < capacity; i++) {
            baskets[i].clear();
        }
    }
}
