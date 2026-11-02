# Lab 13 - Spring Introduction

A layered product API using Spring MVC, Spring Data JDBC, H2, and Jakarta validation.

## Run

From `SE411_CourseLabs\lab13`:

```cmd
mvn clean package
mvn spring-boot:run
```

Requires Java 21 or later; verified with the laptop's JDK 23.
Open http://localhost:8080/api/v1/products or use requests.http in VS Code.
Stop the server with Ctrl+C. The H2 database is in memory and resets on restart.

## Endpoints

| Request | Result |
| --- | --- |
| GET /api/v1/products | 200 and all 17 supplied products |
| GET /api/v1/products/10 | 200 and one product |
| GET /api/v1/products/99999 | 404 |
| GET /api/v1/products/-1 | 400 Bad Request |
| GET /api/v1/products/0 | 400 Bad Request |
| GET /api/v1/products/abc | 400 Bad Request |

The negative-ID validation question in the handout is answered by **400**.
`spring-boot-starter-validation` enables the controller's `@Positive` constraint.
Spring MVC validates path parameters before calling the service.
Product fields also declare constraints for names, descriptions, and prices.

Money uses BigDecimal and DECIMAL(10,2), as requested in the final handout steps.
The repository extends both CrudRepository and PagingAndSortingRepository.
The controller delegates to a constructor-injected service and repository.

## Verification and notes

Run `mvn test`. Tests exercise the full Spring context, HTTP controller layer,
actual H2 repository, seed data, invalid IDs, and monetary precision.
See [VERIFICATION.md](VERIFICATION.md) for recorded results.

Spring Boot 4.1.2 follows the handout. Source targets Java 21 to align with the
provided Lab 14 starter. The instructions mistakenly call the controller a
ProductService in one step; the implementation uses ProductController.
The project uses the established edu.psu.se411.lab13 namespace and Maven layout.
Generated target files and logs are ignored by Git.
