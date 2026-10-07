package edu.psu.se411.lab07.service;

import java.math.BigDecimal;
import edu.psu.se411.lab07.model.Booking;
import edu.psu.se411.lab07.exceptions.InvalidArgumentException;
import edu.psu.se411.lab07.exceptions.MissingInformationException;

public final class BookingService {
    private BookingService() {
    }

    public static BigDecimal computeTotalPrice(Booking booking) throws MissingInformationException {
        if (booking == null) {
            throw new InvalidArgumentException("Booking is required.");
        }
        // Dynamic dispatch: no casts or booking-type checks are needed.
        return booking.calculateTotalPrice();
    }
}
