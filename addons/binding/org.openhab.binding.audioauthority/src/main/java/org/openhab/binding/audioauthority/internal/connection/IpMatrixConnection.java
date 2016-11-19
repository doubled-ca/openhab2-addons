package org.openhab.binding.audioauthority.internal.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.openhab.binding.audioauthority.AudioAuthorityBindingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpMatrixConnection extends StreamMatrixConnection {

    private static final Logger logger = LoggerFactory.getLogger(IpMatrixConnection.class);

    /** default port for IP communication **/
    public static final int DEFAULT_IPCONTROL_PORT = 23;

    /** Connection timeout in milliseconds **/
    private static final int CONNECTION_TIMEOUT = 30 * 1000;

    /** Socket read timeout in milliseconds **/
    private static final int SOCKET_READ_TIMEOUT = 15 * 60 * 1000;

    private int receiverPort;
    private String receiverHost;

    private Socket ipMatrixControlSocket;

    public IpMatrixConnection(String receiverHost) {
        this(receiverHost, null, null);
    }

    public IpMatrixConnection(String receiverHost, Integer ipMatrixControlPort) {
        this(receiverHost, ipMatrixControlPort, null);
    }

    public IpMatrixConnection(String receiverHost, Integer ipMatrixControlPort, Integer unitNumber) {
        this.receiverHost = receiverHost;
        this.receiverPort = ipMatrixControlPort != null && ipMatrixControlPort >= 1 ? ipMatrixControlPort
                : DEFAULT_IPCONTROL_PORT;
        this.unitNumber = unitNumber != null && unitNumber >= 1 && unitNumber <= 4 ? unitNumber
                : AudioAuthorityBindingConstants.DEFAULT_UNIT_NUMBER;
    }

    @Override
    protected void openConnection() throws IOException {
        ipMatrixControlSocket = new Socket();

        // Set this timeout to unblock a blocking read when no data is received. It is useful to check if the
        // reading thread has to be stopped (it implies a latency of SOCKET_READ_TIMEOUT at most before the
        // thread is really stopped)
        ipMatrixControlSocket.setSoTimeout(SOCKET_READ_TIMEOUT);

        // Connect to the AVR with a connection timeout.
        ipMatrixControlSocket.connect(new InetSocketAddress(receiverHost, receiverPort), CONNECTION_TIMEOUT);

        logger.debug("Connected to {}:{}", receiverHost, receiverPort);
    }

    @Override
    public boolean isConnected() {
        return ipMatrixControlSocket != null && ipMatrixControlSocket.isConnected()
                && !ipMatrixControlSocket.isClosed();
    }

    @Override
    public void close() {
        super.close();
        try {
            if (ipMatrixControlSocket != null) {
                ipMatrixControlSocket.close();
                ipMatrixControlSocket = null;
                logger.debug("Closed socket!");
            }
        } catch (IOException ioException) {
            logger.error("Closing connection throws an exception!", ioException);
        }
    }

    @Override
    public String getConnectionName() {
        return receiverHost + ":" + receiverPort;
    }

    @Override
    protected InputStream getInputStream() throws IOException {
        return ipMatrixControlSocket.getInputStream();
    }

    @Override
    protected OutputStream getOutputStream() throws IOException {
        return ipMatrixControlSocket.getOutputStream();
    }

}
