package service;

/**
 * An exception used to indicate that input data failed a validation check.
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}