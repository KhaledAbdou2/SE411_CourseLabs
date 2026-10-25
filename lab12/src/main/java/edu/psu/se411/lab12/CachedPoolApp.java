package edu.psu.se411.lab12;

/** Second main program requested in the handout. */
public class CachedPoolApp {
    public static void main(String[] args) throws InterruptedException {
        Benchmark.run(Simulation.Pool.CACHED);
    }
}
