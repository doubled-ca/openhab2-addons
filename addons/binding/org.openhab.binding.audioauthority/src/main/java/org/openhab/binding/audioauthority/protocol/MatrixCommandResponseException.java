package org.openhab.binding.audioauthority.protocol;

public class MatrixCommandResponseException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 272867634886998584L;

    public MatrixCommandResponseException() {
        super();
    }

    public MatrixCommandResponseException(String message) {
        super(message);
    }

    public MatrixCommandResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public MatrixCommandResponseException(Throwable cause) {
        super(cause);
    }
}
