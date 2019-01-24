package com.java.trie;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Serializable {
    /**
     * Serialize an object to the output stream
     * @param out output stream
     * @throws IOException if an I/O error occurs
     */
    void serialize(OutputStream out) throws IOException;

    /**
     * Deserialize an object from the input stream
     * @param in input stream
     * @throws IOException if an I/O error occurs
     */
    void deserialize(InputStream in) throws IOException;
}