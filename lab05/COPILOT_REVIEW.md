# Lab 05 - GitHub Copilot Review

Date: 2026-09-09
Reviewed source revision: `3b2f3ab`
Evidence: GitHub Copilot's response supplied by the student from VS Code Chat.

## Scope and findings

Copilot reviewed the four production Java files under `lab05/src/main/java`
and consulted the tests and README. It reported no source defects.

- Both custom exceptions are checked and specific to their failure conditions.
- Catch blocks handle specific exceptions instead of broad `Exception`.
- Messages include relevant constraints and values.
- Validation occurs before wallet balance mutation.
- Failed withdrawals preserve the balance.
- Successful withdrawals return the amount credited to the simulated bank.

Copilot observed that null inputs throw `NullPointerException`, while other
invalid values throw `IllegalArgumentException`. It considered this conventional,
consistently tested behavior, not a defect. No source changes were needed.

## Verified test result

After the review, `mvn -B -ntp -f lab05/pom.xml test` completed successfully:
**8 tests, 0 failures, 0 errors, 0 skipped; BUILD SUCCESS.**
Copilot's response reported 9 tests; the separate Maven run confirms 8 for this
revision. This record uses Maven's verified count.
