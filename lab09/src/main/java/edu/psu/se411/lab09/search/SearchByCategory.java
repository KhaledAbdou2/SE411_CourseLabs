package edu.psu.se411.lab09.search;

import java.util.Objects;
import edu.psu.se411.lab09.model.ElectronicDevice;

/** Exact, case-sensitive matching. */
public final class SearchByCategory implements SearchStrategy<ElectronicDevice> {
    private final String category;

    public SearchByCategory(String category) {
        this.category = Objects.requireNonNull(category, "Search value is required.");
    }

    @Override
    public boolean matches(ElectronicDevice item) {
        return category.equals(item.getCategory());
    }
}
