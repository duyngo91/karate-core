package core.platform.exceptions;

public class TimeoutException extends RuntimeException {
    public TimeoutException(String operation, int timeout) {
        super(String.format("Operation '%s' timed out after %d milliseconds", operation, timeout));
    }
    
    public TimeoutException(String operation, int timeout, Throwable cause) {
        super(String.format("Operation '%s' timed out after %d milliseconds", operation, timeout), cause);
    }
}