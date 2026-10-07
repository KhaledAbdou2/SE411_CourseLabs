package edu.psu.se411.lab08.sensor;

public class TemperatureSensor extends Sensor {
    public TemperatureSensor(String name, double initialReading) {
        super(name, "C", initialReading);
    }

    @Override
    public TemperatureSensor clone() {
        return (TemperatureSensor) super.clone();
    }
}
