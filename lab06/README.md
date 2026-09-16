# Lab 06 - Logging with SLF4J

Requires JDK 23 and Maven 3.9.x. Run commands from this folder.

## Starter and structure
Adapted from the supplied Lab 06 wallet project. The starter's `edu.spu` typo
is corrected to `edu.psu.se411.lab06`, matching this repository.
`App`, `model/WalletAccount`, and `exceptions/InsufficientFundsException`
retain their roles. The Maven artifact is `lab06`.
Generated Eclipse files and compiled starter output are not imported.

## Run the final application
```cmd
mvn clean package exec:java
```
The default profile is `log4j`, with INFO as the minimum level.
Read `logs/App/log4j/log.out`; application messages go to the file, not the console.
The demo deposits 200 and withdraws 100 from 1000, then handles an overdraft
and a negative deposit. The final balance stays 1100.
Expected error stack traces are part of the exercise; Maven still succeeds.

## Repeat the handout stages
Select only one provider profile per command.
```cmd
mvn -Pno-provider clean package exec:java -Dexec.args=startup-only
mvn -Plogback clean package exec:java -Dexec.args=startup-only
mvn clean package exec:java -Dlab.log.level=ERROR -Dexec.args=startup-only
mvn clean package exec:java -Dlab.log.level=INFO -Dexec.args=startup-only
mvn clean package exec:java -Dlab.log.level=DEBUG
```
No-provider mode emits the SLF4J warning and discards application logs.
Logback displays lifecycle messages. In lifecycle-only mode, ERROR produces
an empty log; INFO includes start/end. The full demo still logs errors at ERROR.
The handout's `slf4j-log4j12:2.0.16` redirects to `slf4j-reload4j:2.0.16`.
Its expected Maven relocation warning does not mean the build failed.

## Tests and evidence
Run `mvn test` for six wallet tests.
With Node.js installed, run `node scripts/verify-logging.mjs` to check providers,
formatting, level filtering, and exception traces. It restores INFO at the end.
Generated evidence is in `target/verification`; logs and build output are ignored.
Each application run replaces `log.out` to avoid mixing different log levels.
See [verification results](VERIFICATION.md) and [AI review](AI_REVIEW.md).

## Configuration
The two configurations are in `src/main/resources`.
Maven filters `lab.log.level` during `package`; use the full commands above after
changing levels, rather than invoking `exec:java` alone.
