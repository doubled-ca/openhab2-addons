package org.openhab.binding.audioauthority.connection.event;

import org.openhab.binding.audioauthority.connection.MatrixConnection;

public class MatrixStatusUpdateEvent {

    private MatrixConnection connection;
    private String data;

    public MatrixStatusUpdateEvent(MatrixConnection connection, String data) {
        this.connection = connection;
        this.data = data;
    }

    public MatrixConnection getConnection() {
        return connection;
    }

    public String getData() {
        return data;
    }

}
