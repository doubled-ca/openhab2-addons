package org.openhab.binding.hdtvsupply.connection.event;

import org.openhab.binding.hdtvsupply.connection.HDTVSupplyConnection;

public class HDTVSupplyStatusUpdateEvent {

    private HDTVSupplyConnection connection;
    private String data;

    public HDTVSupplyStatusUpdateEvent(HDTVSupplyConnection connection, String data) {
        this.connection = connection;
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public HDTVSupplyConnection getConnection() {
        return connection;
    }
}
