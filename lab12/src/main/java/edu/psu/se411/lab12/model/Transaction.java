package edu.psu.se411.lab12.model;

import java.util.Objects;
import edu.psu.se411.lab12.exceptions.InsufficientBalanceException;

public final class Transaction implements Runnable {
    private final String id;
    private final CryptoAccount sender;
    private final CryptoAccount receiver;
    private final long amount;

    public Transaction(String id, CryptoAccount sender, CryptoAccount receiver, Double amount) {
        this.id = Objects.requireNonNull(id, "Transaction id");
        this.sender = Objects.requireNonNull(sender, "Sender");
        this.receiver = Objects.requireNonNull(receiver, "Receiver");
        if (sender == receiver) throw new IllegalArgumentException("Sender and receiver must differ");
        this.amount = CryptoAccount.toCents(amount);
    }

    public void execute() throws InsufficientBalanceException {
        CryptoAccount first = sender.compareOrder(receiver) < 0 ? sender : receiver;
        CryptoAccount second = first == sender ? receiver : sender;
        first.lock.lock();
        try {
            second.lock.lock();
            try {
                sender.transferTo(receiver, amount);
            } finally {
                second.lock.unlock();
            }
        } finally {
            first.lock.unlock();
        }
    }

    @Override
    public void run() {
        try { execute(); }
        catch (InsufficientBalanceException e) {
            // Runnable cannot declare a checked exception. Do not silently lose it.
            throw new IllegalStateException("Transaction " + id + " rejected", e);
        }
    }

    public String getId() { return id; }
    public CryptoAccount getSender() { return sender; }
    public CryptoAccount getReceiver() { return receiver; }
    public Double getAmount() { return amount / 100.0; }
}
