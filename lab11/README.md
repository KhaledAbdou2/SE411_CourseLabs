# Lab 11 - Introduction to Threads

Simulates the double-spending problem, then fixes concurrent transfers with one
`synchronized` method.

## Run

Use JDK 23 and Maven 3.9.x, as in the previous labs:

```cmd
cd /d C:\Users\khale\Desktop\SE411_Labs\SE411_CourseLabs\lab11
mvn clean package exec:java
```

Run just the tests with `mvn test`, or repeat the demonstration with `mvn exec:java`.
Application events and exceptions are written to `logs/log.out`, replaced on each run.
Maven's SLF4J relocation notice is expected: the supplied Log4j binding resolves
to the maintained reload4j binding.

## Requirements and implementation

- Each mode creates **10 accounts with 1,000 riyals each**, totaling **10,000 riyals**.
- Each mode starts **10,000 separate Java virtual threads**, one per transaction.
  A start gate encourages overlap; every thread is joined before totals are read.
  Virtual threads avoid allocating 10,000 operating-system threads.
- Both modes use fresh accounts and the same seeded random transaction plan:
  two randomly selected accounts and an amount from 1 to less than 11 riyals.
- Part 1 calls `Transaction.executeUnsynchronized()`. Its ordinary read/modify/write
  balance operations deliberately allow races.
- Part 2 calls `Transaction.execute()`, which delegates to the **one static
  synchronized method**, `executeWithSharedLock`. The shared class monitor covers
  the funds check, debit, and credit across all transaction instances.
- `Simulation.calculateTotal` sums balances after all workers finish.
- The logger records application startup, shutdown, summaries, and worker exceptions.

A synchronized instance method on each separate transaction would lock different
objects and would not protect the shared accounts. This simple class-wide lock
serializes all safe transfers; it prioritizes correctness over throughput.

The unsynchronized total varies by scheduling. It can occasionally equal the initial
total despite unsafe code, so neither the demo nor the tests falsely requires every
unsafe run to fail. The corrected total must remain exactly 10,000.00 riyals.

## Starter adaptations

The starter's package/artifact are normalized to `edu.psu.se411.lab11` / `lab11`.
The handout's 1,000-per-account and 10,000-thread values take precedence over the
starter's conflicting constants. The three-argument constructor from the handout
and the starter's four-argument constructor are both supported.

Amounts retain the starter's `Double` API but are rounded once to two decimal places
using HALF_UP and stored as integer cents. This separates real lost updates from
floating-point rounding noise. Negative/nonfinite amounts and null inputs are rejected.
Zero transfers and transfers to the same account do nothing. Insufficient funds raise
the supplied checked exception without changing either balance.

Transactions are immutable. Direct account mutators and the unsafe entry point are
for sequential use or the deliberate race demonstration; do not mix them with safe
concurrent transfers. Read balances only after the workers have joined.

## Files and verification

- `src/main/java`: application, simulation, models, and checked exception.
- `src/main/resources/log4j.properties`: file logging configuration.
- `src/test/java`: transfer validation, competing withdrawals, and full-size simulation tests.
- [VERIFICATION.md](VERIFICATION.md): actual laptop results.

Generated `target/`, logs, and editor settings remain local and are ignored by Git.

## Java references

- [Virtual threads and join](https://docs.oracle.com/en/java/javase/23/core/virtual-threads.html)
- [Shared monitors and join visibility](https://docs.oracle.com/javase/specs/jls/se8/html/jls-17.html)
