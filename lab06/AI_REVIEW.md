# Lab 06 - AI Review of the Logging Strategy

Reviewed by OpenAI Codex on 2026-09-16 against the supplied handout and starter.

## Evaluation and applied improvements

- Replaced console messages with SLF4J and static final class-specific loggers.
- Used parameterized messages so values remain separate from message templates.
- Kept lifecycle messages at INFO and successful wallet events at DEBUG.
- Logged custom exception creation at WARN, as explicitly required by the handout.
- Logged caught transaction exceptions at ERROR with the original throwable,
  preserving stack traces once at the handling boundary.
- Added a finally block so the application-end event also runs after a failure.
- Rejected NaN, infinity, negative amounts, and deposit overflow before mutation.
  Tests verify that failed operations leave the balance unchanged.
- Isolated providers in profiles, avoiding simultaneous Logback and Reload4j.
- Added repeatable assertions for provider behavior and severity filtering.
- Kept generated logs out of Git and replaced the demo log on each run.

## Deliberate teaching constraints

The handout requires a WARN in the exception constructor and an ERROR for
the handled failure. Both events are retained; only the ERROR includes a trace.
For a production application, log once at an appropriate boundary and classify
expected business rejections by operational severity rather than always ERROR.
The starter's double-based API and acceptance of zero amounts are preserved.
Production money should use decimal amounts or integer minor units, with
controlled access to logs that contain balances and appropriate log rotation.

References: [SLF4J manual](https://www.slf4j.org/manual.html),
[SLF4J FAQ](https://www.slf4j.org/faq.html).
