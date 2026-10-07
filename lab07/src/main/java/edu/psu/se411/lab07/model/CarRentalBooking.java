package edu.psu.se411.lab07.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import edu.psu.se411.lab07.config.BookingConfig;
import edu.psu.se411.lab07.exceptions.MissingInformationException;

public class CarRentalBooking extends Booking {
    private final BigDecimal dailyRentalRate;
    private Integer rentalDays;

    public CarRentalBooking(String id, String customer, LocalDate date, String destination,
                            BigDecimal dailyRate) {
        super(id, customer, date, destination);
        dailyRentalRate = requirePrice(dailyRate, "Daily rental rate");
    }

    public void setRentalDays(int days) {
        requireRange(days, BookingConfig.MIN_RENTAL_DAYS,
                BookingConfig.MAX_RENTAL_DAYS, "Rental days");
        rentalDays = days;
    }

    @Override
    public BigDecimal calculateTotalPrice() throws MissingInformationException {
        if (rentalDays == null) {
            throw new MissingInformationException("Rental days are missing for " + getBookingId());
        }
        return dailyRentalRate.multiply(BigDecimal.valueOf(rentalDays));
    }
}
