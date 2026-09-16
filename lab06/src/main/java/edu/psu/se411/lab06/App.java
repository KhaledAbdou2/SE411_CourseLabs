package edu.psu.se411.lab06;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.psu.se411.lab06.exceptions.InsufficientFundsException;
import edu.psu.se411.lab06.model.WalletAccount;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        LOGGER.info("Application is starting...");
        try {
            // Reproduces the handout's initial lifecycle-only logging experiment.
            if (args.length > 0 && "startup-only".equals(args[0])) {
                return;
            }
            WalletAccount account = new WalletAccount(1000);
            account.deposit(200);
            try {
                account.withdraw(100);
                account.withdraw(1500);
            } catch (InsufficientFundsException exception) {
                LOGGER.error("Withdrawal failed: {}", exception.getMessage(), exception);
            }
            try {
                account.deposit(-100);
            } catch (IllegalArgumentException exception) {
                LOGGER.error("Deposit failed: {}", exception.getMessage(), exception);
            }
        } finally {
            LOGGER.info("Application ends.");
        }
    }
}
