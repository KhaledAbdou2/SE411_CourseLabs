package edu.psu.se411.lab12;

import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.Test;
import edu.psu.se411.lab12.exceptions.InsufficientBalanceException;
import edu.psu.se411.lab12.model.*;

class TransactionTest {
    @Test
    void transfersExactAmount() throws Exception {
        CryptoAccount a = new CryptoAccount("a", 100.0);
        CryptoAccount b = new CryptoAccount("b", 50.0);
        new Transaction("t", a, b, 12.34).execute();
        assertEquals(8766, a.getBalanceInCents());
        assertEquals(6234, b.getBalanceInCents());
    }

    @Test
    void rejectsOverdraftWithoutMutation() {
        CryptoAccount a = new CryptoAccount("a", 1.0);
        CryptoAccount b = new CryptoAccount("b", 2.0);
        assertThrows(InsufficientBalanceException.class,
                () -> new Transaction("t", a, b, 1.01).execute());
        assertEquals(100, a.getBalanceInCents());
        assertEquals(200, b.getBalanceInCents());
    }

    @Test
    void exactBalanceAndZeroTransfers() throws Exception {
        CryptoAccount a = new CryptoAccount("a", 1.0);
        CryptoAccount b = new CryptoAccount("b", 0.0);
        new Transaction("zero", a, b, 0.0).execute();
        new Transaction("all", a, b, 1.0).execute();
        assertEquals(0, a.getBalanceInCents());
        assertEquals(100, b.getBalanceInCents());
    }

    @Test
    void rejectsInvalidAmountsAndSelfTransfers() {
        CryptoAccount a = new CryptoAccount("a", 1.0);
        CryptoAccount b = new CryptoAccount("b", 1.0);
        for (double value : new double[]{-1, Double.NaN, Double.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> new CryptoAccount("bad", value));
            assertThrows(IllegalArgumentException.class, () -> new Transaction("bad", a, b, value));
        }
        assertThrows(IllegalArgumentException.class, () -> new Transaction("self", a, a, 0.5));
        assertThrows(NullPointerException.class, () -> new Transaction("null", a, b, null));
        assertThrows(NullPointerException.class, () -> new Transaction("null", null, b, 1.0));
    }

    @Test
    void roundsAmountsToCurrencyPrecision() throws Exception {
        CryptoAccount a = new CryptoAccount("a", 10.0);
        CryptoAccount b = new CryptoAccount("b", 0.0);
        new Transaction("t", a, b, 1.235).execute();
        assertEquals(876, a.getBalanceInCents());
        assertEquals(124, b.getBalanceInCents());
    }

    @Test
    void directAccountMethodsValidateAndPreserveBalance() throws Exception {
        CryptoAccount a = new CryptoAccount("a", 10.0);
        a.addToBalance(2.0);
        a.retrieveFromBalance(1.0);
        assertThrows(IllegalArgumentException.class, () -> a.retrieveFromBalance(-1.0));
        assertThrows(InsufficientBalanceException.class, () -> a.retrieveFromBalance(12.0));
        assertEquals(1100, a.getBalanceInCents());
    }

    @Test
    void runnableDoesNotSilentlySwallowRejection() {
        CryptoAccount a = new CryptoAccount("a", 0.0);
        CryptoAccount b = new CryptoAccount("b", 0.0);
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> new Transaction("t", a, b, 1.0).run());
        assertInstanceOf(InsufficientBalanceException.class, error.getCause());
    }

    @Test
    void reverseTransfersCompleteWithoutDeadlock() throws Exception {
        exerciseOppositeDirections("a", "b");
    }

    @Test
    void duplicateIdsStillHaveConsistentLockOrder() throws Exception {
        exerciseOppositeDirections("same", "same");
    }

    private void exerciseOppositeDirections(String firstId, String secondId) throws Exception {
        CryptoAccount a = new CryptoAccount(firstId, 20000.0);
        CryptoAccount b = new CryptoAccount(secondId, 20000.0);
        ExecutorService pool = Executors.newFixedThreadPool(2, Thread.ofPlatform().daemon().factory());
        CountDownLatch start = new CountDownLatch(1);
        try {
            Future<?> forward = pool.submit(() -> {
                start.await();
                for (int i = 0; i < 10000; i++) new Transaction("f", a, b, 1.0).execute();
                return null;
            });
            Future<?> backward = pool.submit(() -> {
                start.await();
                for (int i = 0; i < 10000; i++) new Transaction("b", b, a, 1.0).execute();
                return null;
            });
            start.countDown();
            forward.get(10, TimeUnit.SECONDS);
            backward.get(10, TimeUnit.SECONDS);
            assertEquals(2000000, a.getBalanceInCents());
            assertEquals(2000000, b.getBalanceInCents());
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    void exceptionReleasesBothLocksForAnotherThread() throws Exception {
        CryptoAccount a = new CryptoAccount("a", 0.0);
        CryptoAccount b = new CryptoAccount("b", 10.0);
        assertThrows(InsufficientBalanceException.class,
                () -> new Transaction("fail", a, b, 1.0).execute());
        ExecutorService pool = Executors.newSingleThreadExecutor(Thread.ofPlatform().daemon().factory());
        try {
            pool.submit(() -> {
                new Transaction("next", b, a, 1.0).execute();
                return null;
            }).get(3, TimeUnit.SECONDS);
            assertEquals(100, a.getBalanceInCents());
            assertEquals(900, b.getBalanceInCents());
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    void concurrentSpendingCannotOverdraw() throws Exception {
        CryptoAccount a = new CryptoAccount("a", 1.0);
        CryptoAccount b = new CryptoAccount("b", 0.0);
        ExecutorService pool = Executors.newFixedThreadPool(8);
        java.util.List<Future<Boolean>> results = new java.util.ArrayList<>();
        try {
            for (int i = 0; i < 100; i++) {
                results.add(pool.submit(() -> {
                    try {
                        new Transaction("spend", a, b, 1.0).execute();
                        return true;
                    } catch (InsufficientBalanceException e) {
                        return false;
                    }
                }));
            }
            int accepted = 0;
            for (Future<Boolean> result : results) if (result.get(3, TimeUnit.SECONDS)) accepted++;
            assertEquals(1, accepted);
            assertEquals(0, a.getBalanceInCents());
            assertEquals(100, b.getBalanceInCents());
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    void bothPoolsFinishAndConserveTotal() throws Exception {
        for (Simulation.Pool pool : Simulation.Pool.values()) {
            Simulation.Result result = Simulation.run(pool, 10, 20000, 411L);
            assertEquals(20000, result.succeeded() + result.rejected());
            assertEquals(10000000, result.totalCents());
            assertTrue(result.peakThreads() > 0);
        }
    }
}
