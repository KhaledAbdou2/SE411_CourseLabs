package edu.psu.se411.lab07.config;

import java.math.BigDecimal;

/** Example rates: the handout specifies the formulas but not numeric rates. */
public final class BookingConfig {
    public static final BigDecimal EXTRA_LUGGAGE_RATE = new BigDecimal("15.00");
    public static final BigDecimal TRAIN_STANDARD_RATE = new BigDecimal("0.50");
    public static final BigDecimal TRAIN_FIRST_CLASS_RATE = new BigDecimal("1.00");
    public static final int MAX_LUGGAGE_WEIGHT = 40;
    public static final int MIN_TRAIN_DISTANCE = 1;
    public static final int MAX_TRAIN_DISTANCE = 2000;
    public static final int MIN_RENTAL_DAYS = 1;
    public static final int MAX_RENTAL_DAYS = 30;

    private BookingConfig() {
    }
}
