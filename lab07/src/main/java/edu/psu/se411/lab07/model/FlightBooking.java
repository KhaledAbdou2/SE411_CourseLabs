package edu.psu.se411.lab07.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import edu.psu.se411.lab07.config.BookingConfig;
import edu.psu.se411.lab07.exceptions.MissingInformationException;

public class FlightBooking extends Booking {
    private final BigDecimal baseTicketPrice;
    private Double luggageWeight;

    public FlightBooking(String id, String customer, LocalDate date, String destination,
                         BigDecimal basePrice) {
        super(id, customer, date, destination);
        baseTicketPrice = requirePrice(basePrice, "Base ticket price");
    }

    public void setLuggageWeight(double weight) {
        requireRange(weight, 0, BookingConfig.MAX_LUGGAGE_WEIGHT, "Luggage weight");
        luggageWeight = weight;
    }

    @Override
    public BigDecimal calculateTotalPrice() throws MissingInformationException {
        if (luggageWeight == null) {
            throw new MissingInformationException("Luggage weight is missing for " + getBookingId());
        }
        return baseTicketPrice.add(BigDecimal.valueOf(luggageWeight)
                .multiply(BookingConfig.EXTRA_LUGGAGE_RATE));
    }
}
