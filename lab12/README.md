# Lab 12 - Threads and Locks

Compares fixed and cached thread pools, and replaces synchronized account operations
with ordered ReentrantLock acquisition.

## Run on Windows

Use JDK 23 and Maven 3.9.x. From this folder:

```cmd
mvn clean package
mvn exec:java
mvn exec:java -Dexec.mainClass=edu.psu.se411.lab12.CachedPoolApp
```

The first application (`App`) uses a fixed pool; `CachedPoolApp` is the second main
program requested by the handout. Each runs one million transactions with 1,000
accounts, then one million with 10 accounts.

Run the combined comparison:

```cmd
mvn exec:java -Dexec.mainClass=edu.psu.se411.lab12.Benchmark
```

Run tests only with `mvn test`. See [VERIFICATION.md](VERIFICATION.md) for measured
results and observations from the laptop.

## What could go wrong with locks?

Acquiring the sender lock before the receiver lock can deadlock. For example, an
A-to-B transfer holds A and waits for B while a B-to-A transfer holds B and waits for
A. Neither can proceed. Forgetting to unlock after an exception can also block all
later transfers involving that account.

The solution always locks the lower account first, then the higher account,
regardless of transfer direction. Accounts are ordered by immutable ID, with an
immutable unique sequence number to break ties between duplicate IDs. This creates
a total order and prevents a circular wait. Both locks are released in nested
finally blocks, in reverse order. Funds checking and both balance changes happen
while both locks are held; rejected transfers leave both balances unchanged.

## Pool comparison method

- Genuine `Executors.newFixedThreadPool` and `Executors.newCachedThreadPool` use
  platform threads. Fixed workers = available processors, bounded to 2 through 16.
- The starter incorrectly uses the transaction count as the fixed pool size.
  A million tasks should be executed by reusable workers, not a million OS threads.
- Both pools use the same producer limit of 256 outstanding tasks. A semaphore
  applies backpressure equally to both; this avoids unlimited thread/queue growth.
  This is a comparison under bounded submission, not an unrestricted cached-pool benchmark.
- Both use ordered account locks, fresh accounts, the same seed, and the same
  transaction-generation algorithm. Amounts range from 0.00 to 99.99 riyals.
- Each account begins with 10,000.00 riyals, as in this starter.
- Elapsed time includes generation, submission, execution, and waiting for shutdown.
  Account creation and final balance checking are excluded.
- A 20,000-transaction warmup runs for each selected pool. The full measured cases
  run once each, so the results are illustrative and affected by JVM scheduling and
  other activity on the laptop, not a rigorous microbenchmark.
- The output includes peak pool size, success/rejection counts, and final totals.
  Insufficient funds are counted separately; unexpected worker failures fail the run.

With fewer accounts, transfers compete for the same locks more often. A cached
pool can add threads when existing workers are busy, but extra threads do not
remove contention and can increase scheduling costs. The observed winner depends
on workload, processor count, and submission behavior.

## Structure and starter adaptations

Package: `edu.psu.se411.lab12`; Maven artifact: `lab12`.
The starter account, transaction, exception, and main-program concepts are retained.
Transactions and account identities are immutable so lock ordering cannot change
mid-transfer. Self-transfers are rejected, matching the starter.

Money uses integer cents internally, with the original Double-facing API rounded
HALF_UP to two decimal places. This avoids mistaking floating-point drift for lost
money. Account getters and direct mutators also use their account lock.

`src/main/resources/log4j.properties` writes start/stop events, benchmark summaries,
and unexpected failures to `logs/log.out`. Expected insufficient-funds rejections
are summarized rather than producing up to a million log entries.
The SLF4J-to-reload4j Maven relocation notice is expected.

Generated `target/`, logs, and local editor settings stay on the laptop and are
ignored by Git. Source, tests, Maven configuration, and documentation are tracked.

## References

- [Java executor factories](https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/util/concurrent/Executors.html)
- [ReentrantLock and finally-based unlock](https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/util/concurrent/locks/ReentrantLock.html)
