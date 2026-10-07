package edu.psu.se411.lab08.observer;

import java.util.Locale;
import edu.psu.se411.lab08.sensor.Sensor;

public class DashboardObserver extends SensorObserver {
    public DashboardObserver(String name) {
        super(name);
    }

    @Override
    protected void onReading(Sensor sensor) {
        System.out.printf(Locale.ROOT, "Dashboard[%s] %s: %.2f %s%n",
                getName(), sensor.getName(), sensor.getReading(), sensor.getUnit());
    }
}
