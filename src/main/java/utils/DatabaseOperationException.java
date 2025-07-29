package utils;

/**
 * Custom unchecked exception for DB operations.
 * Useful for wrapping SQL or JDBC errors into runtime exceptions.
 */
public class DatabaseOperationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DatabaseOperationException() {
        super();
    }

    public DatabaseOperationException(String message) {
        super(message);
    }

    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseOperationException(Throwable cause) {
        super(cause);
    }
}
