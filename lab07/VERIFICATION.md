# Lab 07 Verification

Verified on MSI on 2026-10-07 with Java 23.0.1 and Maven 3.9.16.

- `mvn -B -ntp clean package exec:java`: BUILD SUCCESS.
- Eight JUnit tests passed; zero failures, errors, or skipped tests.
- Demo totals: flight 650.00, standard train 150.00, first-class train 300.00,
  car rental 300.00.
- File log includes application start and stop and six expected exception events:
  three missing values and three invalid values, with exception stack traces.
- Tests exercise the shared Booking service, all inclusive limits, absent inputs,
  invalid numeric values, preserved state after rejection, and exact decimal pricing.

Detailed build output remains locally in `build-verification.log`.
The runtime file is `logs/App/log4j/log.out`. Both are ignored by Git.
