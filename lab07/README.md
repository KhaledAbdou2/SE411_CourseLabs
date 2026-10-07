# Lab 07 - Polymorphism in Java

Requires JDK 23 and Maven 3.9.x. Run from this folder:

```cmd
mvn clean package exec:java
```

## Design
An abstract `Booking` stores the booking ID, customer full name, travel date,
and destination city. Each concrete booking overrides `calculateTotalPrice()`.
`BookingService.computeTotalPrice(Booking)` uses dynamic dispatch for all types.
Common field and range validation is reused from the base class.
Money uses BigDecimal. Late-entered values start as missing, not zero.

## Rules and example rates
The handout does not assign numeric rates; these example values are centralized
in `config/BookingConfig.java` in generic currency units.

| Booking | Formula | Valid late-entered value |
| --- | --- | --- |
| Flight | Base price + weight x 15.00 | 0-40 kg inclusive |
| Standard train | Distance x 0.50 | 1-2000 km inclusive |
| First-class train | Distance x 1.00 | 1-2000 km inclusive |
| Car rental | Daily rate x days | 1-30 days inclusive |

Missing data throws checked `MissingInformationException`.
Invalid data throws custom `InvalidArgumentException`, an IllegalArgumentException subtype.
Rejected updates preserve the last valid value.

## Demo and logging
The demo handles three missing-value cases and three invalid-value cases,
then prints totals of 650.00, 150.00, 300.00, and 300.00 through the common service.
File logging records start, stop, and handled exceptions with stack traces in
`logs/App/log4j/log.out`. Those demonstration errors are expected.
Each run replaces the previous log. Generated output and logs are ignored by Git.
The annex's copied main-class name is corrected to `edu.psu.se411.lab07.App`.
The required slf4j-log4j12 dependency redirects to Reload4j, as in Lab 06.

## Tests
Run `mvn test`. Eight tests cover polymorphic formulas, missing inputs, inclusive
boundaries, invalid values, unchanged state after rejection, decimal arithmetic,
constructor validation, and shared booking information.
