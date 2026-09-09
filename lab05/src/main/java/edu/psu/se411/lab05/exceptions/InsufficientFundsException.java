package edu.psu.se411.lab05.exceptions;

/** Indicates that a withdrawal exceeds the available wallet balance. */
public class InsufficientFundsException extends Exception {
    private static final long serialVersionUID = 1L;

    public InsufficientFundsException(String message) {
        super(message);
    }
}
