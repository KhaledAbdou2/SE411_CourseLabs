# Lab 09 Verification

Verified on MSI on 2026-10-07 with Java 23.0.1 and Maven 3.9.16.

- `mvn -B -ntp clean package exec:java`: BUILD SUCCESS.
- Ten JUnit tests passed; zero failures, errors, or skipped tests.
- The mixed inventory displayed two books and two electronic devices.
- Name queries returned the expected book and laptop.
- The ID query returned the headphones.
- Author and category queries returned the expected typed products.
- An Item name strategy also searched a Book inventory successfully.
- Removing the headphones returned true and left three mixed-inventory items.
- The wildcard display printed both mixed and book-only inventories.
- The file log recorded application start and close.

Tests additionally verify generic non-product inventories, immutable snapshots,
all matches in insertion order, no matches, case sensitivity, lambdas,
invalid input, removal behavior, and wildcard display across unrelated types.

The local build output is in `build-verification.log`.
The runtime log is `logs/log.out`. Generated files are ignored by Git.
