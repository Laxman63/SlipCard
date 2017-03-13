package com.silpe.vire.slip.models;

/**
 * The persistent interface allows objects to be stored in {@code SharedPrefreences}
 * or {@code Preference}. All objects that implement {@code Persistent} must also implement
 * {@code equals} and {@code hashCode}. The contract is
 * <p>
 * {@code t.encode() == s.encode()} if and only if {@code t.equals(s)}.
 */
public interface Persistent<T> {

    boolean equals(Object o);

    int hashCode();

    String encode();

    T decode(String serial);

}
