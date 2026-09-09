# Lab 05 - Exception Handling

Requires JDK 23 and Maven 3.9.x. Run commands from this folder.

## Build, test, and run
```cmd
mvn clean package exec:java
```
Run only the JUnit tests with `mvn test`.

## Exercise 1: Custom age exception
`Main.validateAge(int)` throws the checked `InvalidAgeException` below 18.
Ages 18 and above print the valid-age message. The demo uses 17, 18, and 25.

## Exercise 2: Online wallet
`Wallet.withdraw(BigDecimal)` returns the amount to credit to a simulated bank
balance. An overdraw throws the checked `InsufficientFundsException`.
Both custom exception classes live in the dedicated `exceptions` package.
The demo starts with 100.00 in the wallet and 0.00 in the bank: a 40.00 transfer
succeeds, then an 80.00 withdrawal fails. Final balances are 60.00 and 40.00.
This is an in-memory simulation, with no connection to a real bank.

Money uses exact decimal arithmetic. Null, negative opening balances,
nonpositive withdrawals, and fractional cents are rejected before mutation.
Eight JUnit tests cover age boundaries, transfers, overdrafts, and invalid input.

## Copilot review
Completed on 2026-09-09. Copilot found no source defects; no code changes were needed.
See [Copilot review](COPILOT_REVIEW.md) for findings and the verified Maven test result.
