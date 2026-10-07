package edu.psu.se411.lab09;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.psu.se411.lab09.model.*;
import edu.psu.se411.lab09.search.*;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        LOGGER.info("Application is starting...");
        try {
            // Illustrative inventory data, not a catalogue of real publications.
            Book concurrency = new Book("B-01", "Concurrency in Java", "Alice Green");
            Book practical = new Book("B-02", "Practical Java", "Bob Stone");
            ElectronicDevice laptop = new ElectronicDevice("E-01", "Laptop", "Computers");
            ElectronicDevice headphones = new ElectronicDevice("E-02", "Headphones", "Audio");

            Inventory<Item> inventory = new Inventory<>();
            inventory.addItem(concurrency);
            inventory.addItem(practical);
            inventory.addItem(laptop);
            inventory.addItem(headphones);

            System.out.println("Complete mixed inventory:");
            displayInventory(inventory);
            System.out.println("\nBook by name: "
                    + inventory.findItems(new SearchByName("Concurrency in Java")));
            System.out.println("Device by name: "
                    + inventory.findItems(new SearchByName("Laptop")));
            System.out.println("Item by ID: "
                    + inventory.findItems(new SearchById("E-02")));

            Inventory<Book> books = new Inventory<>();
            books.addItem(concurrency);
            books.addItem(practical);
            Inventory<ElectronicDevice> devices = new Inventory<>();
            devices.addItem(laptop);
            devices.addItem(headphones);

            System.out.println("Books by author: "
                    + books.findItems(new SearchByAuthor("Alice Green")));
            System.out.println("Devices by category: "
                    + devices.findItems(new SearchByCategory("Computers")));
            System.out.println("Base-type name strategy on books: "
                    + books.findItems(new SearchByName("Practical Java")));

            System.out.println("\nRemoved headphones: " + inventory.removeItem(headphones));
            System.out.println("Mixed inventory after removal:");
            displayInventory(inventory);
            System.out.println("\nBook-only inventory through the same wildcard method:");
            displayInventory(books);
        } catch (RuntimeException exception) {
            LOGGER.error("Inventory demonstration failed.", exception);
            throw exception;
        } finally {
            LOGGER.info("Application is closing...");
        }
    }

    public static void displayInventory(Inventory<?> inventory) {
        Objects.requireNonNull(inventory, "Inventory is required.");
        for (Object item : inventory.getAllItems()) {
            System.out.println(item);
        }
    }
}
