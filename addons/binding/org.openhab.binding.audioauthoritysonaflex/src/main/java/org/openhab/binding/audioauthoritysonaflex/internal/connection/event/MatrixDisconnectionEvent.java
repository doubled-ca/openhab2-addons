package org.openhab.binding.audioauthoritysonaflex.internal.connection.event;

import org.openhab.binding.audioauthoritysonaflex.internal.connection.MatrixConnection;

public class MatrixDisconnectionEvent {

    private MatrixConnection connection;
    private Throwable cause;

    public MatrixDisconnectionEvent(MatrixConnection connection, Throwable cause) {
        this.connection = connection;
        this.cause = cause;
    }

    public MatrixConnection getConnection() {
        return connection;
    }

    public Throwable getCause() {
        return cause;
    }
}
