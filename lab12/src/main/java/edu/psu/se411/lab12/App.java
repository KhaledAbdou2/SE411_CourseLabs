package edu.psu.se411.lab12;

/** Fixed thread pool entry point. */
public class App {
    public static void main(String[] args) throws InterruptedException {
        Benchmark.run(Simulation.Pool.FIXED);
    }
}
