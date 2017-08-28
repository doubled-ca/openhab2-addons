package org.openhab.binding.hdtvsupply.connection.event;

import org.openhab.binding.hdtvsupply.connection.HDTVSupplyConnection;

public class HDTVSupplyDisconnectionEvent {

    private Throwable cause;
    private HDTVSupplyConnection connection;

    public HDTVSupplyDisconnectionEvent(HDTVSupplyConnection connection, Throwable cause) {
        this.connection = connection;
        this.cause = cause;
    }

    public Throwable getCause() {
        return cause;
    }

    public HDTVSupplyConnection getConnection() {
        return connection;
    }

}
