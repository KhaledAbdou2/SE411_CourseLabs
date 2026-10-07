package edu.psu.se411.lab09;

import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import edu.psu.se411.lab09.model.*;
import edu.psu.se411.lab09.search.*;

class InventoryTest {
    private Book book() { return new Book("B-01", "Concurrency in Java", "Alice Green"); }
    private ElectronicDevice device() { return new ElectronicDevice("E-01", "Laptop", "Computers"); }

    @Test
    void inventorySupportsAnyTypeAndRemoval() {
        Inventory<String> inventory = new Inventory<>();
        inventory.addItem("one"); inventory.addItem("two"); inventory.addItem("one");
        assertTrue(inventory.removeItem("one"));
        assertEquals(List.of("two", "one"), inventory.getAllItems());
        assertFalse(inventory.removeItem("missing"));
    }

    @Test
    void allItemsIsAnImmutableSnapshot() {
        Inventory<Item> inventory = new Inventory<>();
        Book book = book(); inventory.addItem(book);
        List<Item> snapshot = inventory.getAllItems();
        assertThrows(UnsupportedOperationException.class, snapshot::clear);
        inventory.addItem(device());
        assertEquals(List.of(book), snapshot);
        assertEquals(2, inventory.getAllItems().size());
    }

    @Test
    void mixedNameAndIdSearchesReturnTheRightProduct() {
        Inventory<Item> inventory = new Inventory<>();
        Book book = book(); ElectronicDevice device = device();
        inventory.addItem(book); inventory.addItem(device);
        assertEquals(List.of(book), inventory.findItems(new SearchByName("Concurrency in Java")));
        assertEquals(List.of(device), inventory.findItems(new SearchByName("Laptop")));
        assertEquals(List.of(device), inventory.findItems(new SearchById("E-01")));
        inventory.removeItem(device);
        assertTrue(inventory.findItems(new SearchById("E-01")).isEmpty());
    }

    @Test
    void authorStrategyWorksOnBooks() {
        Inventory<Book> books = new Inventory<>();
        Book first = book();
        Book second = new Book("B-02", "Other Book", "Other Author");
        books.addItem(first); books.addItem(second);
        assertEquals(List.of(first), books.findItems(new SearchByAuthor("Alice Green")));
    }

    @Test
    void categoryStrategyWorksOnDevices() {
        Inventory<ElectronicDevice> devices = new Inventory<>();
        ElectronicDevice first = device();
        devices.addItem(first);
        devices.addItem(new ElectronicDevice("E-02", "Headphones", "Audio"));
        assertEquals(List.of(first), devices.findItems(new SearchByCategory("Computers")));
    }

    @Test
    void itemStrategiesWorkOnSubtypeInventories() {
        Inventory<Book> books = new Inventory<>();
        Book first = book(); books.addItem(first);
        SearchStrategy<Item> strategy = new SearchByName("Concurrency in Java");
        assertEquals(List.of(first), books.findItems(strategy));
        assertEquals(List.of(first), books.findItems(new SearchById("B-01")));
    }

    @Test
    void allMatchesPreserveInsertionOrderAndResultsAreSnapshots() {
        Inventory<Item> inventory = new Inventory<>();
        Book first = book();
        Book second = new Book("B-02", "Concurrency in Java", "Another Author");
        inventory.addItem(first); inventory.addItem(device()); inventory.addItem(second);
        List<Item> found = inventory.findItems(new SearchByName("Concurrency in Java"));
        assertEquals(List.of(first, second), found);
        assertThrows(UnsupportedOperationException.class, found::clear);
        inventory.removeItem(first);
        assertEquals(List.of(first, second), found);
    }

    @Test
    void absentAndCaseDifferentQueriesReturnEmptyAndLambdasWork() {
        Inventory<Item> inventory = new Inventory<>();
        assertTrue(inventory.findItems(new SearchById("missing")).isEmpty());
        Book book = book(); inventory.addItem(book);
        assertTrue(inventory.findItems(new SearchByName("concurrency in java")).isEmpty());
        assertEquals(List.of(book), inventory.findItems(item -> item.getId().startsWith("B-")));
    }

    @Test
    void nullsAndMissingProductDetailsAreRejected() {
        Inventory<Item> inventory = new Inventory<>();
        assertThrows(NullPointerException.class, () -> inventory.addItem(null));
        assertThrows(NullPointerException.class, () -> inventory.removeItem(null));
        assertThrows(NullPointerException.class, () -> inventory.findItems(null));
        assertThrows(NullPointerException.class, () -> new SearchByName(null));
        assertThrows(NullPointerException.class, () -> new SearchById(null));
        assertThrows(NullPointerException.class, () -> new SearchByAuthor(null));
        assertThrows(NullPointerException.class, () -> new SearchByCategory(null));
        assertThrows(IllegalArgumentException.class, () -> new Book(" ", "Name", "Author"));
        assertThrows(IllegalArgumentException.class, () -> new Book("B", null, "Author"));
        assertThrows(IllegalArgumentException.class, () -> new Book("B", "Name", ""));
        assertThrows(IllegalArgumentException.class, () -> new ElectronicDevice("E", "Name", null));
        assertTrue(inventory.getAllItems().isEmpty());
    }

    @Test
    void wildcardDisplayAcceptsMixedAndUnrelatedInventories() {
        Inventory<Item> mixed = new Inventory<>();
        mixed.addItem(book()); mixed.addItem(device());
        Inventory<String> strings = new Inventory<>(); strings.addItem("Generic item");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream original = System.out;
        try (PrintStream capture = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            System.setOut(capture);
            App.displayInventory(mixed);
            App.displayInventory(strings);
            App.displayInventory(new Inventory<Book>());
        } finally {
            System.setOut(original);
        }
        String output = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("author=Alice Green"));
        assertTrue(output.contains("category=Computers"));
        assertTrue(output.contains("Generic item"));
    }
}
