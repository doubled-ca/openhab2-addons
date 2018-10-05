package org.openhab.binding.audioauthoritysonaflex.internal.connection;

public class MatrixConnectionException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -1174552685641708318L;

    /**
     *
     */

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
