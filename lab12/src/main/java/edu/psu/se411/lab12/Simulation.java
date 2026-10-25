package edu.psu.se411.lab12;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;
import edu.psu.se411.lab12.exceptions.InsufficientBalanceException;
import edu.psu.se411.lab12.model.*;

public final class Simulation {
    public enum Pool { FIXED, CACHED }
    public static final int FIXED_WORKERS = Math.max(2, Math.min(16,
            Runtime.getRuntime().availableProcessors()));
    public static final int MAX_IN_FLIGHT = 256;
    public static final double INITIAL_BALANCE = 10_000.0;

    public record Result(Pool pool, int accounts, int transactions, long succeeded,
                         long rejected, long totalCents, double milliseconds, int peakThreads) {}

    private Simulation() {}

    public static Result run(Pool kind, int count, int transactions, long seed)
            throws InterruptedException {
        if (count < 2 || transactions < 0) throw new IllegalArgumentException("Invalid workload");
        List<CryptoAccount> accounts = new ArrayList<>();
        for (int i = 0; i < count; i++)
            accounts.add(new CryptoAccount(String.format("acc_%04d", i), INITIAL_BALANCE));
        ThreadPoolExecutor executor = (ThreadPoolExecutor) (kind == Pool.FIXED
                ? Executors.newFixedThreadPool(FIXED_WORKERS) : Executors.newCachedThreadPool());
        Semaphore slots = new Semaphore(MAX_IN_FLIGHT);
        LongAdder succeeded = new LongAdder();
        LongAdder rejected = new LongAdder();
        AtomicReference<Throwable> unexpected = new AtomicReference<>();
        Random random = new Random(seed);
        long started = System.nanoTime();
        try {
            for (int i = 0; i < transactions; i++) {
                slots.acquire();
                int from = random.nextInt(count);
                int to = random.nextInt(count - 1);
                if (to >= from) to++;
                Transaction transaction = new Transaction("tx_" + i,
                        accounts.get(from), accounts.get(to), random.nextInt(10_000) / 100.0);
                try {
                    executor.execute(() -> {
                        try {
                            transaction.execute();
                            succeeded.increment();
                        } catch (InsufficientBalanceException e) {
                            rejected.increment();
                        } catch (Throwable e) {
                            unexpected.compareAndSet(null, e);
                        } finally {
                            slots.release();
                        }
                    });
                } catch (RuntimeException | Error e) {
                    slots.release();
                    throw e;
                }
            }
        } finally {
            // close waits for every accepted task and preserves interruption.
            executor.close();
        }
        double elapsed = (System.nanoTime() - started) / 1_000_000.0;
        if (unexpected.get() != null) throw new IllegalStateException("Worker failed", unexpected.get());
        if (succeeded.sum() + rejected.sum() != transactions)
            throw new IllegalStateException("Missing transaction results");
        long total = 0;
        for (CryptoAccount account : accounts) {
            long balance = account.getBalanceInCents();
            if (balance < 0) throw new IllegalStateException("Negative account balance");
            total = Math.addExact(total, balance);
        }
        if (total != count * 1_000_000L) throw new IllegalStateException("Money was not conserved");
        return new Result(kind, count, transactions, succeeded.sum(), rejected.sum(),
                total, elapsed, executor.getLargestPoolSize());
    }
}
