package edu.psu.se411.lab08.observer;

import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.psu.se411.lab08.sensor.Sensor;

public class LoggingObserver extends SensorObserver {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingObserver.class);

    public LoggingObserver(String name) {
        super(name);
    }

    @Override
    protected void onReading(Sensor sensor) {
        System.out.printf(Locale.ROOT, "Logger[%s] %s: %.2f %s%n",
                getName(), sensor.getName(), sensor.getReading(), sensor.getUnit());
        LOGGER.info("Observer={} sensor={} reading={} {}",
                getName(), sensor.getName(), sensor.getReading(), sensor.getUnit());
    }
}
