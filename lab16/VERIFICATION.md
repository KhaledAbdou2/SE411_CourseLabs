# Lab 16 verification

2026-11-15, MSI laptop, Java 23.0.1, Maven 3.9.16.

`mvn -B -ntp clean package`: BUILD SUCCESS.
9 tests passed, 0 failures, 0 errors: 3 CRUD tests and 6 security tests.

Verified in a full Spring context with an isolated H2 MySQL-mode test database:
- Complete CRUD lifecycle and generated ID Location header.
- Public home page; anonymous API/dashboard requests require login.
- USER and ADMIN dashboard access and forbidden cross-role access.
- Actual form login with both configured accounts; wrong password rejected.
- Login form includes CSRF token; missing tokens reject login and API writes.
- Logout clears authentication and stored passwords match BCrypt verification.

**Pending:** live Aiven/MySQL connection and browser/Postman session verification
against that database. No credentials were provided. H2 tests are not proof of
MySQL compatibility. The application's runtime configuration uses MySQL only.

The startup configuration preserves existing data. Personal database credentials
belong in ignored config/application-local.properties, never in tracked files.
