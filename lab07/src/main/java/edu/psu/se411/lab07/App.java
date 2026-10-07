package edu.psu.se411.lab07;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.psu.se411.lab07.exceptions.InvalidArgumentException;
import edu.psu.se411.lab07.exceptions.MissingInformationException;
import edu.psu.se411.lab07.model.*;
import edu.psu.se411.lab07.service.BookingService;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        LOGGER.info("Application is starting...");
        try {
            LocalDate date = LocalDate.of(2026, 12, 1);
            FlightBooking flight = new FlightBooking("F-01", "Khaled Abdou", date,
                    "Jeddah", new BigDecimal("500.00"));
            TrainBooking standard = new TrainBooking("T-01", "Khaled Abdou", date,
                    "Dammam", SeatClass.STANDARD);
            TrainBooking first = new TrainBooking("T-02", "Khaled Abdou", date,
                    "Dammam", SeatClass.FIRST_CLASS);
            CarRentalBooking car = new CarRentalBooking("C-01", "Khaled Abdou", date,
                    "Riyadh", new BigDecimal("100.00"));

            // Missing values must not silently default to zero.
            printPrice(flight);
            printPrice(standard);
            printPrice(car);
            demonstrateInvalidInput(() -> flight.setLuggageWeight(41));
            demonstrateInvalidInput(() -> standard.setDistanceKm(2001));
            demonstrateInvalidInput(() -> car.setRentalDays(31));

            flight.setLuggageWeight(10);
            standard.setDistanceKm(300);
            first.setDistanceKm(300);
            car.setRentalDays(3);
            for (Booking booking : List.of(flight, standard, first, car)) {
                printPrice(booking);
            }
        } finally {
            LOGGER.info("Application ends.");
        }
    }

    private static void printPrice(Booking booking) {
        try {
            BigDecimal price = BookingService.computeTotalPrice(booking);
            System.out.printf("%s (%s): %.2f%n", booking.getBookingId(),
                    booking.getClass().getSimpleName(), price);
        } catch (MissingInformationException exception) {
            LOGGER.error("Cannot price booking {}", booking.getBookingId(), exception);
            System.out.println("Expected missing information: " + exception.getMessage());
        }
    }

    private static void demonstrateInvalidInput(Runnable action) {
        try {
            action.run();
        } catch (InvalidArgumentException exception) {
            LOGGER.error("Invalid booking input", exception);
            System.out.println("Expected invalid input: " + exception.getMessage());
        }
    }
}
