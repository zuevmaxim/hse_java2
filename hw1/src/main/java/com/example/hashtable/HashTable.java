package com.example.hashtable;

/**
 * Hash table, which maps String to String
 */
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
     * @return (a % b) >= 0
     */
    private int mod(int a, int b) {
        return ((a % b) + b) % b;
    }

    /**
     * Find a basket for an element with key
     * @throws IllegalArgumentException if key is null
     * @return number from 0 to (capacity - 1)
     */
    private int getHash(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("Key should not be null.");
        }
        return mod(key.hashCode(), capacity);
    }

    /**
     * Check if element with such key is included in hash table
     * @throws IllegalArgumentException if key is null
     * @return true iff hash table includes element with such key
     */
    public boolean contains(String key) throws IllegalArgumentException {
        return baskets[getHash(key)].find(key) != null;
    }

    /**
     * Find element by key
     * @throws IllegalArgumentException if key is null
     * @return element with such key
     */
    private Element getElement(String key) throws IllegalArgumentException {
        return baskets[getHash(key)].find(key);
    }

    /**
     * Find value of element by key
     * @throws IllegalArgumentException if key is null
     * @return value of element with such key
     */
    public String get(String key) throws IllegalArgumentException {
        Element element = getElement(key);
        if (element == null) {
            return null;
        }
        return element.getValue();
    }

    /**
     * Check if hash table should be resized and resize if needed
     */
    private void checkSize() {
        if (size < capacity) {
            return;
        }
        capacity *= 2;
        var newHashTable = new HashTable(capacity);
        for (List list : baskets) {
            for (Element element : list.toArray()) {
                newHashTable.put(element.getKey(), element.getValue());
            }
        }
        baskets = newHashTable.baskets;
    }

    /**
     * Add an element to a hash table. If such a key has already been in a hash table, then value changes.
     * @param key key to add
     * @param value value to add
     * @throws IllegalArgumentException if key or value is null
     * @return previous value if such key has already been in a hash table or null if not
     */
    public String put(String key, String value) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("Key should not be null.");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value should not be null.");
        }
        Element element = getElement(key);
        if (element != null) {
            String prevValue = element.getValue();
            element.setValue(value);
            return prevValue;
        }

        checkSize();
        ++size;
        var newElement = new Element(key, value);
        baskets[getHash(key)].insert(newElement);
        return null;
    }

    /**
     * Delete an element by key
     * @throws IllegalArgumentException if key is null
     * @return previous value if such key was in a hash table, null otherwise
     */
    public String remove(String key) throws IllegalArgumentException {
        Element element = getElement(key);
        if (element == null) {
            return null;
        }
        --size;
        baskets[getHash(key)].remove(key);
        return element.getValue();
    }

    /**
     * Clear hash table, remove all elements
     */
    public void clear() {
        var newHashTable = new HashTable();
        baskets = newHashTable.baskets;
        capacity = newHashTable.capacity;
        size = newHashTable.size;
    }
}
