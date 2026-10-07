package edu.psu.se411.lab09.search;

import java.util.Objects;
import edu.psu.se411.lab09.model.Item;

/** Exact, case-sensitive matching. */
public final class SearchByName implements SearchStrategy<Item> {
    private final String name;

    public SearchByName(String name) {
        this.name = Objects.requireNonNull(name, "Search value is required.");
    }

    @Override
    public boolean matches(Item item) {
        return name.equals(item.getName());
    }
}
