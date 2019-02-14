package com.hse.java.phonebook;

class Pair<F, S> {
    private final F first;
    private final S second;

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
            return first.equals(((Pair) obj).first)
                    && second.equals(((Pair) obj).second);
        }
        return false;
    }
}
