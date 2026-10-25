# Lab 12 verification

Run on the MSI laptop on 2026-10-25, Windows 11, Java 23.0.1, Maven 3.9.16.
The JVM reported 16 processors. Fixed pool: 16 workers. Both pool types used
the same limit of 256 outstanding tasks.

## Command and tests

```cmd
mvn -B -ntp clean package exec:java -Dexec.mainClass=edu.psu.se411.lab12.Benchmark
```

BUILD SUCCESS. 12 tests passed; 0 failures, 0 errors, 0 skipped.

Tests cover exact transfers, rejected overdrafts, zero/exact-balance amounts,
input validation, currency rounding, direct account operations, Runnable failure
reporting, simultaneous spending, both pool implementations, opposite-direction
transfers, duplicate account IDs, and lock release after an exception.
The opposite-direction tests use time-limited Future.get calls.

## Measured comparison

Each row ran **1,000,000 transactions**. Each account started with 10,000.00 riyals.

| Pool | Accounts | Time (ms) | Peak threads | Succeeded | Insufficient funds | Final total (riyals) |
| --- | ---: | ---: | ---: | ---: | ---: | ---: |
| Fixed | 1,000 | 822.067 | 16 | 1,000,000 | 0 | 10,000,000.00 |
| Cached | 1,000 | 3,735.901 | 15 | 1,000,000 | 0 | 10,000,000.00 |
| Fixed | 10 | 613.175 | 16 | 996,090 | 3,910 | 100,000.00 |
| Cached | 10 | 3,448.816 | 38 | 996,091 | 3,909 | 100,000.00 |

All submitted transactions were accounted for. No account became negative, and
every final total exactly matched its initial total.

## What do we observe?

The fixed pool was about 4.5 times faster with 1,000 accounts and 5.6 times faster
with 10 accounts in this run. Cached pools reuse idle threads but perform direct
task handoffs and may create more workers when existing workers are busy.
For these tiny CPU/locking tasks, that behavior did not improve throughput.

The cached pool's peak grew from 15 to 38 threads in the 10-account case,
consistent with more workers waiting on shared account locks. This is a plausible
explanation, not a profiler-confirmed attribution. The fixed pool kept 16 workers.

Both pools were faster with 10 accounts in this particular run. Fewer accounts
increase contention, but total time also depends on locality, scheduling, handoffs,
and rejected work. Therefore, the measurements do not prove that fewer accounts
must always be slower, or that a fixed pool is universally faster.

Some 10-account transfers were rejected because the selected sender lacked funds
at that instant. The one-transaction difference in rejection counts is expected:
identical random inputs do not impose identical execution order across pools.
Rejections leave both balances unchanged.

These are single measured cases after small warmups, under bounded submission.
They are illustrative laptop measurements, not a statistically controlled benchmark.

## Locks and logging

Acquiring account locks in a total order prevents the A-waits-for-B/B-waits-for-A
cycle. Every lock is released in a finally block, including on rejected transfers.
The README explains the unsafe sender-first approach and the ordering solution.

The application log records startup, the four summaries, rejection counts, and
shutdown. Runtime logs, compiled output, and build-verification.log are ignored;
source files, tests, Maven configuration, and this report are tracked.
