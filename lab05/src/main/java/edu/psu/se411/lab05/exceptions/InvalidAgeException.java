package edu.psu.se411.lab05.exceptions;

/** Indicates that an age does not meet the minimum age requirement. */
public class InvalidAgeException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidAgeException(String message) {
        super(message);
    }
}
