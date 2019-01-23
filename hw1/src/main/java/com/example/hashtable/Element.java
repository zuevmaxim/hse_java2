package com.example.hashtable;

/**
 * Element that hash table contains, namely pairs of String (key, value)
 */
public class Element {
    private String key;
    private String value;

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Construct element (key, value)
     */
    public Element(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Compare two elements
     * @param obj other element to compare with
     * @return true iff elements are equal, namely have equal keys and values
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Element other = (Element)obj;
        return other.key.equals(key) && other.value.equals(value);
    }
}
