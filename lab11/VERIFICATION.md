# Lab 11 verification

Verified on the MSI Windows laptop on 2026-10-12 using the repository's JDK 23
and Maven setup.

## Build and tests

Command: `mvn -B -ntp clean package exec:java`

- BUILD SUCCESS; process exit code 0.
- 10 tests run, 0 failures, 0 errors, 0 skipped.
- Tests include 100 competing withdrawals from a balance that permits exactly
  50 successes, plus a full 10,000-thread synchronized simulation.
- Account validation, insufficient funds, exact-balance transfers, self-transfers,
  zero amounts, currency rounding, and the sequential unsafe baseline passed.

## Observed demonstration

Each mode started with ten accounts containing 1,000.00 riyals each.

| Mode | Completed | Failed | Final total (riyals) | Difference |
| --- | ---: | ---: | ---: | ---: |
| Unsynchronized | 10,000 | 0 | 10,008.92 | +8.92 |
| Synchronized | 10,000 | 0 | 10,000.00 | 0.00 |

These are actual results from the laptop, not fixed expected unsafe output.
Thread scheduling changes the unsafe result between runs. A coincidentally correct
total would not establish thread safety. The synchronized run must conserve money.

## Logging and repository hygiene

The file `logs/log.out` contains startup, both result summaries, and shutdown.
There were no rejected transactions in this demonstration. The calling application
logs worker exceptions when present.

Git ignore checks confirmed that `target/lab11-1.0-SNAPSHOT.jar`,
`logs/log.out`, and `build-verification.log` remain local.
Only source, tests, Maven configuration, and Markdown documentation are committed.
