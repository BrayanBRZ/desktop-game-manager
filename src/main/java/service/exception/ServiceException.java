package service.exception;

/**
 * A generic exception for the service layer, used to wrap errors
 * that occur during business logic execution, such as database failures.
 */
public class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }
}