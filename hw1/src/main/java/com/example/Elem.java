package com.example;

public class Elem {
    private String key;
    private String value;

    /**
     * Set key equal to k
     * @param k new key
     */
    public void setKey(String k) {
        key = k;
    }

    /**
     * @return key
     */
    public String getKey() {
        return key;
    }

    /**
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * Set value equal to v
     * @param v new value
     */
    public void setValue(String v) {
        value = v;
    }

    /**
     * Construct element
     * @param k key
     * @param v value
     */
    public Elem(String k, String v) {
        key = k;
        value = v;
    }

    /**
     * Compare two elements
     * @param elem other element to compare with
     * @return true iff element are equal
     */
    public boolean eq(Elem elem) {
        return elem.key.equals(key) && elem.value.equals(value);
    }
}
