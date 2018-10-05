package org.openhab.binding.audioauthoritysonaflex.internal.connection.event;

public interface MatrixUpdateListener {

    void statusUpdateReceived(MatrixStatusUpdateEvent event);
}
