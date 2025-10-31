package service;

/**
 * A generic exception for the service layer, used to wrap errors
 * that occur during business logic execution, such as database failures.
 */
public class ServiceException extends RuntimeException {
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}