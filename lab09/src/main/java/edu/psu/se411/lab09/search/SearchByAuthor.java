package edu.psu.se411.lab09.search;

import java.util.Objects;
import edu.psu.se411.lab09.model.Book;

/** Exact, case-sensitive matching. */
public final class SearchByAuthor implements SearchStrategy<Book> {
    private final String authorName;

    public SearchByAuthor(String authorName) {
        this.authorName = Objects.requireNonNull(authorName, "Search value is required.");
    }

    @Override
    public boolean matches(Book item) {
        return authorName.equals(item.getAuthorName());
    }
}
