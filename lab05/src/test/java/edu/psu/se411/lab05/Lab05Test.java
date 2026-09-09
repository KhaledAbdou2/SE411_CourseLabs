package edu.psu.se411.lab05;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import edu.psu.se411.lab05.exceptions.InvalidAgeException;
import edu.psu.se411.lab05.exceptions.InsufficientFundsException;

class Lab05Test {
    @Test
    void ageBelowEighteenIsRejected() {
        InvalidAgeException exception =
                assertThrows(InvalidAgeException.class, () -> Main.validateAge(17));
        assertTrue(exception.getMessage().contains("18"));
        assertThrows(InvalidAgeException.class, () -> Main.validateAge(-1));
    }

    @Test
    void ageEighteenAndOlderIsAccepted() {
        assertDoesNotThrow(() -> Main.validateAge(18));
        assertDoesNotThrow(() -> Main.validateAge(25));
    }

    @Test
    void successfulWithdrawalCreditsSimulatedBank() throws InsufficientFundsException {
        Wallet wallet = new Wallet(new BigDecimal("100.00"));
        BigDecimal bankBalance = new BigDecimal("0.00");
        bankBalance = bankBalance.add(wallet.withdraw(new BigDecimal("40.00")));
        assertEquals(new BigDecimal("60.00"), wallet.getBalance());
        assertEquals(new BigDecimal("40.00"), bankBalance);
        assertEquals(new BigDecimal("100.00"), wallet.getBalance().add(bankBalance));
    }

    @Test
    void insufficientFundsLeavesBalanceUnchanged() {
        Wallet wallet = new Wallet(new BigDecimal("60.00"));
        InsufficientFundsException exception = assertThrows(
                InsufficientFundsException.class,
                () -> wallet.withdraw(new BigDecimal("80.00")));
        assertTrue(exception.getMessage().contains("60.00"));
        assertEquals(new BigDecimal("60.00"), wallet.getBalance());
    }

    @Test
    void exactBalanceCanBeWithdrawn() throws InsufficientFundsException {
        Wallet wallet = new Wallet(new BigDecimal("10.00"));
        assertEquals(new BigDecimal("10.00"), wallet.withdraw(new BigDecimal("10")));
        assertEquals(new BigDecimal("0.00"), wallet.getBalance());
        assertThrows(InsufficientFundsException.class,
                () -> wallet.withdraw(new BigDecimal("0.01")));
    }

    @Test
    void zeroAndNegativeWithdrawalsAreRejectedWithoutMutation() {
        Wallet wallet = new Wallet(new BigDecimal("10.00"));
        for (String amount : new String[] {"0", "-1"}) {
            assertThrows(IllegalArgumentException.class,
                    () -> wallet.withdraw(new BigDecimal(amount)));
            assertEquals(new BigDecimal("10.00"), wallet.getBalance());
        }
    }

    @Test
    void invalidMoneyValuesAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Wallet(new BigDecimal("-0.01")));
        assertThrows(NullPointerException.class, () -> new Wallet(null));
        Wallet wallet = new Wallet(new BigDecimal("10.00"));
        assertThrows(NullPointerException.class, () -> wallet.withdraw(null));
        assertThrows(IllegalArgumentException.class,
                () -> wallet.withdraw(new BigDecimal("0.001")));
        assertEquals(new BigDecimal("10.00"), wallet.getBalance());
    }

    @Test
    void decimalArithmeticIsExact() throws InsufficientFundsException {
        Wallet wallet = new Wallet(new BigDecimal("0.30"));
        wallet.withdraw(new BigDecimal("0.10"));
        wallet.withdraw(new BigDecimal("0.20"));
        assertEquals(new BigDecimal("0.00"), wallet.getBalance());
    }
}
