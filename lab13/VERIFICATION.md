# Lab 13 verification

Verified on MSI, Windows, 2026-11-02, using Java 23.0.1 and Maven 3.9.16.
Spring Boot 4.1.2; source compatibility Java 21.

## Build and tests

`mvn -B -ntp clean package` completed with exit code 0.
9 tests passed, 0 failures, 0 errors, 0 skipped.

The tests load the complete Spring application and real H2 schema/data.
They check all 17 sample products, lookup, exact BigDecimal price storage,
missing records, negative/zero/nonnumeric/out-of-range IDs, and field validation.

## Live HTTP verification

The packaged executable JAR was started on 127.0.0.1:8080.

| Request | Observed result |
| --- | --- |
| GET /api/v1/products/10 | 200; product 10, 3d printer, price 1840.25 |
| GET /api/v1/products/-1 | 400; validation failure |
| GET /api/v1/products/99999 | 404; product not found |

Thus the handout's negative-ID question is answered by HTTP 400 Bad Request.
Application data is in memory and resets on restart.
Build output and server logs stay local and are ignored by Git.
