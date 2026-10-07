package edu.psu.se411.lab09.model;

public abstract class Item {
    private final String id;
    private final String name;

    protected Item(String id, String name) {
        this.id = requireText(id, "ID");
        this.name = requireText(name, "Name");
    }

    public String getId() { return id; }
    public String getName() { return name; }

    protected static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required.");
        }
        return value.trim();
    }
}
