# Lab 14 verification

Verified on MSI, Windows, 2026-11-02, with Java 23.0.1 and Maven 3.9.16.
Spring Boot 4.1.2, matching the supplied starter.

## Automated verification

`mvn -B -ntp clean package`: BUILD SUCCESS.
21 tests passed; 0 failures, 0 errors, 0 skipped.

The full Spring context and real H2 database were used. Checks covered all CRUD
operations, 201 Location headers, empty 204 responses, duplicate IDs returning 422,
missing records returning 404, mismatched update IDs, invalid JSON, negative IDs,
blank codes, short names, missing IDs/credits, and credit boundaries 1 and 10.
Invalid requests did not mutate the stored records.

## Packaged server HTTP check

Started `java -jar target/lab14-1.0-SNAPSHOT.jar` on 127.0.0.1:3000.

| Request | Observed result |
| --- | --- |
| GET /api/v1/courses | 200 with seed courses |
| POST /api/v1/courses with ID 100 | 201; Location: /api/v1/courses/100 |
| GET /api/v1/courses/100 | 200; correct submitted fields |
| PUT /api/v1/courses/100 | 204; zero-length body |
| GET after update | Updated course name and credits 4 persisted |
| DELETE /api/v1/courses/100 | 204; zero-length body |
| GET after delete | 404 |

The initial detached verification process was not reachable for the first HTTP
attempt. Running the packaged server in a persistent terminal resolved the issue;
the complete live lifecycle above then passed.

## Scope and repository hygiene

The required validation dependency and annotations are present. Validation was
implemented with Codex; no separate Copilot session is claimed.
Source, tests, SQL, configuration, request examples, and documentation are tracked.
Generated target output and build/server logs remain local and are ignored.
The H2 database is temporary and resets when the server restarts.
