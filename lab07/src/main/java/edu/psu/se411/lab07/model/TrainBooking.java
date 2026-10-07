package edu.psu.se411.lab07.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import edu.psu.se411.lab07.config.BookingConfig;
import edu.psu.se411.lab07.exceptions.InvalidArgumentException;
import edu.psu.se411.lab07.exceptions.MissingInformationException;

public class TrainBooking extends Booking {
    private final SeatClass seatClass;
    private Double distanceKm;

    public TrainBooking(String id, String customer, LocalDate date, String destination,
                        SeatClass seatClass) {
        super(id, customer, date, destination);
        if (seatClass == null) {
            throw new InvalidArgumentException("Seat class is required.");
        }
        this.seatClass = seatClass;
    }

    public void setDistanceKm(double distance) {
        requireRange(distance, BookingConfig.MIN_TRAIN_DISTANCE,
                BookingConfig.MAX_TRAIN_DISTANCE, "Train distance");
        distanceKm = distance;
    }

    @Override
    public BigDecimal calculateTotalPrice() throws MissingInformationException {
        if (distanceKm == null) {
            throw new MissingInformationException("Train distance is missing for " + getBookingId());
        }
        BigDecimal rate = seatClass == SeatClass.STANDARD
                ? BookingConfig.TRAIN_STANDARD_RATE : BookingConfig.TRAIN_FIRST_CLASS_RATE;
        return BigDecimal.valueOf(distanceKm).multiply(rate);
    }
}
