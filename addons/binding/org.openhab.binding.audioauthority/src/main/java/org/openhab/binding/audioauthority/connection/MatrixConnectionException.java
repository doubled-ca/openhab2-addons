package org.openhab.binding.audioauthority.connection;

public class MatrixConnectionException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 7193480817515471879L;

    public MatrixConnectionException() {
        super();
    }

    public MatrixConnectionException(String message) {
        super(message);
    }

    public MatrixConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public MatrixConnectionException(Throwable cause) {
        super(cause);
    }
}
