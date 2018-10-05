package org.openhab.binding.audioauthoritysonaflex.internal.connection;

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.audioauthoritysonaflex.internal.connection.event.MatrixDisconnectionListener;
import org.openhab.binding.audioauthoritysonaflex.internal.connection.event.MatrixUpdateListener;

public interface MatrixConnection {

    /**
     * Add an update listener. It is notified when an update is received from the AVR.
     *
     * @param listener
     */
    public void addUpdateListener(MatrixUpdateListener listener);

    /**
     * Add a disconnection listener. It is notified when the AVR is disconnected.
     *
     * @param listener
     */
    public void addDisconnectionListener(MatrixDisconnectionListener listener);

    /**
     * Query an Audio Zone Settings
     *
     * @param which zone to get information for
     * @return List of settings for the zone
     */
    public boolean sendZoneQuery(ChannelUID channelUID, int zone);

    /**
     * Send a Power command to a zone
     *
     * @param command
     * @return
     * @throws CommandTypeNotSupportedException
     */
    public boolean sendPowerCommand(Command command, ChannelUID channelUID) throws MatrixCommandResponseException;

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

    boolean sendVolumeCommand(Command command, ChannelUID channelUID) throws MatrixCommandResponseException;

    public boolean sendMuteCommand(Command command, ChannelUID channelUID) throws MatrixCommandResponseException;

    public boolean sendInputSourceCommand(Command command, ChannelUID channelUID) throws MatrixCommandResponseException;

    public boolean sendZoneQuery(int i, int zone);

    public boolean sendNameCommand(Command command, ChannelUID channelUID) throws MatrixCommandResponseException;

    int getUnitNumber();

}
