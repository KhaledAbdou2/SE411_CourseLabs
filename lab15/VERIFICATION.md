# Lab 15 verification

2026-11-15, MSI laptop, Java 23.0.1, Maven 3.9.16.

`mvn -B -ntp clean package`: BUILD SUCCESS.
3 full-context API tests passed with the H2 MySQL-mode test profile.
They cover the create/read/update/delete lifecycle with generated IDs and Location,
invalid data without database mutation, listing, missing records, and invalid IDs.

The normal application uses MySQL Connector/J and requires external connection
settings. H2 is test-only and is not included as a runtime database.

**Pending:** Aiven service/database connection and five live CRUD operations on
actual MySQL. No Aiven credentials were supplied and no local MySQL service was
found. These tests do not establish MySQL compatibility or remote connectivity.
The local configuration example and setup instructions are ready for those details.
