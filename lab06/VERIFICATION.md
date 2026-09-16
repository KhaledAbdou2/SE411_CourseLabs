# Lab 06 - Verification Results

Verified on the MSI laptop on 2026-09-16 using Java 23.0.1 and Maven 3.9.16.

## Build and wallet tests
`mvn -B -ntp clean package exec:java` completed with BUILD SUCCESS.
**6 tests, 0 failures, 0 errors, 0 skipped.**

## Logging integration checks
`node scripts/verify-logging.mjs` exited successfully.
The no-provider profile emitted the expected warning and no application logs.
Logback emitted lifecycle messages. With the file provider, lifecycle-only mode
produced an empty file at ERROR and formatted start/end messages at INFO.

For the full wallet demo, the script verified these event counts:

| Minimum level | INFO | DEBUG | WARN | ERROR |
| --- | ---: | ---: | ---: | ---: |
| ERROR | 0 | 0 | 0 | 2 |
| WARN | 0 | 0 | 1 | 2 |
| INFO | 2 | 0 | 1 | 2 |
| DEBUG | 2 | 3 | 1 | 2 |

Both exception stack traces were present at every tested threshold.
The default INFO configuration was restored after verification.
Maven's relocation notice for slf4j-log4j12 is expected.
Detailed run output and per-level application logs remain locally under
`target/verification`; source code, tests, and this summary are tracked in Git.
