package edu.psu.se411.lab08;

import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import edu.psu.se411.lab08.observer.*;
import edu.psu.se411.lab08.sensor.*;

class SensorTest {
    @Test
    void observersReceiveBothSensorsAndCurrentReadings() {
        TemperatureSensor temp = new TemperatureSensor("Temperature", 20);
        HumiditySensor humidity = new HumiditySensor("Humidity", 40);
        List<String> first = new ArrayList<>();
        List<String> second = new ArrayList<>();
        Observer a = subject -> first.add(((Sensor) subject).getName() + ":" + ((Sensor) subject).getReading());
        Observer b = subject -> second.add(((Sensor) subject).getName() + ":" + ((Sensor) subject).getReading());
        for (Subject subject : new Subject[] {temp, humidity}) {
            subject.register(a); subject.register(b);
        }
        temp.setReading(25); humidity.setReading(50);
        assertEquals(List.of("Temperature:25.0", "Humidity:50.0"), first);
        assertEquals(first, second);
    }

    @Test
    void unchangedReadingDoesNotNotifyButExplicitNotifyDoes() {
        Sensor sensor = new TemperatureSensor("Temperature", 20);
        AtomicInteger calls = new AtomicInteger();
        sensor.register(subject -> calls.incrementAndGet());
        sensor.setReading(20); assertEquals(0, calls.get());
        sensor.setReading(21); assertEquals(1, calls.get());
        sensor.notifyObservers(); assertEquals(2, calls.get());
    }

    @Test
    void unregisterStopsOnlyThatObserverAndIsIdempotent() {
        Sensor sensor = new HumiditySensor("Humidity", 40);
        AtomicInteger first = new AtomicInteger();
        AtomicInteger second = new AtomicInteger();
        Observer a = subject -> first.incrementAndGet();
        sensor.register(a);
        sensor.register(subject -> second.incrementAndGet());
        sensor.setReading(41);
        sensor.unregister(a); sensor.unregister(a);
        sensor.setReading(42);
        assertEquals(1, first.get()); assertEquals(2, second.get());
    }

    @Test
    void duplicateRegistrationNotifiesOnce() {
        Sensor sensor = new TemperatureSensor("Temperature", 20);
        AtomicInteger calls = new AtomicInteger();
        Observer observer = subject -> calls.incrementAndGet();
        sensor.register(observer); sensor.register(observer);
        sensor.setReading(21);
        assertEquals(1, calls.get()); assertEquals(1, sensor.getObserverCount());
    }

    @Test
    void cloneStartsUnobservedAndHasIndependentStateAndRegistrations() {
        TemperatureSensor original = new TemperatureSensor("Temperature", 20);
        AtomicInteger originalCalls = new AtomicInteger();
        AtomicInteger cloneCalls = new AtomicInteger();
        original.register(subject -> originalCalls.incrementAndGet());
        TemperatureSensor clone = original.clone();
        assertNotSame(original, clone);
        assertEquals(original.getName(), clone.getName());
        assertEquals(original.getReading(), clone.getReading());
        assertEquals(0, clone.getObserverCount());
        assertEquals(1, original.getObserverCount());
        clone.setReading(25);
        assertEquals(0, originalCalls.get()); assertEquals(20, original.getReading());
        Observer cloneObserver = subject -> cloneCalls.incrementAndGet();
        clone.register(cloneObserver);
        original.setReading(21);
        assertEquals(1, originalCalls.get()); assertEquals(0, cloneCalls.get());
        clone.setReading(26);
        assertEquals(1, originalCalls.get()); assertEquals(1, cloneCalls.get());
        clone.unregister(cloneObserver);
        assertEquals(1, original.getObserverCount());
    }

    @Test
    void humidityClonePreservesTypeAndValueWithoutObservers() {
        HumiditySensor original = new HumiditySensor("Humidity", 55);
        original.register(subject -> fail("Original observer must not receive clone updates."));
        HumiditySensor clone = original.clone();
        assertEquals(HumiditySensor.class, clone.getClass());
        assertEquals("%", clone.getUnit()); assertEquals(55, clone.getReading());
        clone.setReading(60);
        assertEquals(55, original.getReading()); assertEquals(0, clone.getObserverCount());
    }

    @Test
    void callbacksCanChangeRegistrationWithoutBreakingCurrentNotification() {
        Sensor sensor = new TemperatureSensor("Temperature", 20);
        AtomicInteger laterCalls = new AtomicInteger();
        Observer later = subject -> laterCalls.incrementAndGet();
        Observer[] self = new Observer[1];
        self[0] = subject -> { subject.unregister(self[0]); subject.register(later); };
        sensor.register(self[0]);
        sensor.setReading(21);
        assertEquals(0, laterCalls.get());
        sensor.setReading(22);
        assertEquals(1, laterCalls.get());
    }

    @Test
    void invalidReadingsDoNotMutateOrNotify() {
        Sensor sensor = new TemperatureSensor("Temperature", 20);
        sensor.register(subject -> fail("Invalid reading must not notify."));
        for (double value : new double[] {Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> sensor.setReading(value));
            assertEquals(20, sensor.getReading());
        }
        assertThrows(IllegalArgumentException.class, () -> new HumiditySensor("Humidity", Double.NaN));
    }

    @Test
    void dashboardAndLoggerPrintBothSensorTypes() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try (PrintStream capture = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            System.setOut(capture);
            Sensor temp = new TemperatureSensor("Temperature", 20);
            Sensor humidity = new HumiditySensor("Humidity", 40);
            for (Sensor sensor : new Sensor[] {temp, humidity}) {
                sensor.register(new DashboardObserver("Test"));
                sensor.register(new LoggingObserver("Test"));
            }
            temp.setReading(25); humidity.setReading(50);
        } finally {
            System.setOut(originalOut);
        }
        String text = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(text.contains("Dashboard[Test] Temperature: 25.00 C"));
        assertTrue(text.contains("Logger[Test] Temperature: 25.00 C"));
        assertTrue(text.contains("Dashboard[Test] Humidity: 50.00 %"));
        assertTrue(text.contains("Logger[Test] Humidity: 50.00 %"));
    }

    @Test
    void requiredNamesAndObserversAreValidated() {
        Sensor sensor = new TemperatureSensor("Temperature", 20);
        assertThrows(NullPointerException.class, () -> sensor.register(null));
        assertThrows(NullPointerException.class, () -> sensor.unregister(null));
        assertThrows(IllegalArgumentException.class, () -> new TemperatureSensor(" ", 20));
        assertThrows(IllegalArgumentException.class, () -> new DashboardObserver(null));
        assertThrows(IllegalArgumentException.class, () -> new LoggingObserver(" "));
    }
}
