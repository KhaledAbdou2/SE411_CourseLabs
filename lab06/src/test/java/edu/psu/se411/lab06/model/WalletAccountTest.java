package edu.psu.se411.lab06.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import edu.psu.se411.lab06.exceptions.InsufficientFundsException;

class WalletAccountTest {
    @Test
    void successfulTransactionsUpdateBalance() throws InsufficientFundsException {
        WalletAccount wallet = new WalletAccount(1000);
        wallet.deposit(200);
        wallet.withdraw(100);
        assertEquals(1100, wallet.getBalance());
    }

    @Test
    void overdraftThrowsSpecificExceptionAndPreservesBalance() {
        WalletAccount wallet = new WalletAccount(1000);
        InsufficientFundsException exception = assertThrows(
                InsufficientFundsException.class, () -> wallet.withdraw(1500));
        assertTrue(exception.getMessage().contains("1000"));
        assertEquals(1000, wallet.getBalance());
    }

    @Test
    void invalidTransactionsPreserveBalance() {
        WalletAccount wallet = new WalletAccount(1000);
        for (double amount : new double[] {-1, Double.NaN, Double.POSITIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> wallet.deposit(amount));
            assertThrows(IllegalArgumentException.class, () -> wallet.withdraw(amount));
            assertEquals(1000, wallet.getBalance());
        }
    }

    @Test
    void invalidBalancesAreRejectedBeforeAssignment() {
        WalletAccount wallet = new WalletAccount(1000);
        for (double balance : new double[] {-1, Double.NaN, Double.NEGATIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> new WalletAccount(balance));
            assertThrows(IllegalArgumentException.class, () -> wallet.setBalance(balance));
            assertEquals(1000, wallet.getBalance());
        }
    }

    @Test
    void exactBalanceAndZeroAmountsFollowStarterContract() throws InsufficientFundsException {
        WalletAccount wallet = new WalletAccount(1000);
        wallet.deposit(0);
        wallet.withdraw(0);
        wallet.withdraw(1000);
        assertEquals(0, wallet.getBalance());
    }

    @Test
    void overflowingDepositPreservesBalance() {
        WalletAccount wallet = new WalletAccount(Double.MAX_VALUE);
        assertThrows(IllegalArgumentException.class, () -> wallet.deposit(Double.MAX_VALUE));
        assertEquals(Double.MAX_VALUE, wallet.getBalance());
    }
}
