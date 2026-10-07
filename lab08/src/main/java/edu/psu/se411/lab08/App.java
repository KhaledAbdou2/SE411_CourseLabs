package edu.psu.se411.lab08;

import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.psu.se411.lab08.observer.*;
import edu.psu.se411.lab08.sensor.*;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        LOGGER.info("Application is starting...");
        try {
            TemperatureSensor temp = new TemperatureSensor("Temperature", 20);
            HumiditySensor humidity = new HumiditySensor("Humidity", 40);
            DashboardObserver dashboard = new DashboardObserver("Main dashboard");
            LoggingObserver logger = new LoggingObserver("Main logger");
            for (Subject subject : new Subject[] {temp, humidity}) {
                subject.register(dashboard);
                subject.register(logger);
            }

            Random random = new Random(411);
            long delayMillis = args.length > 0 && "--fast".equals(args[0]) ? 0 : 1000;
            for (int i = 0; i < 10; i++) {
                temp.setReading(20 + random.nextDouble() * 15);
                humidity.setReading(40 + random.nextDouble() * 20);
                try {
                    if (i < 9) {
                        Thread.sleep(delayMillis);
                    }
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    LOGGER.warn("Sensor simulation interrupted.", exception);
                    return;
                }
            }

            TemperatureSensor clone = temp.clone();
            System.out.printf("Clone observers: %d; original observers: %d%n",
                    clone.getObserverCount(), temp.getObserverCount());
            double originalReading = temp.getReading();
            clone.setReading(10); // No notifications: the clone has no observers yet.
            clone.register(new DashboardObserver("Clone dashboard"));
            clone.register(new LoggingObserver("Clone logger"));
            clone.setReading(11);
            System.out.println("Original reading unchanged: " + (temp.getReading() == originalReading));

            humidity.unregister(dashboard);
            System.out.println("After unregister: only the logger receives this humidity update.");
            humidity.setReading(35);
        } finally {
            LOGGER.info("Application ends.");
        }
    }
}
