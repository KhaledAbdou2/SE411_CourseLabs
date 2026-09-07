package edu.psu.se411.lab02;

import java.util.Arrays;
import java.util.List;

public class PrintableList<T> {

    private final List<T> items;

    public PrintableList(T[] items) {
        this.items = Arrays.asList(items);
    }

    public void printItems() {
        for (T item : items) {
            System.out.println(item);
        }
    }
}