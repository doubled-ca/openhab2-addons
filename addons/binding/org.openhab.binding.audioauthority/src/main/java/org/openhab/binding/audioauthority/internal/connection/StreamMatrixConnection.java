package org.openhab.binding.audioauthority.internal.connection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.DatatypeConverter;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.IncreaseDecreaseType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.audioauthority.AudioAuthorityBindingConstants;
import org.openhab.binding.audioauthority.connection.MatrixConnection;
import org.openhab.binding.audioauthority.connection.event.MatrixDisconnectionEvent;
import org.openhab.binding.audioauthority.connection.event.MatrixDisconnectionListener;
import org.openhab.binding.audioauthority.connection.event.MatrixStatusUpdateEvent;
import org.openhab.binding.audioauthority.connection.event.MatrixUpdateListener;
import org.openhab.binding.audioauthority.internal.protocol.RequestResponseFactory;
import org.openhab.binding.audioauthority.internal.protocol.VolumeConverter;
import org.openhab.binding.audioauthority.protocol.MatrixCommand;
import org.openhab.binding.audioauthority.protocol.MatrixCommandResponseException;
import org.openhab.binding.audioauthority.protocol.states.MuteStateValues;
import org.openhab.binding.audioauthority.protocol.states.PowerStateValues;
import org.openhab.binding.audioauthority.protocol.states.VolumeUpDownStateValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class StreamMatrixConnection implements MatrixConnection {

    private static final Logger logger = LoggerFactory.getLogger(StreamMatrixConnection.class);

    // The maximum time to wait incoming messages.
    private static final Integer READ_TIMEOUT = 1000;

    protected int unitNumber = AudioAuthorityBindingConstants.DEFAULT_UNIT_NUMBER;

    private List<MatrixUpdateListener> updateListeners;
    private List<MatrixDisconnectionListener> disconnectionListeners;

    private ControlMatrixInputStreamReader inputStreamReader;
    private DataOutputStream outputStream;

    public StreamMatrixConnection() {
        this.updateListeners = new ArrayList<>();
        this.disconnectionListeners = new ArrayList<>();
    }

    @Override
    public void addUpdateListener(MatrixUpdateListener listener) {
        synchronized (updateListeners) {
            updateListeners.add(listener);
        }
    }

    @Override
    public void addDisconnectionListener(MatrixDisconnectionListener listener) {
        synchronized (disconnectionListeners) {
            disconnectionListeners.add(listener);
        }
    }

    @Override
    public boolean connect() {

        if (!isConnected()) {
            doConnection();
        }
        return isConnected();
    }

    /**
     * Open the connection to the AVR.
     *
     * @throws IOException
     */
    protected abstract void openConnection() throws IOException;

    /**
     * Reconnect Socket when remote socket closes gracefully
     *
     * @throws IOException
     */
    @Override
    public void reConnect() {
        doConnection();
    }

    private void doConnection() {
        try {
            openConnection();

            // Start the inputStream reader.
            inputStreamReader = new ControlMatrixInputStreamReader(getInputStream());
            inputStreamReader.start();

            // Get Output stream
            outputStream = new DataOutputStream(getOutputStream());

        } catch (IOException ioException) {
            logger.debug("Can't connect to {}. Cause: {}", getConnectionName(), ioException.getMessage());
        }
    }

    /**
     * Return the inputStream to read responses.
     *
     * @return
     * @throws IOException
     */
    protected abstract InputStream getInputStream() throws IOException;

    /**
     * Return the outputStream to send commands.
     *
     * @return
     * @throws IOException
     */
    protected abstract OutputStream getOutputStream() throws IOException;

    @Override
    public void close() {
        if (inputStreamReader != null) {
            // This method block until the reader is really stopped.
            inputStreamReader.stopReader();
            inputStreamReader = null;
            logger.debug("Stream reader stopped!");
        }
    }

    /**
     * Sends to command to the receiver. It does not wait for a reply.
     *
     * @param MatrixCommand
     *            the command to send.
     **/
    protected boolean sendMatrixCommand(MatrixCommand commandToSend) {

        String command = commandToSend.getCommand();
        command = command.replaceFirst("UNITNUM__", "U" + unitNumber);

        return sendCommand(command);
    }

    /**
     * @param command
     */
    private boolean sendCommand(String command) {
        boolean isSent = false;
        if (connect()) {

            try {
                if (logger.isTraceEnabled()) {
                    logger.trace("Sending {} bytes: {}", command.length(),
                            DatatypeConverter.printHexBinary(command.getBytes()));
                }
                outputStream.writeBytes(command);
                outputStream.flush();
                isSent = true;
            } catch (IOException ioException) {
                logger.error("Error occured when sending command", ioException);

                // log and send disconnection event to update thing status.
                logger.warn("The AudioAuthority Matrix @{} is disconnected.", getConnectionName(), ioException);
                MatrixDisconnectionEvent event = new MatrixDisconnectionEvent(StreamMatrixConnection.this, ioException);
                for (MatrixDisconnectionListener audioMatrixDisconnectionListener : disconnectionListeners) {
                    audioMatrixDisconnectionListener.onDisconnection(event);
                }

                // close connection on error
                close();
            }

            logger.debug("Command sent to Audio Authority Matrix @{}: {}", getConnectionName(), command);
        }

        return isSent;
    }

    // used to send command without activating from a channel
    protected boolean sendRawCommand(String commandToSend) {
        String command = AudioAuthorityBindingConstants.COMMAND_PREFIX + commandToSend
                + AudioAuthorityBindingConstants.COMMAND_SUFFIX;

        return sendCommand(command);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.audioauthority.communication.MatrixConnection#sendZoneQuery(java.lang.String)
     */
    @Override
    public boolean sendZoneQuery(ChannelUID channelUID, int zone) {

        return sendRawCommand("U1O" + zone + "Q");
    }

    @Override
    public boolean sendZoneQuery(int unit, int zone) {
        String rawCommand = "U" + unit + "O" + zone + "Q";
        return sendRawCommand(rawCommand);
    }

    @Override
    public boolean sendVolumeCommand(Command command, ChannelUID channelUID) throws MatrixCommandResponseException {
        // boolean commandSent = false;
        MatrixCommand commandToSend = null;

        if (command == OnOffType.ON) {
            String ipControlVolume = VolumeConverter.convertFromPercentToIpControlVolume(100d);
            commandToSend = RequestResponseFactory.getIpControlCommand(channelUID, ipControlVolume);
        } else if (command == OnOffType.OFF) {
            String ipControlVolume = VolumeConverter.convertFromPercentToIpControlVolume(0d);
            commandToSend = RequestResponseFactory.getIpControlCommand(channelUID, ipControlVolume);
        } else if (command == IncreaseDecreaseType.DECREASE) {
            commandToSend = RequestResponseFactory.getIpControlCommand(channelUID, VolumeUpDownStateValues.DOWN_VALUE);
        } else if (command == IncreaseDecreaseType.INCREASE) {
            commandToSend = RequestResponseFactory.getIpControlCommand(channelUID, VolumeUpDownStateValues.UP_VALUE);
        } else if (command instanceof PercentType) {
            String ipControlVolume = VolumeConverter
                    .convertFromPercentToIpControlVolume(((PercentType) command).doubleValue());
            commandToSend = RequestResponseFactory.getIpControlCommand(channelUID, ipControlVolume);
        } else if (command instanceof DecimalType) {
            String ipControlVolume = VolumeConverter
                    .convertFromDbToIpControlVolume(((DecimalType) command).doubleValue());
            commandToSend = RequestResponseFactory.getIpControlCommand(channelUID, ipControlVolume);
        } else {
            throw new MatrixCommandResponseException("Command type not supported.");
        }

        return sendMatrixCommand(commandToSend);
    }

    @Override
    public boolean sendMuteCommand(Command command, ChannelUID channelUID) throws MatrixCommandResponseException {
        MatrixCommand commandToSend = null;

        if (command == OnOffType.ON) {
            commandToSend = RequestResponseFactory.getIpControlCommand(channelUID, MuteStateValues.ON_VALUE);
        } else if (command == OnOffType.OFF) {
            commandToSend = RequestResponseFactory.getIpControlCommand(channelUID, MuteStateValues.OFF_VALUE);
        } else {
            throw new MatrixCommandResponseException("Command type not supported.");
        }

        return sendMatrixCommand(commandToSend);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openhab.binding.audioauthority.communication.MatrixConnection#sendPowerCommand(org.eclipse.smarthome.core.
     * types.Command)
     */
    @Override
    public boolean sendPowerCommand(Command command, ChannelUID channelUID) throws MatrixCommandResponseException {
        MatrixCommand commandToSend = null;

        if (command == OnOffType.OFF) {
            commandToSend = RequestResponseFactory.getIpControlCommand(channelUID, PowerStateValues.OFF_VALUE);
        } else if (command == OnOffType.ON) {
            commandToSend = RequestResponseFactory.getIpControlCommand(channelUID, PowerStateValues.ON_VALUE);
        } else {
            throw new MatrixCommandResponseException("Command type not supported for Power Command.");
        }

        return sendMatrixCommand(commandToSend);
    }

    @Override
    public boolean sendNameCommand(Command command, ChannelUID channelUID) throws MatrixCommandResponseException {
        MatrixCommand commandToSend = null;

        if (command instanceof StringType) {
            commandToSend = RequestResponseFactory.getIpControlCommand(channelUID,
                    ((StringType) command).format("\"%.16s\""));
        } else {
            throw new MatrixCommandResponseException("Command type not supported for Name Command");
        }

        return sendMatrixCommand(commandToSend);
    }

    @Override
    public boolean sendInputSourceCommand(Command command, ChannelUID channelUID)
            throws MatrixCommandResponseException {
        MatrixCommand commandToSend = null;

        if (command instanceof StringType || command instanceof DecimalType) {
            commandToSend = RequestResponseFactory.getIpControlCommand(channelUID, command.toString());
        } else {
            throw new MatrixCommandResponseException("Command type not supported for Input Switch Command");
        }

        return sendMatrixCommand(commandToSend);
    }

    @Override
    public int getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(int unitNumber) {
        this.unitNumber = unitNumber;
    }

    private class ControlMatrixInputStreamReader extends Thread {

        private BufferedReader bufferedReader = null;

        private volatile boolean stopReader;

        // This latch is used to block the stop method until the reader is really stopped.
        private CountDownLatch stopLatch;

        /**
         * Construct a reader that read the given inputStream
         *
         * @param ipControlSocket
         * @throws IOException
         */
        public ControlMatrixInputStreamReader(InputStream inputStream) {
            this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            this.stopLatch = new CountDownLatch(1);

            this.setDaemon(true);
            this.setName("ControlMatrixInputStreamReader-" + getConnectionName());
        }

        @Override
        public void run() {
            try {

                while (!stopReader && !Thread.currentThread().isInterrupted()) {

                    String receivedData = null;
                    try {
                        receivedData = bufferedReader.readLine();
                    } catch (SocketTimeoutException e) {
                        // Do nothing. Just happen to allow the thread to check if it has to stop.
                    }

                    if (receivedData != null) {
                        logger.debug("Data received from AudioAuthority Matrix @{}: {}", getConnectionName(),
                                receivedData);
                        MatrixStatusUpdateEvent event = new MatrixStatusUpdateEvent(StreamMatrixConnection.this,
                                receivedData);
                        synchronized (updateListeners) {
                            for (MatrixUpdateListener audioMatrixEventListener : updateListeners) {
                                audioMatrixEventListener.statusUpdateReceived(event);
                            }
                        }
                    }
                }

            } catch (IOException e) {
                logger.warn("The AudioAuthority Matrix @{} is disconnected.", getConnectionName(), e);

                MatrixDisconnectionEvent event = new MatrixDisconnectionEvent(StreamMatrixConnection.this, e);
                for (MatrixDisconnectionListener audioMatrixDisconnectionListener : disconnectionListeners) {
                    audioMatrixDisconnectionListener.onDisconnection(event);
                }

            }

            // Notify the stopReader method caller that the reader is stopped.
            this.stopLatch.countDown();
        }

        /**
         * Stop this reader. Block until the reader is really stopped.
         */
        public void stopReader() {
            this.stopReader = true;
            try {
                this.stopLatch.await(READ_TIMEOUT, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                // Do nothing. The timeout is just here for safety and to be sure that the call to this method will not
                // block the caller indefinitely.
                // This exception should never happen.
            }
        }

    }

}
