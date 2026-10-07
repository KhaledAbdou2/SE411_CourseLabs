package edu.psu.se411.lab07.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import edu.psu.se411.lab07.exceptions.InvalidArgumentException;
import edu.psu.se411.lab07.exceptions.MissingInformationException;

public abstract class Booking {
    private final String bookingId;
    private final String customerFullName;
    private final LocalDate travelDate;
    private final String destinationCity;

    protected Booking(String id, String customer, LocalDate date, String destination) {
        bookingId = requireText(id, "Booking ID");
        customerFullName = requireText(customer, "Customer name");
        if (date == null) {
            throw new InvalidArgumentException("Travel date is required.");
        }
        travelDate = date;
        destinationCity = requireText(destination, "Destination city");
    }

    public String getBookingId() { return bookingId; }
    public String getCustomerFullName() { return customerFullName; }
    public LocalDate getTravelDate() { return travelDate; }
    public String getDestinationCity() { return destinationCity; }

    public abstract BigDecimal calculateTotalPrice() throws MissingInformationException;

    protected static void requireRange(double value, double min, double max, String field) {
        if (!Double.isFinite(value) || value < min || value > max) {
            throw new InvalidArgumentException(field + " must be between " + min + " and " + max + ".");
        }
    }

    protected static BigDecimal requirePrice(BigDecimal price, String field) {
        if (price == null || price.signum() < 0) {
            throw new InvalidArgumentException(field + " must be provided and nonnegative.");
        }
        return price;
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidArgumentException(field + " is required.");
        }
        return value.trim();
    }
}
