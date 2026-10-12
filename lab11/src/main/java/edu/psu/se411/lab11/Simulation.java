package edu.psu.se411.lab11;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import edu.psu.se411.lab11.exceptions.InsufficientBalanceException;
import edu.psu.se411.lab11.model.CryptoAccount;
import edu.psu.se411.lab11.model.Transaction;

public final class Simulation {

    public static final int ACCOUNT_COUNT = 10;
    public static final int TRANSACTION_COUNT = 10_000;
    public static final double INITIAL_BALANCE = 1_000.0;
    public static final long EXPECTED_TOTAL_CENTS = 1_000_000;

    private Simulation() {
    }

    public record Result(List<CryptoAccount> accounts, int completed,
                         List<Exception> failures, long totalInCents) {
    }

    public static long calculateTotal(List<CryptoAccount> accounts) {
        long total = 0;
        for (CryptoAccount account : accounts) {
            total = Math.addExact(total, account.getBalanceInCents());
        }
        return total;
    }

    public static Result run(boolean synchronizedMode, long seed) throws InterruptedException {
        List<CryptoAccount> accounts = new ArrayList<>();
        for (int i = 0; i < ACCOUNT_COUNT; i++) {
            accounts.add(new CryptoAccount("acc_" + i, INITIAL_BALANCE));
        }

        Random random = new Random(seed);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger completed = new AtomicInteger();
        ConcurrentLinkedQueue<Exception> failures = new ConcurrentLinkedQueue<>();
        List<Thread> threads = new ArrayList<>(TRANSACTION_COUNT);
        try {
            for (int i = 0; i < TRANSACTION_COUNT; i++) {
                Transaction transaction = new Transaction("tx_" + i,
                        accounts.get(random.nextInt(ACCOUNT_COUNT)),
                        accounts.get(random.nextInt(ACCOUNT_COUNT)),
                        1.0 + random.nextDouble() * 10.0);
                Thread thread = Thread.ofVirtual().name("transaction-" + i).start(() -> {
                    try {
                        start.await();
                        if (synchronizedMode) {
                            transaction.execute();
                        } else {
                            transaction.executeUnsynchronized();
                        }
                        completed.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        failures.add(e);
                    } catch (InsufficientBalanceException | RuntimeException e) {
                        failures.add(e);
                    }
                });
                threads.add(thread);
            }
        } finally {
            start.countDown();
        }

        // Do not read the balances until every worker has terminated.
        boolean interrupted = false;
        for (Thread thread : threads) {
            boolean joined = false;
            while (!joined) {
                try {
                    thread.join();
                    joined = true;
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        }
        if (interrupted) {
            throw new InterruptedException("Interrupted while waiting for transactions");
        }
        return new Result(List.copyOf(accounts), completed.get(),
                List.copyOf(failures), calculateTotal(accounts));
    }
}
