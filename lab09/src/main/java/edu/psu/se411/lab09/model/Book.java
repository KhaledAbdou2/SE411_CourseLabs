package edu.psu.se411.lab09.model;

public class Book extends Item {
    private final String authorName;

    public Book(String id, String name, String authorName) {
        super(id, name);
        this.authorName = requireText(authorName, "author");
    }

    public String getAuthorName() { return authorName; }

    @Override
    public String toString() {
        return "Book[id=" + getId() + ", name=" + getName()
                + ", author=" + authorName + "]";
    }
}
