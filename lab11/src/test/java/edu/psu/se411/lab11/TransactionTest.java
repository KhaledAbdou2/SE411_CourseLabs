package edu.psu.se411.lab11;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import edu.psu.se411.lab11.exceptions.InsufficientBalanceException;
import edu.psu.se411.lab11.model.CryptoAccount;
import edu.psu.se411.lab11.model.Transaction;

class TransactionTest {

    @Test
    void transfersExactAmountBetweenAccounts() throws Exception {
        CryptoAccount sender = new CryptoAccount("sender", 1000.0);
        CryptoAccount receiver = new CryptoAccount("receiver", 1000.0);
        Transaction transaction = new Transaction(sender, receiver, 10.25);
        transaction.execute();
        assertEquals(98975, sender.getBalanceInCents());
        assertEquals(101025, receiver.getBalanceInCents());
    }

    @Test
    void rejectsOverdraftWithoutChangingEitherAccount() {
        CryptoAccount sender = new CryptoAccount("sender", 5.0);
        CryptoAccount receiver = new CryptoAccount("receiver", 20.0);
        InsufficientBalanceException failure = assertThrows(InsufficientBalanceException.class,
                () -> new Transaction(sender, receiver, 6.0).execute());
        assertTrue(failure.getMessage().contains("sender"));
        assertEquals(500, sender.getBalanceInCents());
        assertEquals(2000, receiver.getBalanceInCents());
    }

    @Test
    void allowsExactBalanceWithdrawal() throws Exception {
        CryptoAccount sender = new CryptoAccount("sender", 5.0);
        CryptoAccount receiver = new CryptoAccount("receiver", 0.0);
        new Transaction(sender, receiver, 5.0).execute();
        assertEquals(0, sender.getBalanceInCents());
        assertEquals(500, receiver.getBalanceInCents());
    }

    @Test
    void selfTransfersAndZeroAmountsAreNoOps() throws Exception {
        CryptoAccount account = new CryptoAccount("account", 5.0);
        CryptoAccount other = new CryptoAccount("other", 0.0);
        new Transaction(account, account, 100.0).execute();
        new Transaction(account, other, 0.0).execute();
        assertEquals(500, account.getBalanceInCents());
        assertEquals(0, other.getBalanceInCents());
    }

    @Test
    void rejectsInvalidAmountsAndMissingAccounts() {
        CryptoAccount account = new CryptoAccount("account", 5.0);
        for (double amount : new double[]{-1, Double.NaN, Double.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class,
                    () -> new Transaction(account, account, amount));
            assertThrows(IllegalArgumentException.class,
                    () -> new CryptoAccount("bad", amount));
        }
        assertThrows(NullPointerException.class, () -> new Transaction(account, account, null));
        assertThrows(NullPointerException.class, () -> new Transaction(null, account, 1.0));
        assertThrows(NullPointerException.class, () -> new Transaction(account, null, 1.0));
        assertEquals(500, account.getBalanceInCents());
    }

    @Test
    void roundsDoubleAmountsOnceToNearestCent() throws Exception {
        CryptoAccount sender = new CryptoAccount("sender", 10.0);
        CryptoAccount receiver = new CryptoAccount("receiver", 0.0);
        Transaction transaction = new Transaction("rounding", sender, receiver, 1.235);
        transaction.execute();
        assertEquals(1.24, transaction.getAmount());
        assertEquals(876, sender.getBalanceInCents());
        assertEquals(124, receiver.getBalanceInCents());
    }

    @Test
    void starterAccountMethodsValidateBeforeMutation() throws Exception {
        CryptoAccount account = new CryptoAccount("account", 10.0);
        account.addToBalance(2.5);
        account.retrieveFromBalance(1.25);
        assertEquals(1125, account.getBalanceInCents());
        assertThrows(IllegalArgumentException.class, () -> account.addToBalance(-1.0));
        assertThrows(InsufficientBalanceException.class, () -> account.retrieveFromBalance(20.0));
        assertEquals(1125, account.getBalanceInCents());
    }

    @Test
    void unsafeBaselineWorksSequentially() throws Exception {
        CryptoAccount sender = new CryptoAccount("sender", 10.0);
        CryptoAccount receiver = new CryptoAccount("receiver", 0.0);
        new Transaction(sender, receiver, 2.5).executeUnsynchronized();
        assertEquals(750, sender.getBalanceInCents());
        assertEquals(250, receiver.getBalanceInCents());
    }

    @Test
    @Timeout(30)
    void simultaneousWithdrawalsCannotSpendSameMoneyTwice() throws Exception {
        CryptoAccount sender = new CryptoAccount("sender", 50.0);
        CryptoAccount receiver = new CryptoAccount("receiver", 0.0);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger accepted = new AtomicInteger();
        AtomicInteger rejected = new AtomicInteger();
        ConcurrentLinkedQueue<Throwable> unexpected = new ConcurrentLinkedQueue<>();
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            threads.add(Thread.ofVirtual().start(() -> {
                try {
                    start.await();
                    new Transaction(sender, receiver, 1.0).execute();
                    accepted.incrementAndGet();
                } catch (InsufficientBalanceException e) {
                    rejected.incrementAndGet();
                } catch (Throwable e) {
                    unexpected.add(e);
                }
            }));
        }
        start.countDown();
        for (Thread thread : threads) {
            thread.join();
        }
        assertTrue(unexpected.isEmpty(), unexpected.toString());
        assertEquals(50, accepted.get());
        assertEquals(50, rejected.get());
        assertEquals(0, sender.getBalanceInCents());
        assertEquals(5000, receiver.getBalanceInCents());
    }

    @Test
    @Timeout(30)
    void tenThousandThreadsConserveTotalAndTerminate() throws Exception {
        Simulation.Result result = Simulation.run(true, 411L);
        assertEquals(10, result.accounts().size());
        assertEquals(10000, result.completed());
        assertTrue(result.failures().isEmpty(), result.failures().toString());
        assertEquals(1000000, result.totalInCents());
        assertEquals(result.totalInCents(), Simulation.calculateTotal(result.accounts()));
        assertTrue(result.accounts().stream().allMatch(a -> a.getBalanceInCents() >= 0));
    }
}
