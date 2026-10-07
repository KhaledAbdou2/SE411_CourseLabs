package edu.psu.se411.lab09.model;

public class ElectronicDevice extends Item {
    private final String category;

    public ElectronicDevice(String id, String name, String category) {
        super(id, name);
        this.category = requireText(category, "category");
    }

    public String getCategory() { return category; }

    @Override
    public String toString() {
        return "ElectronicDevice[id=" + getId() + ", name=" + getName()
                + ", category=" + category + "]";
    }
}
