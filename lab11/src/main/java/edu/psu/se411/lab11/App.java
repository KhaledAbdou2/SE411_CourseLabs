package edu.psu.se411.lab11;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        LOGGER.info("Application is starting...");
        try {
            System.out.println("10 accounts x 1000.00 riyals; 10000 threads per mode");
            System.out.println("Fresh accounts and identical random transaction plans in both modes.");
            printResult("UNSYNCHRONIZED", Simulation.run(false, 411L));
            Simulation.Result safe = Simulation.run(true, 411L);
            printResult("SYNCHRONIZED", safe);
            if (safe.totalInCents() != Simulation.EXPECTED_TOTAL_CENTS
                    || !safe.failures().isEmpty()
                    || safe.accounts().stream().anyMatch(a -> a.getBalanceInCents() < 0)) {
                throw new IllegalStateException("Synchronized simulation failed its balance checks");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("Simulation interrupted", e);
            throw new IllegalStateException("Simulation interrupted", e);
        } catch (RuntimeException e) {
            LOGGER.error("Simulation failed", e);
            throw e;
        } finally {
            LOGGER.info("Application is closing...");
        }
    }

    private static void printResult(String mode, Simulation.Result result) {
        String summary = String.format(Locale.ROOT,
                "%s: completed=%d, failed=%d, total=%.2f, difference=%+.2f riyals",
                mode, result.completed(), result.failures().size(),
                result.totalInCents() / 100.0,
                (result.totalInCents() - Simulation.EXPECTED_TOTAL_CENTS) / 100.0);
        System.out.println(summary);
        LOGGER.info(summary);
        for (Exception failure : result.failures()) {
            LOGGER.error(mode + " transaction rejected", failure);
        }
        if (mode.equals("UNSYNCHRONIZED")) {
            System.out.println(result.totalInCents() == Simulation.EXPECTED_TOTAL_CENTS
                    ? "No total mismatch observed this run; this does not prove thread safety."
                    : "Race observed: concurrent account updates did not conserve money.");
        }
    }
}
