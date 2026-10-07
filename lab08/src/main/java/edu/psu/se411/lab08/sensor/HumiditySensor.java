package edu.psu.se411.lab08.sensor;

public class HumiditySensor extends Sensor {
    public HumiditySensor(String name, double initialReading) {
        super(name, "%", initialReading);
    }

    @Override
    public HumiditySensor clone() {
        return (HumiditySensor) super.clone();
    }
}
