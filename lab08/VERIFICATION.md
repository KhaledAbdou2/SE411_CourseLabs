# Lab 08 Verification

Verified on MSI on 2026-10-07 with Java 23.0.1 and Maven 3.9.16.

- `mvn -B -ntp clean package exec:java`: BUILD SUCCESS.
- Ten JUnit tests passed; zero failures, errors, or skipped tests.
- The full ten-iteration simulation ran with the default one-second intervals.
- Console output checks verified 43 observer notifications: 40 for the simulation,
  two for the clone's own observers, and one after dashboard unregistration.
- The clone reported zero observers while the original retained two.
- Updating the clone preserved the original sensor reading.
- The file logger recorded 22 sensor updates plus application start and stop.

Tests also cover repeated registration, explicit notification, unchanged readings,
independent clone registrations for both sensor types, registration changes during
callbacks, invalid inputs, and output from both concrete observer implementations.

Detailed output remains locally in `build-verification.log` and
`logs/App/log4j/log.out`. These generated files are ignored by Git.
