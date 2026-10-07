package edu.psu.se411.lab09.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import edu.psu.se411.lab09.search.SearchStrategy;

public class Inventory<T> {
    private final List<T> items = new ArrayList<>();

    public void addItem(T item) {
        items.add(Objects.requireNonNull(item, "Inventory item is required."));
    }

    /** Removes the first matching object, using equals, if present. */
    public boolean removeItem(T item) {
        return items.remove(Objects.requireNonNull(item, "Inventory item is required."));
    }

    /** Immutable snapshot: callers cannot mutate this inventory through the list. */
    public List<T> getAllItems() {
        return List.copyOf(items);
    }

    /** A strategy for a supertype can safely inspect every T in this inventory. */
    public List<T> findItems(SearchStrategy<? super T> strategy) {
        Objects.requireNonNull(strategy, "Search strategy is required.");
        List<T> matches = new ArrayList<>();
        for (T item : items) {
            if (strategy.matches(item)) {
                matches.add(item);
            }
        }
        return List.copyOf(matches);
    }
}
