package org.openhab.binding.hdtvsupply.connection.event;

public interface HDTVSupplyUpdateListener {

    void statusUpdateReceived(HDTVSupplyStatusUpdateEvent statusUpdateEvent);
}
