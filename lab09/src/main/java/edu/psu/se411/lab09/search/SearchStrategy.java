package edu.psu.se411.lab09.search;

@FunctionalInterface
public interface SearchStrategy<T> {
    boolean matches(T item);
}
