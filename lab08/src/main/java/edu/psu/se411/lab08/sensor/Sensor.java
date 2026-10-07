package edu.psu.se411.lab08.sensor;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import edu.psu.se411.lab08.observer.Observer;
import edu.psu.se411.lab08.observer.Subject;

/** Shared state and observer management for the single-threaded simulation. */
public abstract class Sensor implements Subject, Cloneable {
    private final String name;
    private final String unit;
    private double reading;
    private Set<Observer> observers = new LinkedHashSet<>();

    protected Sensor(String name, String unit, double initialReading) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Sensor name is required.");
        }
        requireFinite(initialReading);
        this.name = name;
        this.unit = Objects.requireNonNull(unit, "Unit is required.");
        reading = initialReading;
    }

    public String getName() { return name; }
    public String getUnit() { return unit; }
    public double getReading() { return reading; }
    public int getObserverCount() { return observers.size(); }

    public void setReading(double value) {
        requireFinite(value);
        if (Double.compare(reading, value) != 0) {
            reading = value;
            notifyObservers();
        }
    }

    @Override
    public void register(Observer observer) {
        observers.add(Objects.requireNonNull(observer, "Observer is required."));
    }

    @Override
    public void unregister(Observer observer) {
        observers.remove(Objects.requireNonNull(observer, "Observer is required."));
    }

    @Override
    public void notifyObservers() {
        // Registrations can change during a callback without corrupting iteration.
        for (Observer observer : List.copyOf(observers)) {
            observer.update(this);
        }
    }

    @Override
    public Sensor clone() {
        try {
            Sensor clone = (Sensor) super.clone();
            clone.observers = new LinkedHashSet<>();
            return clone;
        } catch (CloneNotSupportedException exception) {
            throw new AssertionError("Sensor implements Cloneable.", exception);
        }
    }

    private static void requireFinite(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Sensor reading must be finite.");
        }
    }
}
