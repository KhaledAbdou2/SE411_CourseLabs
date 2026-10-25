package edu.psu.se411.lab12.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import edu.psu.se411.lab12.exceptions.InsufficientBalanceException;

public final class CryptoAccount {
    private static final AtomicLong SEQUENCE = new AtomicLong();
    private final long order = SEQUENCE.getAndIncrement();
    private final String id;
    final ReentrantLock lock = new ReentrantLock();
    private long cents;

    public CryptoAccount(String id, Double balance) {
        this.id = Objects.requireNonNull(id, "Account id");
        if (id.isBlank()) throw new IllegalArgumentException("Blank account id");
        cents = toCents(balance);
    }

    static long toCents(Double amount) {
        Objects.requireNonNull(amount, "Amount");
        if (!Double.isFinite(amount) || amount < 0)
            throw new IllegalArgumentException("Amount must be finite and nonnegative");
        return BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP)
                .movePointRight(2).longValueExact();
    }

    // Immutable tie-breaker makes the order total even if IDs are duplicated.
    int compareOrder(CryptoAccount other) {
        int comparison = id.compareTo(other.id);
        return comparison != 0 ? comparison : Long.compare(order, other.order);
    }

    public String getId() { return id; }

    public Double getBalance() { return getBalanceInCents() / 100.0; }

    public long getBalanceInCents() {
        lock.lock();
        try { return cents; }
        finally { lock.unlock(); }
    }

    public void addToBalance(Double amount) {
        long value = toCents(amount);
        lock.lock();
        try { cents = Math.addExact(cents, value); }
        finally { lock.unlock(); }
    }

    public void retrieveFromBalance(Double amount) throws InsufficientBalanceException {
        long value = toCents(amount);
        lock.lock();
        try {
            if (value > cents) throw new InsufficientBalanceException(id);
            cents -= value;
        } finally { lock.unlock(); }
    }

    // Called only while both account locks are held by Transaction.
    void transferTo(CryptoAccount receiver, long amount) throws InsufficientBalanceException {
        if (!lock.isHeldByCurrentThread() || !receiver.lock.isHeldByCurrentThread())
            throw new IllegalStateException("Both account locks are required");
        if (amount > cents) throw new InsufficientBalanceException(id);
        long updatedReceiver = Math.addExact(receiver.cents, amount);
        cents -= amount;
        receiver.cents = updatedReceiver;
    }
}
