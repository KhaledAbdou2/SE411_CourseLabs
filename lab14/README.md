# Lab 14 - Spring CRUD

Complete course CRUD API, based on the supplied Part 1 project.

## Run

From `SE411_CourseLabs\lab14`:

```cmd
mvn clean package
mvn spring-boot:run
```

Use Java 21 or later (the laptop uses JDK 23). The server listens at
http://localhost:3000, preserving the starter's port.
Lab 13 uses 8080, so both can run together. Stop a server with Ctrl+C.
H2 is in memory: course changes reset when the server restarts.

## API contract

| Operation | Endpoint | Success | Errors |
| --- | --- | --- | --- |
| Create | POST /api/v1/courses | 201 with Location and created JSON | 400 invalid body; 422 duplicate ID |
| List | GET /api/v1/courses | 200 with JSON array | |
| Find | GET /api/v1/courses/{id} | 200 with JSON | 400 invalid ID; 404 missing |
| Update | PUT /api/v1/courses/{id} | 204, empty body | 400 invalid body/ID mismatch; 404 missing |
| Delete | DELETE /api/v1/courses/{id} | 204, empty body | 400 invalid ID; 404 missing |

POST and PUT require a client-supplied numeric ID. Zero is accepted because the
handout says the ID cannot be negative. A PUT body's ID must match the URL.
The existing seed courses have IDs 1 and 2.

Example POST body:

```json
{
  "id": 100,
  "code": "SE411",
  "name": "Software Construction",
  "description": "Spring CRUD lab",
  "credits": 3
}
```

Use requests.http in VS Code, Postman, or curl. Run the requests in sequence to
create, retrieve, update, and delete course 100.

## Validation and persistence

The required `spring-boot-starter-validation` dependency provides Jakarta constraints:

- ID: required and nonnegative.
- Code: required, nonblank, at most 100 characters.
- Name: required, nonblank, 2 to 100 characters.
- Description: optional, at most 100 characters.
- Credits: required, 1 through 10 inclusive.

Both POST and PUT use `@Valid`. Invalid JSON and path values return 400.
The service uses constructor injection and transactions. Repository interfaces
retain CRUD and paging/sorting support. Explicit `JdbcAggregateTemplate.insert`
creates client-assigned IDs; repository save would treat an existing nonnull ID
as an update. A duplicate POST never overwrites the existing course.
A database exception handler also maps duplicate-key races to 422.

## Starter adaptations

The starter's package, model, controller/service/repository layout, sample courses,
and port 3000 are retained. Source targets Java 21 as in the starter and runs on
the laptop's JDK 23. The starter uses Spring Boot 4.1.2, while the PDF mentions
4.0.5; this project retains the supplied starter's 4.1.2 version.
Course IDs use SQL BIGINT to match Java Long.

The validation implementation was added with Codex and verified by automated API
tests. The handout names Copilot; this work does not claim a separate Copilot run.

## Verify

`mvn test` runs full-context MockMvc tests against the actual H2 database.
Tests cover every success response, Location headers, empty 204 bodies,
duplicates, missing records, malformed requests, validation boundaries, and
protection against invalid updates. Each test rolls back its database changes.
See [VERIFICATION.md](VERIFICATION.md) for actual results.

Generated target output and logs are ignored by Git.

References:
- [Spring Data JDBC entity persistence](https://docs.spring.io/spring-data/relational/reference/jdbc/entity-persistence.html)
- [Spring Boot testing](https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html)
