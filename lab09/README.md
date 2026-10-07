# Lab 09 - Managing an Inventory with the Strategy Pattern

Requires JDK 23 and Maven 3.9.x. Run from this folder:

```cmd
mvn clean package exec:java
```

## Part 1: Generic inventory
`Inventory<T>` supports `addItem`, `removeItem`, and `getAllItems`.
It can store any non-null T, not only products.
The demo creates `Inventory<Item>` containing Book and ElectronicDevice objects.
Item stores the shared ID and name; the subclasses add author name and category.
Removal uses equals and removes the first matching entry. Product objects use
identity equality; remove the stored instance. Duplicate entries are allowed.
Returned lists are immutable snapshots and cannot change the inventory.

## Part 2: Search strategies
`SearchStrategy<T>` declares `boolean matches(T item)`.
`findItems(SearchStrategy<? super T>)` returns every matching item in insertion order.
The plural method name follows the handout's example and list-valued result.
The bounded wildcard also lets an Item strategy search a Book inventory.

- SearchById and SearchByName operate on Item and either product subtype.
- SearchByAuthor operates on Book.
- SearchByCategory operates on ElectronicDevice.

The mixed inventory is searched by book name, device name, and ID.
Typed inventories demonstrate author and category strategies without unsafe casts.
A Book-specific strategy intentionally cannot be passed to an Inventory<Item>,
because not every Item is a Book. Queries use exact, case-sensitive matching.
Sample book titles and author names are illustrative data.

## Part 3: Wildcard display
`App.displayInventory(Inventory<?> inventory)` prints each object's details
without needing to know its element type. The demo displays both mixed and
book-only inventories, including the mixed inventory after removing an item.

## Starter adaptation and logging
The supplied starter targets Java 24; this version targets the installed JDK 23.
Its artifact and package are aligned with the repository as `lab09` and
`edu.psu.se411.lab09`. The supplied exec plugin version 3.5.0 is retained.
File logging records application start/close and unexpected failures with traces
in `logs/log.out`. Every run replaces that file. Generated logs, target output,
and Eclipse-specific starter files are not tracked.
Maven's expected slf4j-log4j12 relocation notice is the same as in previous labs.

## Tests
Run `mvn test`. Ten tests cover mixed and typed inventories, all four strategies,
base-type strategies on subtype inventories, removal, multiple matches, immutable
snapshots, no matches, null validation, lambda strategies, and wildcard display.
