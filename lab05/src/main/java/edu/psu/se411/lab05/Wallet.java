package edu.psu.se411.lab05;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import edu.psu.se411.lab05.exceptions.InsufficientFundsException;

/** A single-user wallet whose money values have exactly two decimal places. */
public class Wallet {
    private BigDecimal balance;

    public Wallet(BigDecimal openingBalance) {
        balance = money(openingBalance);
        if (balance.signum() < 0) {
            throw new IllegalArgumentException("Opening balance cannot be negative.");
        }
    }

    public BigDecimal getBalance() {
        return balance;
    }

    /** Removes and returns the money to credit to the user's bank account. */
    public BigDecimal withdraw(BigDecimal amount) throws InsufficientFundsException {
        BigDecimal withdrawal = money(amount);
        if (withdrawal.signum() <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (withdrawal.compareTo(balance) > 0) {
            throw new InsufficientFundsException(
                    "Cannot withdraw " + withdrawal + "; available balance is " + balance + ".");
        }
        balance = balance.subtract(withdrawal);
        return withdrawal;
    }

    private static BigDecimal money(BigDecimal value) {
        Objects.requireNonNull(value, "Money value cannot be null.");
        try {
            return value.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException("Money must use whole cents.", exception);
        }
    }
}
