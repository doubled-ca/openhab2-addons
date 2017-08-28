package org.openhab.binding.hdtvsupply.connection;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.hdtvsupply.connection.event.HDTVSupplyDisconnectionListener;
import org.openhab.binding.hdtvsupply.connection.event.HDTVSupplyUpdateListener;

public interface HDTVSupplyConnection {

    /**
     * Add an update listener. It is notified when an update is received from the AVR.
     *
     * @param listener
     */
    public void addUpdateListener(HDTVSupplyUpdateListener listener);

    /**
     * Add a disconnection listener. It is notified when the AVR is disconnected.
     *
     * @param listener
     */
    public void addDisconnectionListener(HDTVSupplyDisconnectionListener listener);

    /**
     * Return the connection name
     *
     * @return
     */
    public String getConnectionName();

    /**
     * Connect to the Matrix. Return true if the connection has succeeded or if already connected.
     *
     **/
    public boolean connect();

    /**
     * Reconnect socket after other end closes gracefully. Used after testing status of socket with an attempted
     * read/write and getting an Exception.
     **/
    public void reConnect();

    /**
     * Return true if this manager is connected to the Matrix.
     *
     * @return
     */
    public boolean isConnected();

    /**
     * Closes the connection.
     **/
    public void close();

    /**
     * Switch Source Input for this Output
     *
     * @param command
     * @param channelUID
     * @return true if command sent successfully.
     */
    public boolean sendOutputSwitchInputCommand(Command command, @NonNull ChannelUID channelUID);

    /**
     * Change the EDID for this Input Source
     * 
     * @param command
     * @param channelUID
     * @return true if command sent successfully.
     */
    public boolean sendInputSetEDID(Command command, @NonNull ChannelUID channelUID);

}
