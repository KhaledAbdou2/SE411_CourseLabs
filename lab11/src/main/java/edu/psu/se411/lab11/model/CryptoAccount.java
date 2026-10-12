package edu.psu.se411.lab11.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import edu.psu.se411.lab11.exceptions.InsufficientBalanceException;

/** Mutable account; concurrent transfers must use Transaction.execute(). */
public class CryptoAccount {

    private final String id;
    private long balanceInCents;

    public CryptoAccount(String id, Double balance) {
        this.id = Objects.requireNonNull(id, "Account id");
        if (id.isBlank()) {
            throw new IllegalArgumentException("Account id cannot be blank");
        }
        balanceInCents = toCents(balance);
    }

    public void addToBalance(Double amount) {
        credit(toCents(amount));
    }

    public void retrieveFromBalance(Double amount) throws InsufficientBalanceException {
        debit(toCents(amount));
    }

    public String getId() {
        return id;
    }

    public Double getBalance() {
        return balanceInCents / 100.0;
    }

    /** Read totals only after all transaction threads have joined. */
    public long getBalanceInCents() {
        return balanceInCents;
    }

    static long toCents(Double amount) {
        Objects.requireNonNull(amount, "Amount");
        if (!Double.isFinite(amount) || amount < 0) {
            throw new IllegalArgumentException("Amount must be finite and nonnegative: " + amount);
        }
        // The handout uses arbitrary Double amounts; round once to currency precision.
        return BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP)
                .movePointRight(2).longValueExact();
    }

    void credit(long cents) {
        balanceInCents = Math.addExact(balanceInCents, cents);
    }

    void debit(long cents) throws InsufficientBalanceException {
        if (balanceInCents < cents) {
            throw new InsufficientBalanceException(
                    "Account " + id + " has " + getBalance()
                            + " riyals; requested " + cents / 100.0);
        }
        balanceInCents -= cents;
    }
}
