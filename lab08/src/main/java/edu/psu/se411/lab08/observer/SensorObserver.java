package edu.psu.se411.lab08.observer;

import edu.psu.se411.lab08.sensor.Sensor;

/** Reuses observer naming and subject validation for both observer implementations. */
public abstract class SensorObserver implements Observer {
    private final String name;

    protected SensorObserver(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Observer name is required.");
        }
        this.name = name;
    }

    protected String getName() { return name; }

    @Override
    public final void update(Subject subject) {
        if (!(subject instanceof Sensor sensor)) {
            throw new IllegalArgumentException("This observer requires a Sensor subject.");
        }
        onReading(sensor);
    }

    protected abstract void onReading(Sensor sensor);
}
