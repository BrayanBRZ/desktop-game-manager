package service.exception;

/**
 * An exception used to indicate that input data failed a validation check.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
