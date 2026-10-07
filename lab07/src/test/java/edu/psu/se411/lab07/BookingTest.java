package edu.psu.se411.lab07;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import edu.psu.se411.lab07.exceptions.*;
import edu.psu.se411.lab07.model.*;
import edu.psu.se411.lab07.service.BookingService;

class BookingTest {
    private static final LocalDate DATE = LocalDate.of(2026, 12, 1);
    private FlightBooking flight() {
        return new FlightBooking("F", "Test Customer", DATE, "Jeddah", new BigDecimal("500"));
    }
    private TrainBooking train(SeatClass seat) {
        return new TrainBooking("T", "Test Customer", DATE, "Dammam", seat);
    }
    private CarRentalBooking car() {
        return new CarRentalBooking("C", "Test Customer", DATE, "Riyadh", new BigDecimal("100"));
    }
    private void price(String expected, Booking booking) throws MissingInformationException {
        assertEquals(0, new BigDecimal(expected).compareTo(BookingService.computeTotalPrice(booking)));
    }

    @Test
    void commonServiceUsesEachPricingImplementation() throws MissingInformationException {
        FlightBooking flight = flight(); flight.setLuggageWeight(10);
        TrainBooking standard = train(SeatClass.STANDARD); standard.setDistanceKm(300);
        TrainBooking first = train(SeatClass.FIRST_CLASS); first.setDistanceKm(300);
        CarRentalBooking car = car(); car.setRentalDays(3);
        Booking[] bookings = {flight, standard, first, car};
        String[] expected = {"650", "150", "300", "300"};
        for (int i = 0; i < bookings.length; i++) { price(expected[i], bookings[i]); }
    }

    @Test
    void missingLateEnteredValuesThrowCustomException() {
        for (Booking booking : new Booking[] {flight(), train(SeatClass.STANDARD), car()}) {
            assertThrows(MissingInformationException.class,
                    () -> BookingService.computeTotalPrice(booking));
        }
    }

    @Test
    void flightAcceptsBothLimitsAndRejectsInvalidWeights() throws MissingInformationException {
        FlightBooking flight = flight();
        flight.setLuggageWeight(0); price("500", flight);
        flight.setLuggageWeight(40); price("1100", flight);
        for (double value : new double[] {-0.1, 40.1, Double.NaN, Double.POSITIVE_INFINITY}) {
            assertThrows(InvalidArgumentException.class, () -> flight.setLuggageWeight(value));
            price("1100", flight);
        }
    }

    @Test
    void trainAcceptsBothLimitsForBothSeatClasses() throws MissingInformationException {
        TrainBooking standard = train(SeatClass.STANDARD);
        TrainBooking first = train(SeatClass.FIRST_CLASS);
        standard.setDistanceKm(1); price("0.50", standard);
        first.setDistanceKm(1); price("1.00", first);
        standard.setDistanceKm(2000); price("1000", standard);
        first.setDistanceKm(2000); price("2000", first);
        for (double value : new double[] {0, 2000.1, Double.NaN, Double.NEGATIVE_INFINITY}) {
            assertThrows(InvalidArgumentException.class, () -> standard.setDistanceKm(value));
            price("1000", standard);
        }
    }

    @Test
    void rentalDaysAcceptBothLimitsAndPreserveStateOnRejection() throws MissingInformationException {
        CarRentalBooking car = car();
        car.setRentalDays(1); price("100", car);
        car.setRentalDays(30); price("3000", car);
        for (int value : new int[] {0, -1, 31}) {
            assertThrows(InvalidArgumentException.class, () -> car.setRentalDays(value));
            price("3000", car);
        }
    }

    @Test
    void moneyUsesExactDecimalArithmetic() throws MissingInformationException {
        FlightBooking flight = new FlightBooking("F", "Test", DATE, "City", new BigDecimal("0.10"));
        flight.setLuggageWeight(0.1);
        price("1.60", flight);
        CarRentalBooking car = new CarRentalBooking("C", "Test", DATE, "City", new BigDecimal("0.10"));
        car.setRentalDays(3); price("0.30", car);
    }

    @Test
    void requiredConstructorInformationAndPricesAreValidated() {
        assertThrows(InvalidArgumentException.class, () -> new FlightBooking("", "Test", DATE, "City", BigDecimal.ONE));
        assertThrows(InvalidArgumentException.class, () -> new FlightBooking("F", " ", DATE, "City", BigDecimal.ONE));
        assertThrows(InvalidArgumentException.class, () -> new FlightBooking("F", "Test", null, "City", BigDecimal.ONE));
        assertThrows(InvalidArgumentException.class, () -> new FlightBooking("F", "Test", DATE, null, BigDecimal.ONE));
        assertThrows(InvalidArgumentException.class, () -> new FlightBooking("F", "Test", DATE, "City", null));
        assertThrows(InvalidArgumentException.class, () -> new FlightBooking("F", "Test", DATE, "City", new BigDecimal("-1")));
        assertThrows(InvalidArgumentException.class, () -> new CarRentalBooking("C", "Test", DATE, "City", new BigDecimal("-1")));
        assertThrows(InvalidArgumentException.class, () -> train(null));
        assertThrows(InvalidArgumentException.class, () -> BookingService.computeTotalPrice(null));
    }

    @Test
    void sharedBookingInformationIsStoredOnce() {
        Booking booking = flight();
        assertEquals("F", booking.getBookingId());
        assertEquals("Test Customer", booking.getCustomerFullName());
        assertEquals(DATE, booking.getTravelDate());
        assertEquals("Jeddah", booking.getDestinationCity());
    }
}
