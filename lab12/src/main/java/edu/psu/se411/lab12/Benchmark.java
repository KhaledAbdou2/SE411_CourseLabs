package edu.psu.se411.lab12;

import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Benchmark {
    private static final Logger LOGGER = LoggerFactory.getLogger(Benchmark.class);
    private Benchmark() {}

    /** Runs both programs' workloads together for a convenient comparison. */
    public static void main(String[] args) throws InterruptedException {
        run(Simulation.Pool.FIXED, Simulation.Pool.CACHED);
    }

    static void run(Simulation.Pool... pools) throws InterruptedException {
        LOGGER.info("Application is starting...");
        System.out.printf("Java=%s; processors=%d; fixed workers=%d; max in flight=%d%n",
                System.getProperty("java.version"), Runtime.getRuntime().availableProcessors(),
                Simulation.FIXED_WORKERS, Simulation.MAX_IN_FLIGHT);
        System.out.println("pool,accounts,transactions,succeeded,rejected,total_riyals,milliseconds,peak_threads");
        try {
            // Small untimed warmup per pool; the full cases always use fresh accounts.
            for (Simulation.Pool pool : pools) Simulation.run(pool, 100, 20_000, 12L);
            for (int accounts : new int[]{1000, 10}) {
                for (Simulation.Pool pool : pools) {
                    Simulation.Result r = Simulation.run(pool, accounts, 1_000_000, 411L);
                    String line = String.format(Locale.ROOT, "%s,%d,%d,%d,%d,%.2f,%.3f,%d",
                            r.pool(), r.accounts(), r.transactions(), r.succeeded(), r.rejected(),
                            r.totalCents() / 100.0, r.milliseconds(), r.peakThreads());
                    System.out.println(line);
                    LOGGER.info(line);
                    if (r.rejected() > 0)
                        LOGGER.warn("{} transactions rejected for insufficient funds", r.rejected());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("Benchmark interrupted", e);
            throw e;
        } catch (RuntimeException e) {
            LOGGER.error("Benchmark failed", e);
            throw e;
        } finally {
            LOGGER.info("Application is closing...");
        }
    }
}
