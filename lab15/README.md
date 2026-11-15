# Lab 15 - MySQL CRUD

Java 21+, Maven 3.9.x. Source is organized under edu.psu.se411.lab15.
Spring Boot 3.5.7 matches the Lab 16 starter; modern managed MySQL Connector/J
coordinates replace the handout's old mysql-connector-java coordinates.

## MySQL setup

Create a free Aiven MySQL service and a dedicated database se411_lab15.
Copy config/application-local.properties.example to config/application-local.properties
and supply your host, port, username, and password there. This local file is ignored.
Alternatively set DB_URL, DB_USERNAME, DB_PASSWORD in the terminal environment.
Use jdbc:mysql://HOST:PORT/DATABASE?sslMode=REQUIRED (no password in the URL).

First run: `mvn spring-boot:run -Dspring-boot.run.profiles=init`.
Later runs: `mvn spring-boot:run`.
Initialization creates the table and inserts a sample only if it is empty.
It deliberately preserves existing data rather than dropping the table on startup.
Default port: 3015. Server binds to localhost.

## CRUD

Base path: /api/v1/products.
GET collection and GET /ID return 200; missing ID returns 404.
POST accepts a null/omitted ID and generates it, returning 201 with Location.
PUT /ID updates an existing resource and returns 204; DELETE /ID returns 204.
Invalid bodies and nonpositive path IDs return 400. Existing supplied POST ID returns 422.
Prices use BigDecimal and DECIMAL(8,2). Course credits are constrained to 1 through 10.

## Tests and verification

Run `mvn clean package`. Tests explicitly select an isolated H2 MySQL-mode test
profile. H2 is a test-only dependency and is not used by the normal application.
These checks verify application behavior, not a real MySQL/Aiven connection.
See VERIFICATION.md for actual results and remaining external setup.
Build output, logs, local credentials, and editor settings are not committed.
