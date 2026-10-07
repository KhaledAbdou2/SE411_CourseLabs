package edu.psu.se411.lab09.search;

import java.util.Objects;
import edu.psu.se411.lab09.model.Item;

/** Exact, case-sensitive matching. */
public final class SearchById implements SearchStrategy<Item> {
    private final String id;

    public SearchById(String id) {
        this.id = Objects.requireNonNull(id, "Search value is required.");
    }

    @Override
    public boolean matches(Item item) {
        return id.equals(item.getId());
    }
}
