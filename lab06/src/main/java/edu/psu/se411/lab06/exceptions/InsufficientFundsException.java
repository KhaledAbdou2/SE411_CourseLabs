package edu.psu.se411.lab06.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InsufficientFundsException extends Exception {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER =
            LoggerFactory.getLogger(InsufficientFundsException.class);

    public InsufficientFundsException(String message) {
        super(message);
        // Required by the handout; the handling boundary logs the stack trace.
        LOGGER.warn("InsufficientFundsException created: {}", message);
    }
}
