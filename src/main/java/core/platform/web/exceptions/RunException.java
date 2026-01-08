package core.platform.web.exceptions;



public class RunException extends RuntimeException {
    public RunException(String message) {
        super(message);
    }

    public RunException(String message, Throwable cause) {
        super(message, cause);
    }
}
