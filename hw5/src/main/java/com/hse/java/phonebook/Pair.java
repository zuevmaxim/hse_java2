package com.hse.java.phonebook;

/**
 * Immutable pair of two elements.
 * @param <F> first element type
 * @param <S> second element type
 */
class Pair<F, S> {
    /** First pair element. */
    private final F first;
    /** Second pair element. */
    private final S second;

    /** Pair constructor. */
    Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    F getFirst() {
        return first;
    }

    S getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pair) {
            var other = (Pair) obj;
            return first.equals(other.first) && second.equals(other.second);
        }
        return false;
    }
}
