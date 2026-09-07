package edu.psu.se411.lab02;

@FunctionalInterface
public interface Transformer<T, R> {

    R transform(T input);
}