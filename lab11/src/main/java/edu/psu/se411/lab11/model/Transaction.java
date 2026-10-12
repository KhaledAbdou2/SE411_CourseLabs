package edu.psu.se411.lab11.model;

import java.util.Objects;
import java.util.UUID;

import edu.psu.se411.lab11.exceptions.InsufficientBalanceException;

public class Transaction {

    private final String id;
    private final CryptoAccount sender;
    private final CryptoAccount receiver;
    private final long amountInCents;

    public Transaction(CryptoAccount sender, CryptoAccount receiver, Double amount) {
        this(UUID.randomUUID().toString(), sender, receiver, amount);
    }

    public Transaction(String id, CryptoAccount sender, CryptoAccount receiver, Double amount) {
        this.id = Objects.requireNonNull(id, "Transaction id");
        this.sender = Objects.requireNonNull(sender, "Sender");
        this.receiver = Objects.requireNonNull(receiver, "Receiver");
        amountInCents = CryptoAccount.toCents(amount);
    }

    /** Corrected public API: all transaction instances share the same monitor. */
    public void execute() throws InsufficientBalanceException {
        executeWithSharedLock(this);
    }

    // Exactly one synchronized method protects the entire debit-and-credit operation.
    private static synchronized void executeWithSharedLock(Transaction transaction)
            throws InsufficientBalanceException {
        transaction.executeUnsynchronized();
    }

    /** Intentionally unsafe baseline for Part 1. Never mix with concurrent safe calls. */
    public void executeUnsynchronized() throws InsufficientBalanceException {
        if (sender == receiver || amountInCents == 0) {
            return;
        }
        // Reject receiver overflow before changing the sender.
        Math.addExact(receiver.getBalanceInCents(), amountInCents);
        sender.debit(amountInCents);
        receiver.credit(amountInCents);
    }

    public String getId() {
        return id;
    }

    public CryptoAccount getSender() {
        return sender;
    }

    public CryptoAccount getReceiver() {
        return receiver;
    }

    public Double getAmount() {
        return amountInCents / 100.0;
    }
}
