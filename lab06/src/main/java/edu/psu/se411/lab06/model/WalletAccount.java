package edu.psu.se411.lab06.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.psu.se411.lab06.exceptions.InsufficientFundsException;

/** In-memory teaching example retaining the starter's double-based API. */
public class WalletAccount {
    private static final Logger LOGGER = LoggerFactory.getLogger(WalletAccount.class);
    private double balance;

    public WalletAccount(double balance) {
        setBalance(balance);
        LOGGER.debug("Wallet account created; balance={}", balance);
    }

    public void withdraw(double amount) throws InsufficientFundsException {
        validateAmount(amount);
        if (amount > balance) {
            throw new InsufficientFundsException(
                    "Insufficient funds. Your balance is " + balance);
        }
        balance -= amount;
        LOGGER.debug("Withdrawal successful; amount={}, balance={}", amount, balance);
    }

    public void deposit(double amount) {
        validateAmount(amount);
        double updatedBalance = balance + amount;
        validateAmount(updatedBalance);
        balance = updatedBalance;
        LOGGER.debug("Deposit successful; amount={}, balance={}", amount, balance);
    }

    public void setBalance(double balance) {
        validateAmount(balance);
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    private static void validateAmount(double amount) {
        if (!Double.isFinite(amount) || amount < 0) {
            throw new IllegalArgumentException(
                    "Amount must be finite and nonnegative: " + amount);
        }
    }
}
