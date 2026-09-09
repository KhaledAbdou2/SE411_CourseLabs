# SE411 Course Labs

Solutions and exercises for the SE411 Software Construction course.

## Labs

| Lab | Topic | Location |
| --- | --- | --- |
| 01 | Git and GitHub | Repository setup and commit history |
| 02 | Java generics | [lab02/src](lab02/src) |
| 03 | JUnit | [lab03](lab03) |
| 04 | Maven and JavaFX | [lab04](lab04/README.md) |
| 05 | Exception handling | [lab05](lab05/README.md) |

## Development

Use JDK 23 and Maven 3.9.x for the Maven labs.
Open this repository root in VS Code so Maven projects are discovered.
Each lab has its own folder; run Maven commands inside that lab.

- Lab 02: plain Java sources in `lab02/src`.
- Lab 03: run `mvn test` inside `lab03`.
- Lab 04: see its README for the JavaFX app and documentation site.
- Lab 05: run `mvn clean package exec:java` inside `lab05`.
  Implementation, tests, and the [Copilot review](lab05/COPILOT_REVIEW.md) are complete.

## Local files

Generated `target/`, `out/`, class files, logs, and local VS Code settings
are ignored by Git. Source files, Maven configuration, and instructions are tracked.
